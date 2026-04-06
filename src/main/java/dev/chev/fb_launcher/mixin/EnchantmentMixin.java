package dev.chev.fb_launcher.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

import dev.chev.fb_launcher.FbLauncherExclusivity;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

	@Inject(method = "canBeCombined", at = @At("RETURN"), cancellable = true)
	private static void fb_launcher$launcherExclusivity(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			return;
		}
		if (FbLauncherExclusivity.areExclusive(first, second)) {
			cir.setReturnValue(false);
		}
	}
}
