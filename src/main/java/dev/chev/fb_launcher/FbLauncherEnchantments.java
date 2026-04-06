package dev.chev.fb_launcher;

import net.minecraft.enchantment.Enchantment;
import java.util.List;
import java.util.Optional;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class FbLauncherEnchantments {

	public static final RegistryKey<Enchantment> HELLFIRE_KEY = key("hellfire");
	public static final RegistryKey<Enchantment> LEAN_BURN_KEY = key("lean_burn");
	public static final RegistryKey<Enchantment> BLAST_SHELL_KEY = key("blast_shell");
	public static final RegistryKey<Enchantment> GHOST_BOLT_KEY = key("ghost_bolt");
	public static final RegistryKey<Enchantment> SURGICAL_FLAME_KEY = key("surgical_flame");
	public static final RegistryKey<Enchantment> PYROCLAST_KEY = key("pyroclast");
	public static final RegistryKey<Enchantment> IRON_STANCE_KEY = key("iron_stance");

	private static final List<RegistryKey<Enchantment>> LAUNCHER_ENCHANTMENT_KEYS = List.of(
		HELLFIRE_KEY,
		LEAN_BURN_KEY,
		BLAST_SHELL_KEY,
		GHOST_BOLT_KEY,
		SURGICAL_FLAME_KEY,
		PYROCLAST_KEY,
		IRON_STANCE_KEY
	);

	private FbLauncherEnchantments() {}

	private static RegistryKey<Enchantment> key(String path) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(FbLauncherMod.MOD_ID, path));
	}

	public static boolean isDestruction(RegistryEntry<Enchantment> e) {
		return e.matchesKey(HELLFIRE_KEY)
			|| e.matchesKey(BLAST_SHELL_KEY)
			|| e.matchesKey(GHOST_BOLT_KEY)
			|| e.matchesKey(PYROCLAST_KEY)
			|| e.matchesKey(IRON_STANCE_KEY);
	}

	public static boolean isDamage(RegistryEntry<Enchantment> e) {
		return e.matchesKey(BLAST_SHELL_KEY)
			|| e.matchesKey(GHOST_BOLT_KEY)
			|| e.matchesKey(PYROCLAST_KEY)
			|| e.matchesKey(IRON_STANCE_KEY)
			|| e.matchesKey(SURGICAL_FLAME_KEY);
	}

	public static boolean isLauncherEnchantment(RegistryEntry<Enchantment> e) {
		for (RegistryKey<Enchantment> k : LAUNCHER_ENCHANTMENT_KEYS) {
			if (e.matchesKey(k)) {
				return true;
			}
		}
		return false;
	}

	public static Optional<String> launcherEnchantDescPath(RegistryEntry<Enchantment> e) {
		for (RegistryKey<Enchantment> k : LAUNCHER_ENCHANTMENT_KEYS) {
			if (e.matchesKey(k)) {
				return Optional.of(k.getValue().getPath());
			}
		}
		return Optional.empty();
	}
}
