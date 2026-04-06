package dev.chev.fb_launcher.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;

import dev.chev.fb_launcher.FbLaunchedFireball;

@Mixin(ExplosiveProjectileEntity.class)
public class ExplosiveProjectileEntityMixin {

	@Inject(method = "canHit", at = @At("RETURN"), cancellable = true)
	private void fb_launcher$ghostBolt(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) {
			return;
		}
		if (!(entity instanceof FireballEntity other)) {
			return;
		}
		ExplosiveProjectileEntity self = (ExplosiveProjectileEntity) (Object) this;
		if (!(self instanceof FireballEntity selfFb)) {
			return;
		}
		if (!Objects.equals(selfFb.getOwner(), other.getOwner())) {
			return;
		}
		FbLaunchedFireball a = (FbLaunchedFireball) selfFb;
		FbLaunchedFireball b = (FbLaunchedFireball) other;
		if (!(a.fb_launcher$ghostBolt() || b.fb_launcher$ghostBolt())) {
			return;
		}
		cir.setReturnValue(false);
	}

	@Inject(method = "getDragInWater", at = @At("HEAD"), cancellable = true)
	private void fb_launcher$pyroclast(CallbackInfoReturnable<Float> cir) {
		ExplosiveProjectileEntity self = (ExplosiveProjectileEntity) (Object) this;
		if (!(self instanceof FireballEntity fb)) {
			return;
		}
		if (!((FbLaunchedFireball) fb).fb_launcher$pyroclast()) {
			return;
		}
		cir.setReturnValue(0.95F);
	}
}
