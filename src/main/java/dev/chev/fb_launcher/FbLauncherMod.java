package dev.chev.fb_launcher;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;

public final class FbLauncherMod implements ModInitializer {

	public static final String MOD_ID = "fb_launcher";

	public static Item FIREBALL_LAUNCHER;

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);
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
		PolymerItem.registerOverlay(FIREBALL_LAUNCHER, (PolymerItem) FIREBALL_LAUNCHER);
		HeatTracker.register();
		DynamicRegistrySetupCallback.EVENT.register(registryView -> registryView.registerEntryAdded(
			RegistryKeys.ENCHANTMENT,
			(rawId, enchantmentId, enchantment) -> {
				if (!enchantmentId.getNamespace().equals(MOD_ID)) {
					return;
				}
				registryView.getOptional(RegistryKeys.ENCHANTMENT).ifPresent(reg -> RegistrySyncUtils.setServerEntry(reg, enchantment));
			}
		));
	}
}
