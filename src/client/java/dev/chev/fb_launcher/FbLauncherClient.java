package dev.chev.fb_launcher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class FbLauncherClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(HeatSyncPayload.ID, (payload, context) -> {
			ClientHeatState.update(payload.heat(), payload.overheatedTicks());
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientHeatState.clear());
		HudRenderCallback.EVENT.register(HeatHudOverlay::render);
		ItemTooltipCallback.EVENT.register(
			(stack, tooltipContext, tooltipType, lines) -> LauncherEnchantTooltip.insertEnchantmentDescriptions(stack, lines)
		);
	}
}
