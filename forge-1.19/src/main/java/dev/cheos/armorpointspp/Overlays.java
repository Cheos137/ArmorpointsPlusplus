package dev.cheos.armorpointspp;

import static net.minecraftforge.client.gui.ForgeIngameGui.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.*;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.*;
import net.minecraftforge.client.gui.OverlayRegistry.OverlayEntry;

//the cool thing about this update is that i can just go in and edit vanilla stuff without worrying about breaking something - it'll never be my fault again
public class Overlays {
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	public static final IIngameOverlay MOUNT_HEALTH_ELEMENT = ForgeIngameGui.MOUNT_HEALTH_ELEMENT,
								  PLAYER_HEALTH      = OverlayRegistry.registerOverlayAbove(BOSS_HEALTH_ELEMENT,  "Player Health",           Overlays::playerHealth),
								  ABSORPTION         = OverlayRegistry.registerOverlayAbove(PLAYER_HEALTH,        "Appp Absorption",         Overlays::absorption),
								  ARMOR_LEVEL        = OverlayRegistry.registerOverlayAbove(ABSORPTION,           "Armor Level",             Overlays::armorLevel),
								  MAGIC_SHIELD       = OverlayRegistry.registerOverlayAbove(ARMOR_LEVEL,          "Appp Magic Shield",       Overlays::magicShield),
								  RESISTANCE         = OverlayRegistry.registerOverlayAbove(MAGIC_SHIELD,         "Appp Resistance",         Overlays::resistance),
								  PROTECTION         = OverlayRegistry.registerOverlayAbove(RESISTANCE,           "Appp Protection",         Overlays::protection),
								  ARMOR_TOUGHNESS    = OverlayRegistry.registerOverlayAbove(MOUNT_HEALTH_ELEMENT, "Appp Armor Toughness",    Overlays::armorToughness),
								  ARMOR_TOUGHNESS_OV = OverlayRegistry.registerOverlayAbove(PROTECTION,           "Appp Armor Toughness OV", Overlays::armorToughnessOv),
								  ARMOR_TEXT         = OverlayRegistry.registerOverlayAbove(HUD_TEXT_ELEMENT,     "Appp Armor Text",         Overlays::armorText),
								  HEALTH_TEXT        = OverlayRegistry.registerOverlayAbove(HUD_TEXT_ELEMENT,     "Appp Health Text",        Overlays::healthText),
								  TOUGHNESS_TEXT     = OverlayRegistry.registerOverlayAbove(HUD_TEXT_ELEMENT,     "Appp Toughness Text",     Overlays::toughnessText),
								  DEBUG              = OverlayRegistry.registerOverlayTop(                        "Appp Debug",              Overlays::debug);
	
	public static void init() {
		// disable forge / vanilla overlays
		OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
		// try to unregister them
		unregister(ForgeIngameGui.PLAYER_HEALTH_ELEMENT);
		unregister(ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
		// try to override them
		try { // TODO mixin for this
			Field health = ReflectionHelper.unfinalize(ReflectionHelper.findField(ForgeIngameGui.class, "PLAYER_HEALTH_ELEMENT"));
			health.set(null, PLAYER_HEALTH);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		try { // TODO mixin for this
			Field armor = ReflectionHelper.unfinalize(ReflectionHelper.findField(ForgeIngameGui.class, "ARMOR_LEVEL_ELEMENT"));
			armor.set(null, ARMOR_LEVEL);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		// hopefully, they're completely replaced now
	}
	
	private static void playerHealth(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
				gui.renderHealth(screenWidth, screenHeight, poseStack);
		} else if (Components.HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY)))
			gui.left_height += 10;
	}
	
	private static void absorption(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSOPRTION.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	private static void armorLevel(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) gui.left_height += 10;
	}
	
	private static void armorToughness(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(poseStack, baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
			switch (side) {
				case LEFT:
					gui.left_height += 10;
					break;
				case RIGHT:
					gui.right_height += 10;
					break;
			}
	}
	
	private static void resistance(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.RESISTANCE.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void protection(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.PROTECTION.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorToughnessOv(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_OVER.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void magicShield(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.MAGIC_SHIELD.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ARMOR_TEXT.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void healthText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.HEALTH_TEXT.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	private static void toughnessText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_TEXT.render(ctx(poseStack, baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	private static void debug(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY);
		Components.DEBUG.render(ctx);
		Components.DEBUG_TEXT.render(ctx);
	}
	
	private static RenderContext ctx(PoseStack poseStack, int x, int y) {
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
	
	
	private static int baseX(int width) {
		return baseX(width, Side.LEFT);
	}
	
	private static int baseX(int width, Side side) {
		return width / 2 + switch (side) { case LEFT -> -91; case RIGHT -> 10; };
	}
	
	private static int baseY(ForgeIngameGui gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(ForgeIngameGui gui, int height, Side side) {
		return height - switch (side) { case LEFT -> gui.left_height; case RIGHT -> gui.right_height; };
	}
	
	private static void unregister(IIngameOverlay overlay) {
		try {
			Map<IIngameOverlay, OverlayEntry> entries = ReflectionHelper.getPrivateValue(OverlayRegistry.class, "overlays");
			List<OverlayEntry> overlaysOrdered = ReflectionHelper.getPrivateValue(OverlayRegistry.class, "overlaysOrdered");
			OverlayEntry entry = entries.remove(overlay);
			if (entry != null)
				overlaysOrdered.remove(entry);
		} catch (Exception e) {
			Armorpointspp.LOGGER.error("Unable to unregister ingame overlay:", e);
		}
	}
}
