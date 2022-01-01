package dev.cheos.armorpointspp;

import java.util.ArrayList;

import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.*;

// reflecting into asm generated code is scary...
// won't touch anything in here anymore... until it is broken...
// when that happens, though, i can (hopefully) maybe drop support for 1.12?
public interface ApppRenderGameOverlayEvent {
	static final int busID = aquireFEBusID();
	
	static int aquireFEBusID() {
		try {
			return ReflectionHelper.getPrivateValue(EventBus.class, "busID", MinecraftForge.EVENT_BUS);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to aquire eventbus id", e);
		}
	}
	
	static <T> ListenerList getListenerList(Class<T> clazz) {
		try {
			return ReflectionHelper.getPrivateValue(clazz, "LISTENER_LIST"); // forge does some asm magic to add this to each event class
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to get listener list for class " + clazz.getName(), e);
		}
	}
	
	static <T> void setListenerList(Class<T> clazz, ListenerList list) {
		try {
			ReflectionHelper.setPrivateValue(clazz, "LISTENER_LIST", list); // forge does some asm magic to add this to each event class
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to set listener list for class " + clazz.getName(), e);
		}
	}
	
	default void aquireListenerList() {
		if (!(this instanceof Event))
			throw new IllegalStateException("Only events can implement ApppRenderGameOverlayEvent! (implemented by: " + getClass().getName() + ")");
		
		Class<?> parent = getClass().getSuperclass();
		ListenerList originalList = getListenerList(parent);
		ListenerList parentList   = getListenerList(parent.getSuperclass());
		setListenerList(parent, new ListenerList(parentList));
		ListenerList listenerList = getListenerList(parent);
		setListenerList(getClass(), originalList == null ? new ListenerList(listenerList) : originalList);
		
		if (listenerList.getListeners(busID).length == 0)
			try {
				listenerList.register(busID, EventPriority.HIGHEST, new ASMEventHandler(RenderGameOverlayListener.class, RenderGameOverlayListener.class.getDeclaredMethod("handle", RenderGameOverlayEvent.class), Loader.instance().activeModContainer(), false));
			} catch (Throwable t) {
				throw new IllegalStateException("Error re-registering appp event handler", t);
			}
	}
	
	
	public static class Pre extends RenderGameOverlayEvent.Pre implements ApppRenderGameOverlayEvent {
		private static boolean set;
		
		public Pre(RenderGameOverlayEvent parent, ElementType type) {
			super(parent, type);
			if (!set) {
				aquireListenerList();
				set = true;
			}
		}
	}
	
	public static class Post extends RenderGameOverlayEvent.Post implements ApppRenderGameOverlayEvent {
		private static boolean set;
		
		public Post(RenderGameOverlayEvent parent, ElementType type) {
			super(parent, type);
			if (!set) {
				aquireListenerList();
				set = true;
			}
		}
	}
	
	public static class BossInfo extends RenderGameOverlayEvent.BossInfo implements ApppRenderGameOverlayEvent {
		private static boolean set;
		
		public BossInfo(RenderGameOverlayEvent parent, ElementType type, BossInfoClient bossInfo, int x, int y, int increment) {
			super(parent, type, bossInfo, x, y, increment);
			if (!set) {
				aquireListenerList();
				set = true;
			}
		}
	}
	
	public static class Text extends RenderGameOverlayEvent.Text implements ApppRenderGameOverlayEvent {
		private static boolean set;
		
		public Text(RenderGameOverlayEvent parent, ArrayList<String> left, ArrayList<String> right) {
			super(parent, left, right);
			if (!set) {
				aquireListenerList();
				set = true;
			}
		}
	}
	
	public static class Chat extends RenderGameOverlayEvent.Chat implements ApppRenderGameOverlayEvent {
		private static boolean set;
		
		public Chat(RenderGameOverlayEvent parent, int posX, int posY) {
			super(parent, posX, posY);
			if (!set) {
				aquireListenerList();
				set = true;
			}
		}
	}
}
