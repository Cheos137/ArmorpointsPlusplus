package dev.cheos.armorpointspp;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = Armorpointspp.MODID, value = Side.CLIENT)
public class RenderGameOverlayListener {
	private static final Minecraft minecraft = Minecraft.getMinecraft();
	private static boolean reposting, working, init;
	
	private static void init() {
		init = true;
		ApppRenderGameOverlayEvent.init();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void handle(RenderGameOverlayEvent event) {
		if (event instanceof ApppRenderGameOverlayEvent) return;
		if (reposting) return;
		if (working) return;
		if (!init) init();
		
		if (event.getType() != ARMOR && event.getType() != HEALTH) { // we only want to handle armor and health ourselves
			if (!(event.isCancelable() && event.isCanceled())) // do not repost if our event somehow got cancelled - SHOULD not happen, though
				if (repost(event) && event.isCancelable())     // repost the event, returns true if cancelled
					event.setCanceled(true);                   // cancel our "parent" event if our reposted event got cancelled
			if (event.getType() != TEXT || (event.isCancelable() && event.isCanceled()))
				return;                                        // return if we are cancelled and not a text event, we want to render but not handle text events
		}
		
		working = true;
		GuiIngameForge gui  = (GuiIngameForge) minecraft.ingameGUI;
		float partialTicks = event.getPartialTicks();
		int screenWidth    = event.getResolution().getScaledWidth();
		int screenHeight   = event.getResolution().getScaledHeight();
		
		switch (event.getType()) {
			case ARMOR:
				if (!(event instanceof Pre)) // extra check for this because mantle thinks it's great and stuff
					break;
				event.setCanceled(true); // prevent forge from rendering vanilla stuff
				if (pre(event, ARMOR))
					break;
				Overlays.armorLevel      (gui, partialTicks, screenWidth, screenHeight);
				Overlays.magicShield     (gui, partialTicks, screenWidth, screenHeight);
				Overlays.resistance      (gui, partialTicks, screenWidth, screenHeight);
				Overlays.protection      (gui, partialTicks, screenWidth, screenHeight);
				Overlays.armorToughness  (gui, partialTicks, screenWidth, screenHeight);
				Overlays.armorToughnessOv(gui, partialTicks, screenWidth, screenHeight);
				post(event, ARMOR);
				break;
			case HEALTH:
				if (!(event instanceof Pre)) // extra check for this because mantle thinks it's great and stuff
					break;
				event.setCanceled(true); // prevent forge from rendering vanilla stuff
				if (pre(event, HEALTH)) {
					if (Armorpointspp.MANTLE) // specific fix, JUST for mantle... why are you like this, mantle?
						post(event, HEALTH);
					break;
				}
				Overlays.playerHealth(gui, partialTicks, screenWidth, screenHeight);
				Overlays.absorption  (gui, partialTicks, screenWidth, screenHeight);
				post(event, HEALTH);
				break;
			case TEXT:
				if (event instanceof Post)
					break;
				Overlays.armorText (gui, partialTicks, screenWidth, screenHeight);
				Overlays.healthText(gui, partialTicks, screenWidth, screenHeight);
				Overlays.debug     (gui, partialTicks, screenWidth, screenHeight);
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
			return post(new ApppRenderGameOverlayEvent.Chat(event, ((Chat) event).getPosX(), ((Chat) event).getPosY()), event.getPhase());
		else if (event instanceof Text)
			return post(new ApppRenderGameOverlayEvent.Text(event, ((Text) event).getLeft(), ((Text) event).getRight()), event.getPhase());
		else if (event instanceof BossInfo)
			return post(new ApppRenderGameOverlayEvent.BossInfo(event, event.getType(), ((BossInfo) event).getBossInfo(), ((BossInfo) event).getX(), ((BossInfo) event).getY(), ((BossInfo) event).getIncrement()), event.getPhase());
		else if (event instanceof Pre)
			return post(new ApppRenderGameOverlayEvent.Pre(event, event.getType()), event.getPhase());
		else if (event instanceof Post)
			post(new ApppRenderGameOverlayEvent.Post(event, event.getType()), event.getPhase());
		return false;
	}
	
	private static boolean pre(RenderGameOverlayEvent parent, ElementType type) {
		return post(new ApppRenderGameOverlayEvent.Pre(parent, type));
	}
	
	private static void post(RenderGameOverlayEvent parent, ElementType type) {
		post(new ApppRenderGameOverlayEvent.Post(parent, type));
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
			EventBus bus = MinecraftForge.EVENT_BUS;
			
			if (ReflectionHelper.<EventBus, Boolean>getPrivateValueDirect(EventBus.class, "shutdown", bus)) return false;
			
			IEventListener[] listeners = event.getListenerList().getListeners(ReflectionHelper.getPrivateValueDirect(EventBus.class, "busID", bus));
			int index = 0;
			try {
				EventPriority current = null;
				for (; index < listeners.length; index++) {
					if (listeners[index] instanceof EventPriority)
						current = (EventPriority) listeners[index];
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
