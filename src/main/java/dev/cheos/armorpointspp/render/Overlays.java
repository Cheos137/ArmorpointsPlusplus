package dev.cheos.armorpointspp.render;

import static net.minecraftforge.client.gui.ForgeIngameGui.BOSS_HEALTH_ELEMENT;
import static net.minecraftforge.client.gui.ForgeIngameGui.HUD_TEXT_ELEMENT;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.client.gui.OverlayRegistry.OverlayEntry;

//the cool thing about this update is that i can just go in and edit vanilla stuff without worrying about breaking something - it'll never be my fault again
public class Overlays {
	private static final ResourceLocation ICONS = new ResourceLocation(Armorpointspp.MODID, "textures/gui/icons.png");
	private static final HUDRenderer hudRenderer     = new HUDRenderer();
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
		unregister(ForgeIngameGui.PLAYER_HEALTH_ELEMENT);
		unregister(ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
		ForgeIngameGui.PLAYER_HEALTH_ELEMENT = PLAYER_HEALTH;
		ForgeIngameGui.ARMOR_LEVEL_ELEMENT = ARMOR_LEVEL;
	}
	
	private static void playerHealth(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements()) return;
		if (!conf("enableHealthBar")) {
			setupVanilla(gui);
			gui.renderHealth(screenWidth, screenHeight, pStack);
			return;
		}
		setup(gui);
		lastHealthY = baseY(gui, screenHeight);
		gui.left_height += 10;
		hudRenderer.renderHealth(pStack, baseX(screenWidth), lastHealthY);
	}
	
	private static void absorption(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("enableHealthBar") || !conf("showAbsorption")) return;
		setup(gui);
		hudRenderer.renderAbsorption(pStack, baseX(screenWidth), lastHealthY);
	}
	
	private static void armorLevel(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements()) return;
		lastArmorY = baseY(gui, screenHeight);
		gui.left_height += 10;
		if (!conf("enableArmorBar")) {
			setupVanilla(gui);
			hudRenderer.renderVanillaArmor(pStack, baseX(screenWidth), lastArmorY);
		} else {
			setup(gui);
			hudRenderer.renderArmor(pStack, baseX(screenWidth), lastArmorY);
		}
	}
	
	private static void resistance(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showResistance")) return;
		setup(gui);
		hudRenderer.renderResistance(pStack, baseX(screenWidth), lastArmorY);
	}
	
	private static void protection(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showProtection")) return;
		setup(gui);
		hudRenderer.renderProtectionOverlay(pStack, baseX(screenWidth), lastArmorY);
	}
	
	private static void armorToughness(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showToughness")) return;
		setup(gui);
		hudRenderer.renderArmorToughness(pStack, baseX(screenWidth), lastArmorY);
	}
	
	private static void armorText(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showArmorValue")) return;
		hudRenderer.renderArmorText(pStack, baseX(screenWidth), lastArmorY);
	}
	
	private static void healthText(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !gui.shouldDrawSurvivalElements() || !conf("showHealthValue")) return;
		hudRenderer.renderHealthText(pStack, baseX(screenWidth), lastHealthY);
	}
	
	private static void debug(ForgeIngameGui gui, PoseStack pStack, float partialTicks, int screenWidth, int screenHeight) {
		if (hidden() || !conf("debug")) return;
		setup(gui);
		hudRenderer.debugTexture(pStack);
		hudRenderer.debugText(pStack, baseX(screenWidth), lastArmorY);
	}
	
	
	private static void setupVanilla(ForgeIngameGui gui) {
		gui.setupOverlayRenderState(true, false);
	}
	
	private static void setup(ForgeIngameGui gui) {
		gui.setupOverlayRenderState(true, false, ICONS);
	}
	
	private static boolean hidden() {
		return minecraft.options.hideGui;
	}
	
	private static int baseX(int width) {
		return width / 2 - 91;
	}
	
	private static int baseY(ForgeIngameGui gui, int height) {
		return height - gui.left_height;
	}
	
	private static boolean conf(String name) {
		return ApppConfig.getBool(name);
	}
	
	private static void unregister(IIngameOverlay overlay) {
		OverlayEntry entry = OverlayRegistry.overlays.remove(overlay);
		if (entry != null)
			OverlayRegistry.overlaysOrdered.remove(entry);
	}
}
