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
	public static final RegistryKey<Enchantment> FORGE_HEART_KEY = key("forge_heart");
	public static final RegistryKey<Enchantment> QUENCH_KEY = key("quench");
	public static final RegistryKey<Enchantment> LEAN_BURN_KEY = key("lean_burn");
	public static final RegistryKey<Enchantment> BLAST_SHELL_KEY = key("blast_shell");
	public static final RegistryKey<Enchantment> GHOST_BOLT_KEY = key("ghost_bolt");
	public static final RegistryKey<Enchantment> SCORCHED_HORIZON_KEY = key("scorched_horizon");
	public static final RegistryKey<Enchantment> SURGICAL_FLAME_KEY = key("surgical_flame");
	public static final RegistryKey<Enchantment> PYRE_BOUNTY_KEY = key("pyre_bounty");
	public static final RegistryKey<Enchantment> PYROCLAST_KEY = key("pyroclast");
	public static final RegistryKey<Enchantment> IRON_STANCE_KEY = key("iron_stance");

	private static final List<RegistryKey<Enchantment>> LAUNCHER_ENCHANTMENT_KEYS = List.of(
		HELLFIRE_KEY,
		FORGE_HEART_KEY,
		QUENCH_KEY,
		LEAN_BURN_KEY,
		BLAST_SHELL_KEY,
		GHOST_BOLT_KEY,
		SCORCHED_HORIZON_KEY,
		SURGICAL_FLAME_KEY,
		PYRE_BOUNTY_KEY,
		PYROCLAST_KEY,
		IRON_STANCE_KEY
	);

	private FbLauncherEnchantments() {}

	private static RegistryKey<Enchantment> key(String path) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(FbLauncherMod.MOD_ID, path));
	}

	public static boolean isBurst(RegistryEntry<Enchantment> e) {
		return e.matchesKey(HELLFIRE_KEY) || e.matchesKey(SCORCHED_HORIZON_KEY);
	}

	public static boolean isSustain(RegistryEntry<Enchantment> e) {
		return e.matchesKey(FORGE_HEART_KEY)
			|| e.matchesKey(QUENCH_KEY)
			|| e.matchesKey(LEAN_BURN_KEY)
			|| e.matchesKey(PYRE_BOUNTY_KEY);
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
