package dev.chev.fb_launcher;

import java.util.List;
import java.util.Optional;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class LauncherEnchantTooltip {

	static final String DESC_ROOT = "item.fb_launcher.fireball_launcher.desc.";

	private LauncherEnchantTooltip() {}

	public static void insertEnchantmentDescriptions(ItemStack stack, List<Text> lines) {
		if (!stack.isOf(FbLauncherMod.FIREBALL_LAUNCHER)) {
			return;
		}
		ItemEnchantmentsComponent comp = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
		if (comp.isEmpty()) {
			return;
		}
		for (int i = 0; i < lines.size(); i++) {
			Text line = lines.get(i);
			String shown = line.getString();
			for (var holder : comp.getEnchantmentEntries()) {
				RegistryEntry<Enchantment> ench = holder.getKey();
				if (!FbLauncherEnchantments.isLauncherEnchantment(ench)) {
					continue;
				}
				int level = holder.getIntValue();
				Text name = Enchantment.getName(ench, level);
				if (!name.getString().equals(shown) && !line.equals(name)) {
					continue;
				}
				Optional<String> path = FbLauncherEnchantments.launcherEnchantDescPath(ench);
				if (path.isEmpty()) {
					continue;
				}
				lines.add(i + 1, Text.translatable(DESC_ROOT + path.get()).formatted(Formatting.DARK_GRAY));
				i++;
				break;
			}
		}
	}
}
