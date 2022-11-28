package dev.cheos.armorpointspp;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.*;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import dev.cheos.armorpointspp.render.VanillaHealthComponent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class Overlays {
	private static final IRenderComponent VANILLA_HEALTH = new VanillaHealthComponent();
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	static void updateHealthY(ForgeIngameGui gui, int screenHeight) { lastHealthY = baseY(gui, screenHeight); }
	static void updateArmorY(ForgeIngameGui gui, int screenHeight) { lastArmorY = baseY(gui, screenHeight); }
	static void updateToughnessY(ForgeIngameGui gui, int screenHeight) { lastToughnessY = baseY(gui, screenHeight, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)); }
	
	static void playerHealth(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && DATA_PROVIDER.shouldDrawSurvivalElements())
				VANILLA_HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
		} else if (Components.HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY)))
			ForgeIngameGui.left_height += 10;
	}
	
	static void absorption(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSORPTION.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static void absorptionOv(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSORPTION_OVER.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static void armorLevel(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) ForgeIngameGui.left_height += 10;
	}
	
	static void armorToughness(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(poseStack, baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
			switch (side) {
				case LEFT:
					ForgeIngameGui.left_height += 10;
					break;
				case RIGHT:
					ForgeIngameGui.right_height += 10;
					break;
			}
	}
	
	static void resistance(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.RESISTANCE.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static void protection(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.PROTECTION.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static void armorToughnessOv(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_OVER.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static void magicShield(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.MAGIC_SHIELD.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static void armorText(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ARMOR_TEXT.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static void healthText(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.HEALTH_TEXT.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static void toughnessText(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_TEXT.render(ctx(poseStack, baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	static void debug(ForgeIngameGui gui, MatrixStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY);
		Components.DEBUG.render(ctx);
		Components.DEBUG_TEXT.render(ctx);
	}
	
	public static RenderContext ctx(MatrixStack poseStack, int x, int y) {
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
	
	private static int baseY(ForgeIngameGui gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(ForgeIngameGui gui, int height, Side side) {
		return height - (side == Side.LEFT ? ForgeIngameGui.left_height : ForgeIngameGui.right_height);
	}
}
