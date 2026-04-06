package dev.chev.fb_launcher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public final class FbLauncherExclusivity {

	private FbLauncherExclusivity() {}

	public static boolean areExclusive(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second) {
		if (FbLauncherEnchantments.isBurst(first) && FbLauncherEnchantments.isSustain(second)) {
			return true;
		}
		if (FbLauncherEnchantments.isSustain(first) && FbLauncherEnchantments.isBurst(second)) {
			return true;
		}
		if (matches(first, second, FbLauncherEnchantments.FORGE_HEART_KEY, FbLauncherEnchantments.QUENCH_KEY)) {
			return true;
		}
		if (matches(first, second, FbLauncherEnchantments.LEAN_BURN_KEY, FbLauncherEnchantments.PYRE_BOUNTY_KEY)) {
			return true;
		}
		return false;
	}

	private static boolean matches(RegistryEntry<Enchantment> a, RegistryEntry<Enchantment> b, RegistryKey<Enchantment> x, RegistryKey<Enchantment> y) {
		return (a.matchesKey(x) && b.matchesKey(y)) || (a.matchesKey(y) && b.matchesKey(x));
	}
}
