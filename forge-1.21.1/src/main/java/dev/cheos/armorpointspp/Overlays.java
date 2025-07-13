package dev.cheos.armorpointspp;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.Side;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import dev.cheos.armorpointspp.mixin.GuiMixin;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = Armorpointspp.MODID, value = Dist.CLIENT)
public class Overlays {
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	public static final ResourceLocation VEHICLE_HEALTH     = ResourceLocation.fromNamespaceAndPath("minecraft"        , "vehicle_health"),
										 PLAYER_HEALTH      = ResourceLocation.fromNamespaceAndPath("minecraft"        , "player_health"),
										 ABSORPTION         = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "absorption"),
										 ABSORPTION_OV      = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "absorption_ov"),
										 ARMOR_LEVEL        = ResourceLocation.fromNamespaceAndPath("minecraft"        , "armor_level"),
										 MAGIC_SHIELD       = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "pc_magic_shield"),
										 PROTECTION         = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "protection"),
										 RESISTANCE         = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "resistance"),
										 ARMOR_TOUGHNESS    = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "toughness"),
										 ARMOR_TOUGHNESS_OV = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "toughness_ov"),
										 ARMOR_TEXT         = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "armor_text"),
										 HEALTH_TEXT        = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "health_text"),
										 TOUGHNESS_TEXT     = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "toughness_text"),
										 ITEM_NAME          = ResourceLocation.fromNamespaceAndPath("minecraft"        , "selected_item_name");
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void register(RegisterGuiLayersEvent event) {
		event.replaceLayer (PLAYER_HEALTH                           , Overlays::playerHealth);
		event.registerAbove(PLAYER_HEALTH , ABSORPTION              , Overlays::absorption);
		event.registerAbove(ABSORPTION    , ABSORPTION_OV           , Overlays::absorptionOv);
		event.replaceLayer (ARMOR_LEVEL                             , Overlays::armorLevel);
		event.registerAbove(ARMOR_LEVEL   , MAGIC_SHIELD            , Overlays::magicShield);
		event.registerAbove(MAGIC_SHIELD  , RESISTANCE              , Overlays::resistance);
		event.registerAbove(RESISTANCE    , PROTECTION              , Overlays::protection);
		event.registerAbove(VEHICLE_HEALTH, ARMOR_TOUGHNESS         , Overlays::armorToughness);
		event.registerAbove(PROTECTION    , ARMOR_TOUGHNESS_OV      , Overlays::armorToughnessOv);
		event.registerAbove(ITEM_NAME     , ARMOR_TEXT              , Overlays::armorText);
		event.registerAbove(ITEM_NAME     , HEALTH_TEXT             , Overlays::healthText);
		event.registerAbove(ITEM_NAME     , TOUGHNESS_TEXT          , Overlays::toughnessText);
		event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "debug"), Overlays::debug);
	}
	
	private static void playerHealth(GuiGraphics graphics, DeltaTracker partialTicks) {
		lastHealthY = baseY(minecraft.gui, graphics.guiHeight());
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && minecraft.gameMode.canHurtPlayer())
				((GuiMixin) minecraft.gui).invokeRenderHealthLevel(graphics);
		} else if (Components.HEALTH.render(ctx(graphics, baseX(graphics.guiWidth()), lastHealthY)))
			minecraft.gui.leftHeight += 10;
	}
	
	private static void absorption(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.ABSORPTION.render(ctx(graphics, baseX(graphics.guiWidth()), lastHealthY));
	}
	
	private static void absorptionOv(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.ABSORPTION_OVER.render(ctx(graphics, baseX(graphics.guiWidth()), lastHealthY));
	}
	
	private static void armorLevel(GuiGraphics graphics, DeltaTracker partialTicks) {
		RenderContext ctx = ctx(graphics, baseX(graphics.guiWidth()), lastArmorY = baseY(minecraft.gui, graphics.guiHeight()));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) minecraft.gui.leftHeight += 10;
	}
	
	private static void armorToughness(GuiGraphics graphics, DeltaTracker partialTicks) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(graphics, baseX(graphics.guiWidth(), side), lastToughnessY = baseY(minecraft.gui, graphics.guiHeight(), side))))
			switch (side) {
				case LEFT:
					minecraft.gui.leftHeight += 10;
					break;
				case RIGHT:
					minecraft.gui.rightHeight += 10;
					break;
			}
	}
	
	private static void resistance(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.RESISTANCE.render(ctx(graphics, baseX(graphics.guiWidth()), lastArmorY));
	}
	
	private static void protection(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.PROTECTION.render(ctx(graphics, baseX(graphics.guiWidth()), lastArmorY));
	}
	
	private static void armorToughnessOv(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.TOUGHNESS_OVER.render(ctx(graphics, baseX(graphics.guiWidth()), lastArmorY));
	}
	
	private static void magicShield(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.MAGIC_SHIELD.render(ctx(graphics, baseX(graphics.guiWidth()), lastArmorY));
	}
	
	private static void armorText(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.ARMOR_TEXT.render(ctx(graphics, baseX(graphics.guiWidth()), lastArmorY));
	}
	
	private static void healthText(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.HEALTH_TEXT.render(ctx(graphics, baseX(graphics.guiWidth()), lastHealthY));
	}
	
	private static void toughnessText(GuiGraphics graphics, DeltaTracker partialTicks) {
		Components.TOUGHNESS_TEXT.render(ctx(graphics, baseX(graphics.guiWidth(), ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	private static void debug(GuiGraphics graphics, DeltaTracker partialTicks) {
		RenderContext ctx = ctx(graphics, baseX(graphics.guiWidth()), lastArmorY);
		Components.DEBUG.render(ctx);
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
	
	
	private static int baseX(int width) {
		return baseX(width, Side.LEFT);
	}
	
	private static int baseX(int width, Side side) {
		return width / 2 + switch (side) { case LEFT -> -91; case RIGHT -> 10; };
	}
	
	private static int baseY(Gui gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(Gui gui, int height, Side side) {
		return height - switch (side) { case LEFT -> gui.leftHeight; case RIGHT -> gui.rightHeight; };
	}
}
