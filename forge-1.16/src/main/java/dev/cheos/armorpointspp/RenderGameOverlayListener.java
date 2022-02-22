package dev.cheos.armorpointspp;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ARMOR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.TEXT;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.*;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Armorpointspp.MODID, value = Dist.CLIENT)
public class RenderGameOverlayListener {
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final ForgeIngameGui gui  = (ForgeIngameGui) minecraft.gui;
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void handle(RenderGameOverlayEvent event) {
		if (event instanceof ApppRenderGameOverlayEvent) return;
		
		if (event.getType() != ARMOR && event.getType() != HEALTH) { // we only want to handle armor and health ourselves
			if (!(event.isCancelable() && event.isCanceled())) // do not repost if our event somehow got cancelled - SHOULD not happen, though
				if (repost(event) && event.isCancelable())     // repost the event, returns true if cancelled
					event.setCanceled(true);                   // cancel our "parent" event if our reposted event got cancelled
			if (event.getType() != TEXT || (event.isCancelable() && event.isCanceled()))
				return;                                        // return if we are cancelled and not a text event, we want to render but not handle text events
		}
		
		MatrixStack poseStack = event.getMatrixStack();
		float partialTicks    = event.getPartialTicks();
		int screenWidth       = event.getWindow().getGuiScaledWidth();
		int screenHeight      = event.getWindow().getGuiScaledHeight();
		
		switch (event.getType()) {
			case ARMOR:
				if (!(event instanceof Pre)) // extra check for this because mantle thinks it's great and stuff
					break;
				event.setCanceled(true); // prevent forge from rendering vanilla stuff
				if (pre(poseStack, event, ARMOR))
					break;
				Overlays.armorLevel      (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.magicShield     (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.resistance      (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.protection      (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.armorToughness  (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.armorToughnessOv(gui, poseStack, partialTicks, screenWidth, screenHeight);
				post(poseStack, event, ARMOR);
				break;
			case HEALTH:
				if (!(event instanceof Pre)) // extra check for this because mantle thinks it's great and stuff
					break;
				event.setCanceled(true); // prevent forge from rendering vanilla stuff
				if (pre(poseStack, event, HEALTH)) {
					if (Armorpointspp.MANTLE) // specific fix, JUST for mantle... why are you like this, mantle?
						post(poseStack, event, HEALTH);
					break;
				}
				Overlays.playerHealth(gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.absorption  (gui, poseStack, partialTicks, screenWidth, screenHeight);
				post(poseStack, event, HEALTH);
				break;
			case TEXT:
				if (event instanceof Post)
					break;
				Overlays.armorText (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.healthText(gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.debug     (gui, poseStack, partialTicks, screenWidth, screenHeight);
				break;
			default: // we can only reach this switch with ARMOR, HEALTH and TEXT, everything else indicates something going wrong
				throw new RuntimeException("Something went wrong - reached code that should not be reachable with event type " + event.getType());
			}
		Overlays.cleanup();
	}
	
	private static boolean repost(RenderGameOverlayEvent event) {
		if (event instanceof Chat)
			return post(new ApppRenderGameOverlayEvent.Chat(event.getMatrixStack(), event, ((Chat) event).getPosX(), ((Chat) event).getPosY()));
		else if (event instanceof Text)
			return post(new ApppRenderGameOverlayEvent.Text(event.getMatrixStack(), event, ((Text) event).getLeft(), ((Text) event).getRight()));
		else if (event instanceof BossInfo)
			return post(new ApppRenderGameOverlayEvent.BossInfo(event.getMatrixStack(), event, ((BossInfo) event).getType(), ((BossInfo) event).getBossInfo(), ((BossInfo) event).getX(), ((BossInfo) event).getY(), ((BossInfo) event).getIncrement()));
		else if (event instanceof Pre)
			return pre(event.getMatrixStack(), event, event.getType());
		else if (event instanceof Post)
			post(event.getMatrixStack(), event, event.getType());
		return false;
	}
	
	private static boolean post(Event event) {
		return MinecraftForge.EVENT_BUS.post(event);
	}
	
	private static boolean pre(MatrixStack poseStack, RenderGameOverlayEvent parent, ElementType type) {
		return post(new ApppRenderGameOverlayEvent.Pre(poseStack, parent, type));
	}
	
	private static void post(MatrixStack poseStack, RenderGameOverlayEvent parent, ElementType type) {
		post(new ApppRenderGameOverlayEvent.Post(poseStack, parent, type));
	}
}
