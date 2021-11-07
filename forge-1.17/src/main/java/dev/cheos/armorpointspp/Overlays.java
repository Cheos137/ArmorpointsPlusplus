package dev.cheos.armorpointspp;

import static net.minecraftforge.client.gui.ForgeIngameGui.BOSS_HEALTH_ELEMENT;
import static net.minecraftforge.client.gui.ForgeIngameGui.HUD_TEXT_ELEMENT;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.ReflectionHelper;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IDataProvider;
import dev.cheos.armorpointspp.core.adapter.IMath;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.DataProviderImpl;
import dev.cheos.armorpointspp.impl.EnchantmentHelperImpl;
import dev.cheos.armorpointspp.impl.PoseStackImpl;
import dev.cheos.armorpointspp.impl.RendererImpl;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.client.gui.OverlayRegistry.OverlayEntry;

//the cool thing about this update is that i can just go in and edit vanilla stuff without worrying about breaking something - it'll never be my fault again
public class Overlays { // TODO: profiling minecraft.getProfiler().push("xxx"); TODO: make config value save category
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0;
	
	public static final IIngameOverlay PLAYER_HEALTH = OverlayRegistry.registerOverlayAbove(BOSS_HEALTH_ELEMENT, "Player Health",         Overlays::playerHealth),
								  ABSORPTION         = OverlayRegistry.registerOverlayAbove(PLAYER_HEALTH,       "Appp Absorption",       Overlays::absorption),
								  ARMOR_LEVEL        = OverlayRegistry.registerOverlayAbove(ABSORPTION,          "Armor Level",           Overlays::armorLevel),
								  RESISTANCE         = OverlayRegistry.registerOverlayAbove(ARMOR_LEVEL,         "Appp Resistance",       Overlays::resistance),
								  PROTECTION         = OverlayRegistry.registerOverlayAbove(RESISTANCE,          "Appp Protection",       Overlays::protection),
								  ARMOR_TOUGHNESS    = OverlayRegistry.registerOverlayAbove(PROTECTION,          "Appp Armor Toughness",  Overlays::armorToughness),
								  ARMOR_TEXT         = OverlayRegistry.registerOverlayAbove(HUD_TEXT_ELEMENT,    "Appp Armor Text",       Overlays::armorText),
								  HEALTH_TEXT        = OverlayRegistry.registerOverlayAbove(HUD_TEXT_ELEMENT,    "Appp Health Text",      Overlays::healthText),
								  DEBUG              = OverlayRegistry.registerOverlayTop(                       "Appp Debug",            Overlays::debug);
	
	public static void init() {
		// disable forge overlays
		OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, false);
		OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT, false);
		// try to unregister them
		unregister(ForgeIngameGui.PLAYER_HEALTH_ELEMENT);
		unregister(ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
		// try to override them
		try {
			Field health = ReflectionHelper.unfinalize(ReflectionHelper.findField(ForgeIngameGui.class, "PLAYER_HEALTH_ELEMENT"));
			health.set(null, PLAYER_HEALTH);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		try {
			Field armor = ReflectionHelper.unfinalize(ReflectionHelper.findField(ForgeIngameGui.class, "ARMOR_LEVEL_ELEMENT"));
			armor.set(null, ARMOR_LEVEL);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		// hopefully, they're completely replaced now
	}
	
	private static void playerHealth(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE))
			if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
				gui.renderHealth(screenWidth, screenHeight, poseStack);
		else {
			Components.HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
			gui.left_height += 10;
		}
	}
	
	private static void absorption(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSOPRTION.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	private static void armorLevel(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			Components.VANILLA_ARMOR.render(ctx);
		else Components.ARMOR.render(ctx);
		gui.left_height += 10;
	}
	
	private static void resistance(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.RESISTANCE.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void protection(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.PROTECTION.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorToughness(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.ARMOR_TEXT.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void healthText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Components.HEALTH_TEXT.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
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
				x, y);
	}
	
	
	private static int baseX(int width) {
		return width / 2 - 91;
	}
	
	private static int baseY(ForgeIngameGui gui, int height) {
		return height - gui.left_height;
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
