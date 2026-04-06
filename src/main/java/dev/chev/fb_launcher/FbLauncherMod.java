package dev.chev.fb_launcher;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class FbLauncherMod implements ModInitializer {

	public static final String MOD_ID = "fb_launcher";

	public static Item FIREBALL_LAUNCHER;

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(HeatSyncPayload.ID, HeatSyncPayload.CODEC);
		Identifier id = Identifier.of(MOD_ID, "fireball_launcher");
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
		FIREBALL_LAUNCHER = Registry.register(
			Registries.ITEM,
			key,
				new FireballLauncherItem(
				new Item.Settings()
					.maxDamage(FireballLauncherItem.MAX_DAMAGE)
					.enchantable(22)
					.rarity(Rarity.EPIC)
					.registryKey(key)
					.useItemPrefixedTranslationKey()
			)
		);
		HeatTracker.register();
		registerPyreBounty();
	}

	private static void registerPyreBounty() {
		ServerLivingEntityEvents.AFTER_DEATH.register(FbLauncherMod::onLivingDeath);
	}

	private static void onLivingDeath(LivingEntity entity, DamageSource damageSource) {
		if (entity.getEntityWorld().isClient()) {
			return;
		}
		if (entity instanceof ServerPlayerEntity) {
			return;
		}
		Entity src = damageSource.getSource();
		if (!(src instanceof FireballEntity fb)) {
			return;
		}
		if (!(fb.getOwner() instanceof ServerPlayerEntity killer)) {
			return;
		}
		int level = 0;
		var main = killer.getMainHandStack();
		var off = killer.getOffHandStack();
		if (main.isOf(FIREBALL_LAUNCHER)) {
			level = Math.max(level, LauncherEnchantmentUtil.stackLevel(main, killer, FbLauncherEnchantments.PYRE_BOUNTY_KEY));
		}
		if (off.isOf(FIREBALL_LAUNCHER)) {
			level = Math.max(level, LauncherEnchantmentUtil.stackLevel(off, killer, FbLauncherEnchantments.PYRE_BOUNTY_KEY));
		}
		if (level <= 0) {
			return;
		}
		HeatTracker.relieveHeat(killer, 20 * level);
	}
}
