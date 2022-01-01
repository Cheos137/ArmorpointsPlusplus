package dev.cheos.armorpointspp;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.Side;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.adapter.IDataProvider;
import dev.cheos.armorpointspp.core.adapter.IMath;
import dev.cheos.armorpointspp.core.adapter.IProfiler;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;

public class Overlays {
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getMinecraft();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	static void playerHealth(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.gameSettings.hideGUI && DATA_PROVIDER.shouldDrawSurvivalElements())
				gui.renderHealth(screenWidth, screenHeight);
		} else if (Components.HEALTH.render(ctx(baseX(screenWidth), lastHealthY)))
			GuiIngameForge.left_height += 10;
		
	}
	
	static void absorption(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSOPRTION.render(ctx(baseX(screenWidth), lastHealthY));
	}
	
	static void armorLevel(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) GuiIngameForge.left_height += 10;
	}
	
	static void armorToughness(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
			switch (side) {
				case LEFT:
					GuiIngameForge.left_height += 10;
					break;
				case RIGHT:
					GuiIngameForge.right_height += 10;
					break;
			}
	}
	
	static void resistance(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.RESISTANCE.render(ctx(baseX(screenWidth), lastArmorY));
	}
	
	static void protection(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.PROTECTION.render(ctx(baseX(screenWidth), lastArmorY));
	}
	
	static void armorToughnessOv(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_OVER.render(ctx(baseX(screenWidth), lastArmorY));
	}
	
	static void armorText(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.ARMOR_TEXT.render(ctx(baseX(screenWidth), lastArmorY));
	}
	
	static void healthText(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.HEALTH_TEXT.render(ctx(baseX(screenWidth), lastHealthY));
	}
	
	static void toughnessText(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_TEXT.render(ctx(baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	static void debug(GuiIngameForge gui, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(baseX(screenWidth), lastArmorY);
		Components.DEBUG.render(ctx);
		Components.DEBUG_TEXT.render(ctx);
	}
	
	private static RenderContext ctx(int x, int y) {
		return new RenderContext(
				ApppConfig.instance(),
				DATA_PROVIDER,
				EnchantmentHelperImpl.INSTANCE,
				IMath.INSTANCE,
				RENDERER,
				new PoseStackImpl(),
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
	
	private static int baseY(GuiIngameForge gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(GuiIngameForge gui, int height, Side side) {
		return height - (side == Side.LEFT ? GuiIngameForge.left_height : GuiIngameForge.right_height);
	}
}
