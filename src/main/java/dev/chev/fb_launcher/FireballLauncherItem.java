package dev.chev.fb_launcher;

import java.util.function.Consumer;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import eu.pb4.polymer.core.api.item.PolymerItem;
import xyz.nucleoid.packettweaker.PacketContext;

public final class FireballLauncherItem extends Item implements PolymerItem {

	public static final int MAX_DAMAGE = 120;
	public static final int GUNPOWDER_PER_SHOT = 6;
	public static final int FIREBALL_EXPLOSION_POWER = 2;
	private static final String TOOLTIP_ROOT = "item.fb_launcher.fireball_launcher.";
	private static final int TOOLTIP_STAT_GREEN = 0x00A800;

	public FireballLauncherItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
		return Items.STICK;
	}

	@Override
	public Identifier getPolymerItemModel(ItemStack itemStack, PacketContext context) {
		return Identifier.of(FbLauncherMod.MOD_ID, "item/fireball_launcher");
	}

	private static Text statLine(String suffix, Object... args) {
		return Text.translatable(TOOLTIP_ROOT + suffix, args).styled(s -> s.withColor(TOOLTIP_STAT_GREEN));
	}

	@Override
	public void appendTooltip(
		ItemStack stack,
		TooltipContext context,
		TooltipDisplayComponent display,
		Consumer<Text> textConsumer,
		TooltipType type
	) {
		super.appendTooltip(stack, context, display, textConsumer, type);
		textConsumer.accept(Text.empty());
		textConsumer.accept(Text.translatable(TOOLTIP_ROOT + "stats_header").formatted(Formatting.GRAY));
		textConsumer.accept(statLine("gunpowder_cost", GUNPOWDER_PER_SHOT));
		textConsumer.accept(statLine("explosion_power", FIREBALL_EXPLOSION_POWER));
		textConsumer.accept(statLine("heat_per_shot", LauncherHeat.HEAT_PER_SHOT));
		textConsumer.accept(statLine("overheat_at", LauncherHeat.OVERHEAT_THRESHOLD));
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
		ItemStack launcherStack = sp.getStackInHand(hand);
		if (!sp.getAbilities().creativeMode && launcherStack.isDamageable() && launcherStack.getDamage() >= launcherStack.getMaxDamage()) {
			return ActionResult.FAIL;
		}
		if (!tryConsumeGunpowder(sp, launcherStack)) {
			world.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0f, 1.0f);
			return ActionResult.FAIL;
		}
		Vec3d look = sp.getRotationVec(1.0f);
		Vec3d motion = look.multiply(1.5);
		int hellfire = LauncherEnchantmentUtil.stackLevel(launcherStack, sp, FbLauncherEnchantments.HELLFIRE_KEY);
		int explosionPower = FIREBALL_EXPLOSION_POWER + hellfire;
		FireballEntity fireball = new FireballEntity(serverWorld, sp, motion, explosionPower);
		Vec3d spawn = sp.getEyePos().add(look.multiply(0.35));
		fireball.setPosition(spawn);
		((FbLaunchedFireball) fireball).fb_launcher$initLauncherData(sp, launcherStack);
		serverWorld.spawnEntity(fireball);
		world.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.6f, 1.5f);
		HeatTracker.addHeatFromShot(sp);
		if (!sp.getAbilities().creativeMode) {
			EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			launcherStack.damage(1, sp, slot);
		}
		return ActionResult.SUCCESS;
	}

	private static boolean tryConsumeGunpowder(ServerPlayerEntity player, ItemStack launcherStack) {
		if (player.getAbilities().creativeMode) {
			return true;
		}
		int cost = gunpowderPerShot(launcherStack, player);
		PlayerInventory inv = player.getInventory();
		int available = 0;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty() && stack.isOf(Items.GUNPOWDER)) {
				available += stack.getCount();
			}
		}
		if (available < cost) {
			return false;
		}
		int remaining = cost;
		for (int i = 0; i < inv.size() && remaining > 0; i++) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty() && stack.isOf(Items.GUNPOWDER)) {
				int take = Math.min(stack.getCount(), remaining);
				stack.decrement(take);
				remaining -= take;
			}
		}
		return remaining == 0;
	}

	private static int gunpowderPerShot(ItemStack launcherStack, ServerPlayerEntity player) {
		int lean = LauncherEnchantmentUtil.stackLevel(launcherStack, player, FbLauncherEnchantments.LEAN_BURN_KEY);
		return Math.max(1, GUNPOWDER_PER_SHOT - lean);
	}
}
