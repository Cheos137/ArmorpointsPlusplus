package dev.cheos.armorpointspp.render;

import static net.minecraftforge.client.gui.ForgeIngameGui.BOSS_HEALTH_ELEMENT;
import static net.minecraftforge.client.gui.ForgeIngameGui.HUD_TEXT_ELEMENT;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.client.gui.OverlayRegistry.OverlayEntry;

//the cool thing about this update is that i can just go in and edit vanilla stuff without worrying about breaking something - it'll never be my fault again
public class Overlays { // TODO: profiling minecraft.getProfiler().push("xxx");
	private static final ResourceLocation ICONS      = new ResourceLocation(Armorpointspp.MODID, "textures/gui/icons.png");
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
//	private static final HUDRenderer hudRenderer     = new HUDRenderer();
//	private static final Minecraft minecraft         = Minecraft.getInstance();
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
			Field health = unfinalize(ForgeIngameGui.class.getDeclaredField("PLAYER_HEALTH_ELEMENT"));
			health.set(null, PLAYER_HEALTH);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		try {
			Field armor = unfinalize(ForgeIngameGui.class.getDeclaredField("ARMOR_LEVEL_ELEMENT"));
			armor.set(null, ARMOR_LEVEL);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		// hopefully, they're completely replaced now
	}
	
	private static void playerHealth(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements()) return;
//		if (!conf("enableHealthBar")) {
//			setupVanilla(gui);
//			gui.renderHealth(screenWidth, screenHeight, pStack);
//			return;
//		}
//		setup(gui);
//		lastHealthY = baseY(gui, screenHeight);
//		gui.left_height += 10;
//		hudRenderer.renderHealth(pStack, baseX(screenWidth), lastHealthY);
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE))
			gui.renderHealth(screenWidth, screenHeight, poseStack);
		else {
			Components.HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
			gui.left_height += 10;
		}
	}
	
	private static void absorption(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("enableHealthBar") || !conf("showAbsorption")) return;
//		setup(gui);
//		hudRenderer.renderAbsorption(pStack, baseX(screenWidth), lastHealthY);
		Components.ABSOPRTION.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	private static void armorLevel(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements()) return;
//		lastArmorY = baseY(gui, screenHeight);
//		gui.left_height += 10;
//		if (!conf("enableArmorBar")) {
//			setupVanilla(gui);
//			hudRenderer.renderVanillaArmor(pStack, baseX(screenWidth), lastArmorY);
//		} else {
//			setup(gui);
//			hudRenderer.renderArmor(pStack, baseX(screenWidth), lastArmorY);
//		}
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			Components.VANILLA_ARMOR.render(ctx);
		else Components.ARMOR.render(ctx);
		gui.left_height += 10;
	}
	
	private static void resistance(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showResistance")) return;
//		setup(gui);
//		hudRenderer.renderResistance(pStack, baseX(screenWidth), lastArmorY);
		Components.RESISTANCE.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void protection(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showProtection")) return;
//		setup(gui);
//		hudRenderer.renderProtectionOverlay(pStack, baseX(screenWidth), lastArmorY);
		Components.PROTECTION.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorToughness(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showToughness")) return;
//		setup(gui);
//		hudRenderer.renderArmorToughness(pStack, baseX(screenWidth), lastArmorY);
		Components.TOUGHNESS.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showArmorValue")) return;
//		hudRenderer.renderArmorText(pStack, baseX(screenWidth), lastArmorY);
		Components.ARMOR_TEXT.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	private static void healthText(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showHealthValue")) return;
//		hudRenderer.renderHealthText(pStack, baseX(screenWidth), lastHealthY);
		Components.HEALTH_TEXT.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	private static void debug(ForgeIngameGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
//		if (hidden() || !conf("debug")) return;
//		setup(gui);
//		hudRenderer.debugTexture(pStack);
//		hudRenderer.debugText(pStack, baseX(screenWidth), lastArmorY);
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
	
	
//	private static void setupVanilla(ForgeIngameGui gui) {
//		gui.setupOverlayRenderState(true, false);
//	}
//
//	private static void setup(ForgeIngameGui gui) {
//		gui.setupOverlayRenderState(true, false, ICONS);
//	}
//
//	private static boolean hidden() {
//		return minecraft.options.hideGui;
//	}
	
	private static int baseX(int width) {
		return width / 2 - 91;
	}
	
	private static int baseY(ForgeIngameGui gui, int height) {
		return height - gui.left_height;
	}
	
//	private static boolean conf(String name) {
//		return ApppConfig.getBool(name);
//	}
	
	@SuppressWarnings("unchecked")
	private static void unregister(IIngameOverlay overlay) {
		try {
			Field overlays = OverlayRegistry.class.getDeclaredField("overlays");
			Field overlaysOrdered = OverlayRegistry.class.getDeclaredField("overlaysOrdered");
			overlays.setAccessible(true);
			overlaysOrdered.setAccessible(true);
			OverlayEntry entry = ((Map<IIngameOverlay, OverlayEntry>) overlays.get(null)).remove(overlay);
			if (entry != null)
				((List<OverlayEntry>) overlaysOrdered.get(null)).remove(entry);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
	}
	
	private static Field unfinalize(Field field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		return field;
	}
}
