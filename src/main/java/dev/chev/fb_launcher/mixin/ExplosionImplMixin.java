package dev.chev.fb_launcher.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionImpl;

import dev.chev.fb_launcher.FbLaunchedFireball;

@Mixin(ExplosionImpl.class)
public class ExplosionImplMixin {

	@ModifyVariable(
		method = "explode",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/world/explosion/ExplosionImpl;getBlocksToDestroy()Ljava/util/List;"
		)
	)
	private List<BlockPos> fb_launcher$surgicalBlocks(List<BlockPos> list) {
		Explosion explosion = (Explosion) (Object) this;
		Entity entity = explosion.getEntity();
		if (!(entity instanceof FbLaunchedFireball fb)) {
			return list;
		}
		int level = fb.fb_launcher$surgicalLevel();
		if (level <= 0 || list.isEmpty()) {
			return list;
		}
		ServerWorld world = explosion.getWorld();
		float keep = Math.max(0.15f, 1f - 0.22f * level);
		List<BlockPos> out = new ArrayList<>(Math.max(8, list.size() / 2));
		for (BlockPos pos : list) {
			if (world.random.nextFloat() < keep) {
				out.add(pos);
			}
		}
		return out;
	}

	@Redirect(
		method = "damageEntities",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addVelocity(Lnet/minecraft/util/math/Vec3d;)V")
	)
	private void fb_launcher$ironStanceKnockback(Entity entity, Vec3d velocity) {
		ExplosionImpl self = (ExplosionImpl) (Object) this;
		Entity src = self.getEntity();
		if (src instanceof FireballEntity fb && entity instanceof PlayerEntity pe && fb.getOwner() == pe) {
			int iron = ((FbLaunchedFireball) fb).fb_launcher$ironStanceLevel();
			if (iron > 0) {
				float f = Math.max(0.2f, 1f - 0.2f * Math.min(iron, 4));
				entity.addVelocity(velocity.multiply(f));
				return;
			}
		}
		entity.addVelocity(velocity);
	}
}
