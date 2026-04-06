package dev.chev.fb_launcher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

public final class FbLauncherExclusivity {

	private FbLauncherExclusivity() {}

	public static boolean areExclusive(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second) {
		return (isDestructionExclusive(first) && isDamageExclusive(second))
			|| (isDamageExclusive(first) && isDestructionExclusive(second));
	}

	private static boolean isDestructionExclusive(RegistryEntry<Enchantment> e) {
		return FbLauncherEnchantments.isDestruction(e) && !FbLauncherEnchantments.isDamage(e);
	}

	private static boolean isDamageExclusive(RegistryEntry<Enchantment> e) {
		return FbLauncherEnchantments.isDamage(e) && !FbLauncherEnchantments.isDestruction(e);
	}
}
