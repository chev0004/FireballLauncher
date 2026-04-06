package dev.chev.fb_launcher;

import net.minecraft.entity.player.PlayerEntity;

public final class LauncherHeat {

	private LauncherHeat() {}

	public static final int OVERHEAT_THRESHOLD = 160;
	public static final int HEAT_PER_SHOT = 10;
	public static final int OVERHEAT_DURATION_TICKS = 200;

	public static int overheatThreshold(PlayerEntity player) {
		int forge = LauncherEnchantmentUtil.level(player, FbLauncherEnchantments.FORGE_HEART_KEY);
		return OVERHEAT_THRESHOLD + forge * 50;
	}

	public static int heatDecayPerTick(PlayerEntity player) {
		return 1 + LauncherEnchantmentUtil.level(player, FbLauncherEnchantments.QUENCH_KEY);
	}

	public static int overheatDurationTicks(PlayerEntity player) {
		int q = LauncherEnchantmentUtil.level(player, FbLauncherEnchantments.QUENCH_KEY);
		return Math.max(40, OVERHEAT_DURATION_TICKS - q * 50);
	}
}
