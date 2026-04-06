package dev.chev.fb_launcher;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class FireballLauncherItem extends Item {

	public FireballLauncherItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}
		ServerPlayerEntity sp = (ServerPlayerEntity) player;
		ServerWorld serverWorld = (ServerWorld) world;
		if (HeatTracker.isOverheated(sp.getUuid())) {
			return ActionResult.FAIL;
		}
		if (!tryConsumeGunpowder(sp)) {
			world.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0f, 1.0f);
			return ActionResult.FAIL;
		}
		Vec3d look = sp.getRotationVec(1.0f);
		Vec3d motion = look.multiply(1.5);
		FireballEntity fireball = new FireballEntity(serverWorld, sp, motion, 3);
		Vec3d spawn = sp.getEyePos().add(look.multiply(0.35));
		fireball.setPosition(spawn);
		serverWorld.spawnEntity(fireball);
		world.playSound(
			null,
			sp.getX(),
			sp.getY(),
			sp.getZ(),
			SoundEvents.ENTITY_GHAST_SHOOT,
			SoundCategory.PLAYERS,
			1.0f,
			(world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2f + 1.0f
		);
		HeatTracker.addHeatFromShot(sp);
		return ActionResult.SUCCESS;
	}

	private static boolean tryConsumeGunpowder(ServerPlayerEntity player) {
		if (player.getAbilities().creativeMode) {
			return true;
		}
		PlayerInventory inv = player.getInventory();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty() && stack.isOf(Items.GUNPOWDER)) {
				stack.decrement(1);
				return true;
			}
		}
		return false;
	}
}
