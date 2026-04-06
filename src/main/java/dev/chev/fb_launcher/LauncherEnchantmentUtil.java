package dev.chev.fb_launcher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public final class LauncherEnchantmentUtil {

	private LauncherEnchantmentUtil() {}

	public static int stackLevel(ItemStack stack, PlayerEntity player, RegistryKey<Enchantment> key) {
		if (!stack.isOf(FbLauncherMod.FIREBALL_LAUNCHER)) {
			return 0;
		}
		var reg = player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
		Enchantment value = reg.getValueOrThrow(key);
		RegistryEntry<Enchantment> entry = reg.getEntry(value);
		return EnchantmentHelper.getLevel(entry, stack);
	}

	public static int level(PlayerEntity player, RegistryKey<Enchantment> key) {
		var reg = player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
		Enchantment value = reg.getValueOrThrow(key);
		RegistryEntry<Enchantment> entry = reg.getEntry(value);
		int m = 0;
		int o = 0;
		ItemStack main = player.getMainHandStack();
		ItemStack off = player.getOffHandStack();
		if (main.isOf(FbLauncherMod.FIREBALL_LAUNCHER)) {
			m = EnchantmentHelper.getLevel(entry, main);
		}
		if (off.isOf(FbLauncherMod.FIREBALL_LAUNCHER)) {
			o = EnchantmentHelper.getLevel(entry, off);
		}
		return Math.max(m, o);
	}
}
