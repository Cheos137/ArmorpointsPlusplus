package dev.cheos.armorpointspp;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.Side;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import net.minecraft.client.Minecraft;

public class Overlays {
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	static boolean playerHealth(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && DATA_PROVIDER.shouldDrawSurvivalElements()) {
				gui.renderHealth(poseStack);
				return true;
			}
		} else if (Components.HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY))) {
			gui.leftHeight += 10;
			return true;
		}
		return false;
	}
	
	static boolean absorption(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ABSOPRTION.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static boolean armorLevel(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) gui.leftHeight += 10;
		return flag;
	}
	
	static boolean armorToughness(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(poseStack, baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
			switch (side) {
				case LEFT:
					gui.leftHeight += 10;
					return true;
				case RIGHT:
					gui.rightHeight += 10;
					return true;
			}
		return false;
	}
	
	static boolean resistance(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.RESISTANCE.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean protection(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.PROTECTION.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean armorToughnessOv(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.TOUGHNESS_OVER.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean magicShield(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.MAGIC_SHIELD.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean armorText(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ARMOR_TEXT.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean healthText(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.HEALTH_TEXT.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static boolean toughnessText(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.TOUGHNESS_TEXT.render(ctx(poseStack, baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	static boolean debug(ApppGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY);
		return Components.DEBUG.render(ctx) &
			   Components.DEBUG_TEXT.render(ctx);
	}
	
	public static RenderContext ctx(PoseStack poseStack, int x, int y) {
		return new RenderContext(
				ApppConfig.instance(),
				DATA_PROVIDER,
				EnchantmentHelperImpl.INSTANCE,
				IMath.INSTANCE,
				RENDERER,
				new PoseStackImpl(poseStack),
				PROFILER,
				x, y);
	}
	
	static void cleanup() {
		RENDERER.setupVanilla();
	}
	
	
	private static int baseX(int width) {
		return baseX(width, Side.LEFT);
	}
	
	private static int baseX(int width, Side side) {
		return width / 2 + (side == Side.LEFT ? -91 : 10);
	}
	
	private static int baseY(ApppGui gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(ApppGui gui, int height, Side side) {
		return height - (side == Side.LEFT ? gui.leftHeight : gui.rightHeight);
	}
}
