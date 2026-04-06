package dev.chev.fb_launcher;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class HeatTracker {

	private static final PlayerHeatData ZERO = new PlayerHeatData(0, 0);
	private static final Map<UUID, PlayerHeatData> DATA = new HashMap<>();
	private static final Map<UUID, PlayerHeatData> LAST_SYNCED = new HashMap<>();

	private HeatTracker() {}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				tickPlayer(player);
			}
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				syncHeatToClient(player);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			UUID id = handler.player.getUuid();
			DATA.remove(id);
			LAST_SYNCED.remove(id);
		});
	}

	public static boolean isOverheated(UUID id) {
		return DATA.getOrDefault(id, ZERO).overheatedTicks() > 0;
	}

	public static void addHeatFromShot(ServerPlayerEntity player) {
		UUID id = player.getUuid();
		PlayerHeatData d = DATA.getOrDefault(id, ZERO);
		if (d.overheatedTicks() > 0) {
			return;
		}
		int heat = d.heat() + LauncherHeat.HEAT_PER_SHOT;
		if (heat >= LauncherHeat.overheatThreshold(player)) {
			triggerOverheat(player);
			return;
		}
		DATA.put(id, new PlayerHeatData(heat, 0));
	}

	private static void triggerOverheat(ServerPlayerEntity player) {
		UUID id = player.getUuid();
		DATA.put(id, new PlayerHeatData(0, LauncherHeat.overheatDurationTicks(player)));
		player.networkHandler.sendPacket(new ClearTitleS2CPacket(false));
		sendTitle(
			player,
			Text.translatable("fb_launcher.title.overheat").formatted(Formatting.RED, Formatting.BOLD),
			Text.translatable("fb_launcher.subtitle.overheat"),
			10,
			70,
			20
		);
		player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.PLAYERS, 1.0f, 0.5f);
		player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1.0f, 0.8f);
	}

	private static void tickPlayer(ServerPlayerEntity player) {
		UUID id = player.getUuid();
		PlayerHeatData d = DATA.getOrDefault(id, ZERO);
		int heat = d.heat();
		int oh = d.overheatedTicks();

		if (oh > 0) {
			if (oh == 1) {
				player.networkHandler.sendPacket(new ClearTitleS2CPacket(false));
				sendTitle(
					player,
					Text.translatable("fb_launcher.title.ready").formatted(Formatting.GREEN, Formatting.BOLD),
					Text.translatable("fb_launcher.subtitle.ready"),
					5,
					40,
					10
				);
				player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 1.5f);
				if (heat > 0) {
					DATA.put(id, new PlayerHeatData(heat, 0));
				} else {
					DATA.remove(id);
				}
				return;
			}
			int seconds = (oh + 19) / 20;
			player.sendMessage(Text.translatable("fb_launcher.hud.overheat", seconds).formatted(Formatting.RED), true);
			DATA.put(id, new PlayerHeatData(heat, oh - 1));
			return;
		}

		if (heat > 0) {
			int decay = LauncherHeat.heatDecayThisTick(player);
			int next = heat > decay ? heat - decay : 0;
			if (next > 0) {
				DATA.put(id, new PlayerHeatData(next, 0));
			} else {
				DATA.remove(id);
			}
		}
	}

	private static void sendTitle(ServerPlayerEntity player, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
		player.networkHandler.sendPacket(new TitleFadeS2CPacket(fadeIn, stay, fadeOut));
		player.networkHandler.sendPacket(new TitleS2CPacket(title));
		player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
	}

	private static void syncHeatToClient(ServerPlayerEntity player) {
		UUID id = player.getUuid();
		PlayerHeatData current = DATA.getOrDefault(id, ZERO);
		PlayerHeatData last = LAST_SYNCED.get(id);
		if (current.heat() == 0 && current.overheatedTicks() == 0) {
			if (last == null) {
				return;
			}
			ServerPlayNetworking.send(player, new HeatSyncPayload(0, 0));
			LAST_SYNCED.remove(id);
			return;
		}
		if (current.equals(last)) {
			return;
		}
		ServerPlayNetworking.send(player, new HeatSyncPayload(current.heat(), current.overheatedTicks()));
		LAST_SYNCED.put(id, current);
	}

	private record PlayerHeatData(int heat, int overheatedTicks) {}
}
