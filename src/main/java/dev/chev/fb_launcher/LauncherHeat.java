package dev.chev.fb_launcher;

import net.minecraft.entity.player.PlayerEntity;

public final class LauncherHeat {

	private LauncherHeat() {}

	public static final int OVERHEAT_THRESHOLD = 160;
	public static final int HEAT_PER_SHOT = 10;
	public static final int OVERHEAT_DURATION_TICKS = 200;

	public static int overheatThreshold(PlayerEntity player) {
		return OVERHEAT_THRESHOLD;
	}

	public static int heatDecayPerTick(PlayerEntity player) {
		return 1;
	}

	public static int overheatDurationTicks(PlayerEntity player) {
		return OVERHEAT_DURATION_TICKS;
	}
}
