package dev.chev.fb_launcher.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;

import dev.chev.fb_launcher.FbLaunchedFireball;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private float fb_launcher$blastShell(float amount, ServerWorld world, DamageSource source) {
		LivingEntity self = (LivingEntity) (Object) this;
		Entity src = source.getSource();
		if (!(src instanceof FireballEntity fb)) {
			return amount;
		}
		if (fb.getOwner() != self) {
			return amount;
		}
		int level = ((FbLaunchedFireball) fb).fb_launcher$blastShellLevel();
		if (level <= 0) {
			return amount;
		}
		return amount * Math.max(0.25f, 1f - 0.15f * level);
	}
}
