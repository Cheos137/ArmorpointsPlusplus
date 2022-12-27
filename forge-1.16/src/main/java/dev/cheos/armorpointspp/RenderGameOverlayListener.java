package dev.cheos.armorpointspp;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.*;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.*;

//@EventBusSubscriber(modid = Armorpointspp.MODID, value = Dist.CLIENT) // handled in main class
public class RenderGameOverlayListener {
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static boolean reposting, working, init;
	
	private static void init() {
		init = true;
		ApppRenderGameOverlayEvent.init();
	}
	
//	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true) // handled in main class
	@SubscribeEvent(receiveCanceled = true)
	public static void handle(RenderGameOverlayEvent event) {
		if (event instanceof ApppRenderGameOverlayEvent) return;
		if (reposting) return;
		if (working) return;
		if (!init) init();
		
		if (event.getType() != ARMOR && event.getType() != HEALTH) { // we only want to handle armor and health ourselves
			if (!(event.isCancelable() && event.isCanceled())) // do not repost if our event somehow got cancelled - SHOULD not happen, though
				if (repost(event) && event.isCancelable()) {   // repost the event, returns true if cancelled
					event.setCanceled(true);                   // cancel our "parent" event if our reposted event got cancelled
					event.getMatrixStack().popPose();
				}
			if (event.getType() != TEXT || (event.isCancelable() && event.isCanceled()))
				return;                                        // return if we are cancelled and not a text event, we want to render but not handle text events
		}
		
		if (event.getPhase() != EventPriority.NORMAL) return;
		
		working = true;
		ForgeIngameGui gui = (ForgeIngameGui) minecraft.gui;
		MatrixStack poseStack = event.getMatrixStack();
		float partialTicks    = event.getPartialTicks();
		int screenWidth       = event.getWindow().getGuiScaledWidth();
		int screenHeight      = event.getWindow().getGuiScaledHeight();
		
		switch (event.getType()) {
			case ARMOR:
//				if (!(event instanceof Pre)) // extra check for this because mantle thinks it's great and stuff
//					break;
				event.setCanceled(true); // prevent forge from rendering vanilla stuff
				if (pre(poseStack, event, ARMOR)) {
					Overlays.updateArmorY(gui, screenHeight);
					Overlays.updateToughnessY(gui, screenHeight);
					break;
				}
				Overlays.armorLevel      (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.magicShield     (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.resistance      (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.protection      (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.armorToughness  (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.armorToughnessOv(gui, poseStack, partialTicks, screenWidth, screenHeight);
				post(poseStack, event, ARMOR);
				break;
			case HEALTH:
//				if (!(event instanceof Pre)) // extra check for this because mantle thinks it's great and stuff
//					break;
				event.setCanceled(true); // prevent forge from rendering vanilla stuff
				if (pre(poseStack, event, HEALTH)) {
					Overlays.updateHealthY(gui, screenHeight);
					if (Armorpointspp.MANTLE) // specific fix, JUST for mantle... why are you like this, mantle?
						post(poseStack, event, HEALTH);
					break;
				}
				Overlays.playerHealth(gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.absorption  (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.absorptionOv(gui, poseStack, partialTicks, screenWidth, screenHeight);
				post(poseStack, event, HEALTH);
				break;
			case TEXT:
//				if (event instanceof Post)
//					break;
				Overlays.armorText (gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.healthText(gui, poseStack, partialTicks, screenWidth, screenHeight);
				Overlays.debug     (gui, poseStack, partialTicks, screenWidth, screenHeight);
				break;
			default: // we can only reach this switch with ARMOR, HEALTH and TEXT, everything else indicates something going wrong
				working = false;
				throw new RuntimeException("Something went wrong - reached code that should not be reachable with event type " + event.getType());
			}
		Overlays.cleanup();
		working = false;
	}
	
	private static boolean repost(RenderGameOverlayEvent event) {
		if (event instanceof Chat)
			return post(new ApppRenderGameOverlayEvent.Chat(event.getMatrixStack(), event, ((Chat) event).getPosX(), ((Chat) event).getPosY()), event.getPhase());
		else if (event instanceof Text)
			return post(new ApppRenderGameOverlayEvent.Text(event.getMatrixStack(), event, ((Text) event).getLeft(), ((Text) event).getRight()), event.getPhase());
		else if (event instanceof BossInfo)
			return post(new ApppRenderGameOverlayEvent.BossInfo(event.getMatrixStack(), event, event.getType(), ((BossInfo) event).getBossInfo(), ((BossInfo) event).getX(), ((BossInfo) event).getY(), ((BossInfo) event).getIncrement()), event.getPhase());
		else if (event instanceof Pre) {
			event.getMatrixStack().pushPose();
			return post(new ApppRenderGameOverlayEvent.Pre(event.getMatrixStack(), event, event.getType()), event.getPhase());
		} else if (event instanceof Post) {
			post(new ApppRenderGameOverlayEvent.Post(event.getMatrixStack(), event, event.getType()), event.getPhase());
			event.getMatrixStack().popPose();
		} return false;
	}
	
	private static boolean pre(MatrixStack poseStack, RenderGameOverlayEvent parent, ElementType type) {
		poseStack.pushPose();
		return post(new ApppRenderGameOverlayEvent.Pre(poseStack, parent, type));
	}
	
	private static void post(MatrixStack poseStack, RenderGameOverlayEvent parent, ElementType type) {
		post(new ApppRenderGameOverlayEvent.Post(poseStack, parent, type));
		poseStack.popPose();
	}
	
	private static boolean post(Event event) {
		reposting = true;
		boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
		reposting = false;
		return cancelled;
	}
	
	private static boolean post(Event event, EventPriority prio) {
		reposting = true;
		try {
			EventBus bus = (EventBus) MinecraftForge.EVENT_BUS;
			
			if (ReflectionHelper.<EventBus, Boolean>getPrivateValueDirect(EventBus.class, "shutdown", bus))
				return false;
			if (ReflectionHelper.<EventBus, Boolean>getPrivateValueDirect(EventBus.class, "checkTypesOnDispatch")) {
				Class<?> baseType = ReflectionHelper.getPrivateValueDirect(EventBus.class, "baseType", bus);
				if (!baseType.isInstance(event))
					throw new IllegalArgumentException("Cannot post event of type " + event.getClass().getSimpleName() + " to this event. Must match type: " + baseType.getSimpleName());
			}
			
			IEventListener[] listeners = event.getListenerList().getListeners(ReflectionHelper.getPrivateValueDirect(EventBus.class, "busID", bus));
			int index = 0;
			try {
				EventPriority current = null;
				for (; index < listeners.length; index++) {
					if (listeners[index] instanceof EventPriority) {
						current = (EventPriority) listeners[index];
						if (!ReflectionHelper.<EventBus, Boolean>getPrivateValueDirect(EventBus.class, "trackPhases", bus))
							continue;
					}
					if (current == prio)
						listeners[index].invoke(event);
				}
			} catch (Throwable t) {
				ReflectionHelper.<EventBus, IEventExceptionHandler>getPrivateValueDirect(EventBus.class, "exceptionHandler", bus).handleException(bus, event, listeners, index, t);
				throw t;
			}
		} catch (Throwable t) {
			Armorpointspp.LOGGER.warn("Exception reposting event " + event.getClass().getName() + " with priority " + prio.name(), t);
			throw new RuntimeException(t); // simply rethrowing does not work due to checked exceptions inside try block
		} finally {
			reposting = false;
		}
		return event.isCancelable() && event.isCanceled();
	}
}
