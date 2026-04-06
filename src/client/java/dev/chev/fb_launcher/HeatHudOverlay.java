package dev.chev.fb_launcher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public final class HeatHudOverlay {

	private static final Identifier XP_BAR_BACKGROUND = Identifier.ofVanilla("hud/experience_bar_background");
	private static final Identifier XP_BAR_PROGRESS = Identifier.ofVanilla("hud/experience_bar_progress");
	private static final float BAR_SCALE = 0.5F;
	private static final int VANILLA_BAR_TEX_W = 182;
	private static final int VANILLA_BAR_TEX_H = 5;
	private static final int GAP_ABOVE_ROW = 2;
	private static final int FOOD_ICON_SIZE = 9;
	private static final int MOUNT_ROW_STEP = 10;

	private HeatHudOverlay() {}

	public static void render(DrawContext context, RenderTickCounter counter) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;
		if (player == null || client.options.hudHidden) {
			return;
		}
		boolean holding = isHoldingLauncher(player);
		int h = ClientHeatState.heat();
		int oh = ClientHeatState.overheatedTicks();
		if (!holding && h <= 0 && oh <= 0) {
			return;
		}
		int sw = context.getScaledWindowWidth();
		int sh = context.getScaledWindowHeight();
		int screenBarW = Math.round(VANILLA_BAR_TEX_W * BAR_SCALE);
		int screenBarH = Math.round(VANILLA_BAR_TEX_H * BAR_SCALE);
		int barX = foodColumnRight(sw) - screenBarW;
		int barY = resolveBarY(client, player, sh, screenBarH);
		float fill;
		if (oh > 0) {
			fill = (float) oh / LauncherHeat.OVERHEAT_DURATION_TICKS;
			fill = MathHelper.clamp(fill, 0f, 1f);
		} else {
			fill = (float) h / LauncherHeat.OVERHEAT_THRESHOLD;
			fill = MathHelper.clamp(fill, 0f, 1f);
		}
		int progressTexW = (int) (fill * 183.0F);
		progressTexW = MathHelper.clamp(progressTexW, 0, VANILLA_BAR_TEX_W);
		context.getMatrices().pushMatrix();
		context.getMatrices().translate(barX, barY);
		context.getMatrices().scale(BAR_SCALE, BAR_SCALE);
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, XP_BAR_BACKGROUND, 0, 0, VANILLA_BAR_TEX_W, VANILLA_BAR_TEX_H);
		if (progressTexW > 0) {
			if (oh > 0) {
				context.drawGuiTexture(
					RenderPipelines.GUI_TEXTURED,
					XP_BAR_PROGRESS,
					VANILLA_BAR_TEX_W,
					VANILLA_BAR_TEX_H,
					0,
					0,
					0,
					0,
					progressTexW,
					VANILLA_BAR_TEX_H,
					ColorHelper.getArgb(255, 255, 160, 90)
				);
			} else {
				int argb = heatGradientArgb(fill);
				context.drawGuiTexture(
					RenderPipelines.GUI_TEXTURED,
					XP_BAR_PROGRESS,
					VANILLA_BAR_TEX_W,
					VANILLA_BAR_TEX_H,
					0,
					0,
					0,
					0,
					progressTexW,
					VANILLA_BAR_TEX_H,
					argb
				);
			}
		}
		context.getMatrices().popMatrix();
	}

	private static int foodColumnRight(int sw) {
		return sw / 2 + 91;
	}

	private static int statusRowTop(int sh) {
		return sh - 39;
	}

	private static LivingEntity riddenMount(PlayerEntity player) {
		Entity vehicle = player.getVehicle();
		return vehicle instanceof LivingEntity living ? living : null;
	}

	private static int mountHeartSlots(LivingEntity mount) {
		if (mount == null || !mount.isLiving()) {
			return 0;
		}
		int halfHearts = (int) (mount.getMaxHealth() + 0.5F) / 2;
		return Math.min(halfHearts, 30);
	}

	private static int mountRowCount(int heartSlots) {
		if (heartSlots <= 0) {
			return 0;
		}
		return (int) Math.ceil(heartSlots / 10.0);
	}

	private static int resolveBarY(MinecraftClient client, PlayerEntity player, int sh, int screenBarH) {
		int baseY = statusRowTop(sh);
		boolean statusBars = client.interactionManager.hasStatusBars();
		int mountHearts = mountHeartSlots(riddenMount(player));
		boolean hungerDrawn = statusBars && mountHearts == 0;
		if (hungerDrawn) {
			return baseY - GAP_ABOVE_ROW - screenBarH;
		}
		if (statusBars && mountHearts > 0) {
			int rows = mountRowCount(mountHearts);
			int mountTopY = baseY - (rows - 1) * MOUNT_ROW_STEP;
			return mountTopY - GAP_ABOVE_ROW - screenBarH;
		}
		return baseY + (FOOD_ICON_SIZE - screenBarH) / 2;
	}

	private static int heatGradientArgb(float heatRatio) {
		float t = MathHelper.clamp(heatRatio, 0f, 1f);
		int a = 0xFF55DD44;
		int b = 0xFFFFFF33;
		int c = 0xFFFF8811;
		int d = 0xFFFF2222;
		if (t < 1f / 3f) {
			return ColorHelper.lerp(t * 3f, a, b);
		}
		if (t < 2f / 3f) {
			return ColorHelper.lerp((t - 1f / 3f) * 3f, b, c);
		}
		return ColorHelper.lerp((t - 2f / 3f) * 3f, c, d);
	}

	private static boolean isHoldingLauncher(PlayerEntity player) {
		ItemStack main = player.getMainHandStack();
		ItemStack off = player.getOffHandStack();
		return main.isOf(FbLauncherMod.FIREBALL_LAUNCHER) || off.isOf(FbLauncherMod.FIREBALL_LAUNCHER);
	}
}
