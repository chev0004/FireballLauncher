package dev.chev.fb_launcher;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface FbLaunchedFireball {

	void fb_launcher$initLauncherData(ServerPlayerEntity shooter, ItemStack launcherStack);

	int fb_launcher$surgicalLevel();

	boolean fb_launcher$ghostBolt();

	boolean fb_launcher$pyroclast();

	int fb_launcher$blastShellLevel();

	int fb_launcher$ironStanceLevel();
}
