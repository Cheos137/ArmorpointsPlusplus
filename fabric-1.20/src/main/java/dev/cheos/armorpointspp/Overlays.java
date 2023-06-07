package dev.cheos.armorpointspp;

import dev.cheos.armorpointspp.compat.*;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.*;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class Overlays {
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	static void updateHealthY(ApppGui gui, int screenHeight) { lastHealthY = baseY(gui, screenHeight); }
	static void updateArmorY(ApppGui gui, int screenHeight) { lastArmorY = baseY(gui, screenHeight); }
	static void updateToughnessY(ApppGui gui, int screenHeight) { lastToughnessY = baseY(gui, screenHeight, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)); }
	
	static boolean playerHealth(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && DATA_PROVIDER.shouldDrawSurvivalElements()) {
				gui.renderHealth(graphics);
				return true;
			}
		} else if (Components.HEALTH.render(ctx(graphics, baseX(screenWidth), lastHealthY))) {
			gui.leftHeight += 10;
			return true;
		}
		return false;
	}
	
	static boolean absorption(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ABSORPTION.render(ctx(graphics, baseX(screenWidth), lastHealthY));
	}
	
	static boolean absorptionOv(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ABSORPTION_OVER.render(ctx(graphics, baseX(screenWidth), lastHealthY));
	}
	
	static void compat$victus(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		VictusCompat.render(gui, graphics, gui.minecraft.player, baseX(screenWidth));
	}
	
	static boolean armorLevel(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(graphics, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_HIDDEN)
				&& Compat.isDetailarmorbarLoaded()
				&& (ApppConfig.instance().enm(EnumOption.DETAILAB_COMPAT) == EnableState.ALWAYS
				|| (ApppConfig.instance().enm(EnumOption.DETAILAB_COMPAT) == EnableState.AUTO && gui.minecraft.player.getArmorValue() <= 20))) {
			DetailarmorbarSafeAccess.render(gui, graphics, gui.minecraft.player);
			flag = true;
		} else if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) gui.leftHeight += 10;
		return flag;
	}
	
	static boolean armorToughness(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(graphics, baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
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
	
	static boolean resistance(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.RESISTANCE.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	static boolean protection(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.PROTECTION.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	static boolean armorToughnessOv(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.TOUGHNESS_OVER.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	static boolean magicShield(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.MAGIC_SHIELD.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	static boolean armorText(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ARMOR_TEXT.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	static boolean healthText(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.HEALTH_TEXT.render(ctx(graphics, baseX(screenWidth), lastHealthY));
	}
	
	static boolean toughnessText(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		return Components.TOUGHNESS_TEXT.render(ctx(graphics, baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	static boolean debug(ApppGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(graphics, baseX(screenWidth), lastArmorY);
		return Components.DEBUG.render(ctx) &
			   Components.DEBUG_TEXT.render(ctx);
	}
	
	public static RenderContext ctx(GuiGraphics graphics, int x, int y) {
		return new RenderContext(
				ApppConfig.instance(),
				DATA_PROVIDER,
				EnchantmentHelperImpl.INSTANCE,
				IMath.INSTANCE,
				RENDERER,
				new PoseStackImpl(graphics),
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
