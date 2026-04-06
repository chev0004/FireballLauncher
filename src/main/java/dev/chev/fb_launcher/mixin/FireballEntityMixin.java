package dev.chev.fb_launcher.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import dev.chev.fb_launcher.FbLaunchedFireball;
import dev.chev.fb_launcher.FbLauncherEnchantments;
import dev.chev.fb_launcher.LauncherEnchantmentUtil;

@Mixin(FireballEntity.class)
public class FireballEntityMixin implements FbLaunchedFireball {

	@Unique
	private int fb_launcher$surgicalLevel;
	@Unique
	private boolean fb_launcher$ghostBolt;
	@Unique
	private boolean fb_launcher$pyroclast;
	@Unique
	private int fb_launcher$blastShellLevel;
	@Unique
	private int fb_launcher$ironStanceLevel;

	@Override
	public void fb_launcher$initLauncherData(ServerPlayerEntity shooter, ItemStack launcherStack) {
		this.fb_launcher$surgicalLevel = LauncherEnchantmentUtil.stackLevel(launcherStack, shooter, FbLauncherEnchantments.SURGICAL_FLAME_KEY);
		this.fb_launcher$ghostBolt = LauncherEnchantmentUtil.stackLevel(launcherStack, shooter, FbLauncherEnchantments.GHOST_BOLT_KEY) > 0;
		this.fb_launcher$pyroclast = LauncherEnchantmentUtil.stackLevel(launcherStack, shooter, FbLauncherEnchantments.PYROCLAST_KEY) > 0;
		this.fb_launcher$blastShellLevel = LauncherEnchantmentUtil.stackLevel(launcherStack, shooter, FbLauncherEnchantments.BLAST_SHELL_KEY);
		this.fb_launcher$ironStanceLevel = LauncherEnchantmentUtil.stackLevel(launcherStack, shooter, FbLauncherEnchantments.IRON_STANCE_KEY);
	}

	@Override
	public int fb_launcher$surgicalLevel() {
		return this.fb_launcher$surgicalLevel;
	}

	@Override
	public boolean fb_launcher$ghostBolt() {
		return this.fb_launcher$ghostBolt;
	}

	@Override
	public boolean fb_launcher$pyroclast() {
		return this.fb_launcher$pyroclast;
	}

	@Override
	public int fb_launcher$blastShellLevel() {
		return this.fb_launcher$blastShellLevel;
	}

	@Override
	public int fb_launcher$ironStanceLevel() {
		return this.fb_launcher$ironStanceLevel;
	}

	@Inject(method = "writeCustomData", at = @At("TAIL"))
	private void fb_launcher$writeExtra(WriteView view, CallbackInfo ci) {
		view.putInt("fb_launcher_surgical", this.fb_launcher$surgicalLevel);
		view.putBoolean("fb_launcher_ghost", this.fb_launcher$ghostBolt);
		view.putBoolean("fb_launcher_pyro", this.fb_launcher$pyroclast);
		view.putInt("fb_launcher_blast_shell", this.fb_launcher$blastShellLevel);
		view.putInt("fb_launcher_iron", this.fb_launcher$ironStanceLevel);
	}

	@Inject(method = "readCustomData", at = @At("TAIL"))
	private void fb_launcher$readExtra(ReadView view, CallbackInfo ci) {
		this.fb_launcher$surgicalLevel = view.getInt("fb_launcher_surgical", 0);
		this.fb_launcher$ghostBolt = view.getBoolean("fb_launcher_ghost", false);
		this.fb_launcher$pyroclast = view.getBoolean("fb_launcher_pyro", false);
		this.fb_launcher$blastShellLevel = view.getInt("fb_launcher_blast_shell", 0);
		this.fb_launcher$ironStanceLevel = view.getInt("fb_launcher_iron", 0);
	}
}
