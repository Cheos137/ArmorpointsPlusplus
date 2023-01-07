package dev.cheos.armorpointspp.compat;

import static dev.cheos.libhud.api.Component.*;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.Side;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import dev.cheos.libhud.*;
import dev.cheos.libhud.api.Component.NamedComponent;
import dev.cheos.libhud.api.event.RegisterComponentsEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class LibhudCompat implements Consumer<RegisterComponentsEvent> {
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	public static final NamedComponent ARMOR              = named(VanillaComponents.ARMOR.getName(), LibhudCompat::armorLevel),
									   HEALTH             = named(VanillaComponents.HEALTH.getName(), LibhudCompat::playerHealth),
									   ABSORPTION         = named(new ResourceLocation(Armorpointspp.MODID, "absorption"), LibhudCompat::absorption),
									   ABSORPTION_OV      = named(new ResourceLocation(Armorpointspp.MODID, "absorption_ov"), LibhudCompat::absorptionOv),
									   MAGIC_SHIELD       = named(new ResourceLocation(Armorpointspp.MODID, "pc_magic_shield"), LibhudCompat::magicShield),
									   RESISTANCE         = named(new ResourceLocation(Armorpointspp.MODID, "resistance"), LibhudCompat::resistance),
									   PROTECTION         = named(new ResourceLocation(Armorpointspp.MODID, "protection"), LibhudCompat::protection),
									   ARMOR_TOUGHNESS    = named(new ResourceLocation(Armorpointspp.MODID, "toughness"), LibhudCompat::armorToughness),
									   ARMOR_TOUGHNESS_OV = named(new ResourceLocation(Armorpointspp.MODID, "toughness_ov"), LibhudCompat::armorToughnessOv),
									   ARMOR_TEXT         = named(new ResourceLocation(Armorpointspp.MODID, "armor_text"), LibhudCompat::armorText),
									   HEALTH_TEXT        = named(new ResourceLocation(Armorpointspp.MODID, "health_text"), LibhudCompat::healthText),
									   TOUGHNESS_TEXT     = named(new ResourceLocation(Armorpointspp.MODID, "toughness_text"), LibhudCompat::toughnessText),
									   DEBUG              = named(new ResourceLocation(Armorpointspp.MODID, "debug"), LibhudCompat::debug);
	
	public static void init() {
		Libhud.registerRegisterComponentListener(new LibhudCompat());
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void accept(RegisterComponentsEvent event) {
		event.replace(HEALTH.getName(), LibhudCompat::playerHealth);
		event.registerAbove(HEALTH.getName(), ABSORPTION);
		event.registerAbove(ABSORPTION.getName(), ABSORPTION_OV);
		event.replace(ARMOR);
		event.registerAbove(ARMOR.getName(), MAGIC_SHIELD);
		event.registerAbove(MAGIC_SHIELD.getName(), RESISTANCE);
		event.registerAbove(RESISTANCE.getName(), PROTECTION);
		event.registerAbove(PROTECTION.getName(), ARMOR_TOUGHNESS_OV);
		event.registerAbove(VanillaComponents.VEHICLE_HEALTH.getName(), ARMOR_TOUGHNESS);
		event.registerAbove(VanillaComponents.ITEM_NAME.getName(), ARMOR_TEXT);
		event.registerAbove(VanillaComponents.ITEM_NAME.getName(), HEALTH_TEXT);
		event.registerAbove(VanillaComponents.ITEM_NAME.getName(), TOUGHNESS_TEXT);
		event.registerTop(DEBUG);
	}
	
	static boolean playerHealth(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && DATA_PROVIDER.shouldDrawSurvivalElements()) {
				VanillaComponents.HEALTH.render(gui, poseStack, partialTicks, screenWidth, screenHeight);
				return true;
			}
		} else if (Components.HEALTH.render(ctx(poseStack, baseX(screenWidth), lastHealthY))) {
			gui.leftOffset += 10;
			return true;
		}
		return false;
	}
	
	static boolean absorption(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ABSORPTION.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static boolean absorptionOv(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ABSORPTION_OVER.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static boolean armorLevel(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(poseStack, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) gui.leftOffset += 10;
		return flag;
	}
	
	static boolean armorToughness(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(poseStack, baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
			switch (side) {
				case LEFT:
					gui.leftOffset += 10;
					return true;
				case RIGHT:
					gui.rightOffset += 10;
					return true;
			}
		return false;
	}
	
	static boolean resistance(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.RESISTANCE.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean protection(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.PROTECTION.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean armorToughnessOv(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.TOUGHNESS_OVER.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean magicShield(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.MAGIC_SHIELD.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean armorText(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.ARMOR_TEXT.render(ctx(poseStack, baseX(screenWidth), lastArmorY));
	}
	
	static boolean healthText(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.HEALTH_TEXT.render(ctx(poseStack, baseX(screenWidth), lastHealthY));
	}
	
	static boolean toughnessText(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
		return Components.TOUGHNESS_TEXT.render(ctx(poseStack, baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	static boolean debug(LibhudGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
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
	
	private static int baseY(LibhudGui gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(LibhudGui gui, int height, Side side) {
		return height - (side == Side.LEFT ? gui.leftOffset : gui.rightOffset);
	}
}
