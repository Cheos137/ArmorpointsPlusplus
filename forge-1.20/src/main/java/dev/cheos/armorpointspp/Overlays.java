package dev.cheos.armorpointspp;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.Side;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.render.Components;
import dev.cheos.armorpointspp.impl.*;
import dev.cheos.armorpointspp.mixin.IRegisterGuiOverlaysEventMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

@EventBusSubscriber(modid = Armorpointspp.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class Overlays {
	private static final IDataProvider DATA_PROVIDER = new DataProviderImpl();
	private static final IRenderer RENDERER          = new RendererImpl();
	private static final IProfiler PROFILER          = new ProfilerImpl();
	private static final Minecraft minecraft         = Minecraft.getInstance();
	private static int lastArmorY = 0, lastHealthY = 0, lastToughnessY = 0;
	
	public static final ResourceLocation MOUNT_HEALTH       = new ResourceLocation("minecraft"        , "mount_health"),
										 PLAYER_HEALTH      = new ResourceLocation("minecraft"        , "player_health"),
										 ABSORPTION         = new ResourceLocation(Armorpointspp.MODID, "absorption"),
										 ABSORPTION_OV      = new ResourceLocation(Armorpointspp.MODID, "absorption_ov"),
										 ARMOR_LEVEL        = new ResourceLocation("minecraft"        , "armor_level"),
										 MAGIC_SHIELD       = new ResourceLocation(Armorpointspp.MODID, "pc_magic_shield"),
										 PROTECTION         = new ResourceLocation(Armorpointspp.MODID, "protection"),
										 RESISTANCE         = new ResourceLocation(Armorpointspp.MODID, "resistance"),
										 ARMOR_TOUGHNESS    = new ResourceLocation(Armorpointspp.MODID, "toughness"),
										 ARMOR_TOUGHNESS_OV = new ResourceLocation(Armorpointspp.MODID, "toughness_ov"),
										 ARMOR_TEXT         = new ResourceLocation(Armorpointspp.MODID, "armor_text"),
										 HEALTH_TEXT        = new ResourceLocation(Armorpointspp.MODID, "health_text"),
										 TOUGHNESS_TEXT     = new ResourceLocation(Armorpointspp.MODID, "toughness_text"),
										 ITEM_NAME          = new ResourceLocation("minecraft"        , "item_name");
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void register(RegisterGuiOverlaysEvent event) {
		MissedFunctionality missedFunctionality = new MissedFunctionality(event);
		
		missedFunctionality.unregister(ARMOR_LEVEL);
		missedFunctionality.registerOverriding(PLAYER_HEALTH                 , Overlays::playerHealth);
		event.registerAbove(PLAYER_HEALTH, ABSORPTION              .getPath(), Overlays::absorption);
		event.registerAbove(ABSORPTION   , ABSORPTION_OV           .getPath(), Overlays::absorptionOv);
		missedFunctionality.registerArbitraryAbove(ABSORPTION_OV, ARMOR_LEVEL, Overlays::armorLevel);
		event.registerAbove(ARMOR_LEVEL  , MAGIC_SHIELD            .getPath(), Overlays::magicShield);
		event.registerAbove(MAGIC_SHIELD , RESISTANCE              .getPath(), Overlays::resistance);
		event.registerAbove(RESISTANCE   , PROTECTION              .getPath(), Overlays::protection);
		event.registerAbove(MOUNT_HEALTH , ARMOR_TOUGHNESS         .getPath(), Overlays::armorToughness);
		event.registerAbove(PROTECTION   , ARMOR_TOUGHNESS_OV      .getPath(), Overlays::armorToughnessOv);
		event.registerAbove(ITEM_NAME    , ARMOR_TEXT              .getPath(), Overlays::armorText);
		event.registerAbove(ITEM_NAME    , HEALTH_TEXT             .getPath(), Overlays::healthText);
		event.registerAbove(ITEM_NAME    , TOUGHNESS_TEXT          .getPath(), Overlays::toughnessText);
		event.registerAboveAll("debug"                                       , Overlays::debug);
	}
	
	private static void playerHealth(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		lastHealthY = baseY(gui, screenHeight);
		if (!ApppConfig.instance().bool(BooleanOption.HEALTH_ENABLE)) {
			if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements())
				gui.renderHealth(screenWidth, screenHeight, graphics);
		} else if (Components.HEALTH.render(ctx(graphics, baseX(screenWidth), lastHealthY)))
			gui.leftHeight += 10;
	}
	
	private static void absorption(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSORPTION.render(ctx(graphics, baseX(screenWidth), lastHealthY));
	}
	
	private static void absorptionOv(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.ABSORPTION_OVER.render(ctx(graphics, baseX(screenWidth), lastHealthY));
	}
	
	private static void armorLevel(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(graphics, baseX(screenWidth), lastArmorY = baseY(gui, screenHeight));
		boolean flag = false;
		if (!ApppConfig.instance().bool(BooleanOption.ARMOR_ENABLE))
			flag = Components.VANILLA_ARMOR.render(ctx);
		else flag = Components.ARMOR.render(ctx);
		if (flag) gui.leftHeight += 10;
	}
	
	private static void armorToughness(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Side side = ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE);
		if (Components.TOUGHNESS.render(ctx(graphics, baseX(screenWidth, side), lastToughnessY = baseY(gui, screenHeight, side))))
			switch (side) {
				case LEFT:
					gui.leftHeight += 10;
					break;
				case RIGHT:
					gui.rightHeight += 10;
					break;
			}
	}
	
	private static void resistance(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.RESISTANCE.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	private static void protection(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.PROTECTION.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorToughnessOv(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_OVER.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	private static void magicShield(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.MAGIC_SHIELD.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	private static void armorText(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.ARMOR_TEXT.render(ctx(graphics, baseX(screenWidth), lastArmorY));
	}
	
	private static void healthText(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.HEALTH_TEXT.render(ctx(graphics, baseX(screenWidth), lastHealthY));
	}
	
	private static void toughnessText(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		Components.TOUGHNESS_TEXT.render(ctx(graphics, baseX(screenWidth, ApppConfig.instance().enm(EnumOption.TOUGHNESS_SIDE)), lastToughnessY));
	}
	
	private static void debug(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
		RenderContext ctx = ctx(graphics, baseX(screenWidth), lastArmorY);
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
	
	private static int baseY(ExtendedGui gui, int height) {
		return baseY(gui, height, Side.LEFT);
	}
	
	private static int baseY(ExtendedGui gui, int height, Side side) {
		return height - switch (side) { case LEFT -> gui.leftHeight; case RIGHT -> gui.rightHeight; };
	}
	
	
	public static final class MissedFunctionality {
		private final IRegisterGuiOverlaysEventMixin event;
		private final Map<ResourceLocation, IGuiOverlay> overlays;
		private final List<ResourceLocation> orderedOverlays;
		
		public MissedFunctionality(RegisterGuiOverlaysEvent event) {
			this.event = (IRegisterGuiOverlaysEventMixin) event;
			this.overlays = this.event.getOverlays();
			this.orderedOverlays = this.event.getOrderedOverlays();
		}
		
		public void registerOverriding(ResourceLocation id, IGuiOverlay overlay) {
			Preconditions.checkArgument(this.overlays.containsKey(id), "Overlay not registered: " + id);
			this.event.getOverlays().put(id, overlay);
		}
		
		public void unregister(ResourceLocation id) {
			Preconditions.checkArgument(this.overlays.containsKey(id), "Overlay not registered: " + id);
			this.overlays.remove(id);
			this.orderedOverlays.remove(id);
		}
		
		public void registerArbitraryAbove(ResourceLocation other, ResourceLocation id, IGuiOverlay overlay) {
			Preconditions.checkArgument(!this.overlays.containsKey(id), "Overlay already registered: " + id);
			
			int idx = this.overlays.size();
			if (other != null) {
				idx = this.orderedOverlays.indexOf(other) + 1;
				Preconditions.checkState(idx > 0, "Attempted to order against an unregistered overlay. Only order against vanilla's and your own.");
			}
			
			this.overlays.put(id, overlay);
			this.orderedOverlays.add(idx, id);
		}
	}
}
