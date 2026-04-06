package dev.chev.fb_launcher;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HeatSyncPayload(int heat, int overheatedTicks) implements CustomPayload {

	public static final CustomPayload.Id<HeatSyncPayload> ID = new CustomPayload.Id<>(Identifier.of(FbLauncherMod.MOD_ID, "heat_sync"));
	public static final PacketCodec<RegistryByteBuf, HeatSyncPayload> CODEC = CustomPayload.codecOf(HeatSyncPayload::write, HeatSyncPayload::read);

	private static HeatSyncPayload read(RegistryByteBuf buf) {
		return new HeatSyncPayload(buf.readVarInt(), buf.readVarInt());
	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(heat);
		buf.writeVarInt(overheatedTicks);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
