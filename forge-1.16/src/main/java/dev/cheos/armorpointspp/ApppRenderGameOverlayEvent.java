package dev.cheos.armorpointspp;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventListenerHelper;

public interface ApppRenderGameOverlayEvent {
	Map<Class<?>, ListenerList> listeners = aquireListeners();
	ReadWriteLock lock = aquireLock();
	int busID = aquireFEBusID();
	
	static Map<Class<?>, ListenerList> aquireListeners() {
		try {
			return ReflectionHelper.getPrivateValue(EventListenerHelper.class, "listeners");
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to aquire event listener list", e);
		}
	}
	
	static ReadWriteLock aquireLock() {
		try {
			return ReflectionHelper.getPrivateValue(EventListenerHelper.class, "lock");
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to aquire rw lock", e);
		}
	}
	
	static int aquireFEBusID() {
		try {
			return ReflectionHelper.getPrivateValue(EventBus.class, "busID", (EventBus) MinecraftForge.EVENT_BUS);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Unable to aquire eventbus id", e);
		}
	}
	
	static void init() { // force listener list init of all events
		RenderGameOverlayEvent parent = new RenderGameOverlayEvent(null, 0, null);
		new      Pre(null, parent, null);
		new     Post(null, parent, null);
		new BossInfo(null, parent, null, null, 0, 0, 0);
		new     Text(null, parent, null, null);
		new     Chat(null, parent, 0, 0);
	}
	
	
	default void aquireListenerList() {
		if (!(this instanceof Event))
			throw new IllegalStateException("Only events can implement ApppRenderGameOverlayEvent! (implemented by: " + getClass().getName() + ")");
		
		final Lock readLock = lock.readLock();
		final Lock writeLock = lock.writeLock();
		
		Class<?> master = RenderGameOverlayEvent.class;
		Class<?> parent = getClass().getSuperclass();
		
		ListenerList masterList = EventListenerHelper.getListenerList(master);
		writeLock.lock();
		readLock.lock();
		ListenerList originalList = listeners.remove(parent);
		readLock.unlock();
		writeLock.unlock();
		EventListenerHelper.getListenerList(getClass()); // force the listener list to update throughout forge
		writeLock.lock();
		readLock.lock();
		listeners.put(parent, new ListenerList(masterList));
		readLock.unlock();
		writeLock.unlock();
		ListenerList listenerList = EventListenerHelper.getListenerList(parent);
		setListenerList(originalList == null ? new ListenerList(listenerList) : originalList);
		
		Armorpointspp.LOGGER.debug("hijacked listener list for RenderGameOverlayEvent." + getClass().getSimpleName()
				+ "\n  new      list: " + Arrays.asList(listenerList.getListeners(busID))
				+ "\n  hijacked list: " + (originalList == null ? "null" : Arrays.asList(originalList)));
		
//		if (listenerList.getListeners(busID).length == 0) // handled in main class
//			try {
//				for (EventPriority prio : EventPriority.values())
//					listenerList.register(busID, prio, new ASMEventHandler(RenderGameOverlayListener.class, RenderGameOverlayListener.class.getDeclaredMethod("handle", RenderGameOverlayEvent.class), false));
//			} catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
//				throw new IllegalStateException("Error re-registering appp event handler", e);
//			}
	}
	
	void setListenerList(ListenerList llist);
	
	public static class Pre extends RenderGameOverlayEvent.Pre implements ApppRenderGameOverlayEvent {
		private static ListenerList list;
		
		public Pre(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type) {
			super(mStack, parent, type);
			if (list == null)
				aquireListenerList();
		}
		
		@Override
		public ListenerList getListenerList() {
			return list;
		}
		
		@Override
		public void setListenerList(ListenerList llist) {
			list = llist;
		}
	}
	
	public static class Post extends RenderGameOverlayEvent.Post implements ApppRenderGameOverlayEvent {
		private static ListenerList list;
		
		public Post(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type) {
			super(mStack, parent, type);
			if (list == null)
				aquireListenerList();
		}
		
		@Override
		public ListenerList getListenerList() {
			return list;
		}
		
		@Override
		public void setListenerList(ListenerList llist) {
			list = llist;
		}
	}
	
	public static class BossInfo extends RenderGameOverlayEvent.BossInfo implements ApppRenderGameOverlayEvent {
		private static ListenerList list;
		
		public BossInfo(MatrixStack mStack, RenderGameOverlayEvent parent, ElementType type, ClientBossInfo bossInfo, int x, int y, int increment) {
			super(mStack, parent, type, bossInfo, x, y, increment);
			if (list == null)
				aquireListenerList();
		}
		
		@Override
		public ListenerList getListenerList() {
			return list;
		}
		
		@Override
		public void setListenerList(ListenerList llist) {
			list = llist;
		}
	}
	
	public static class Text extends RenderGameOverlayEvent.Text implements ApppRenderGameOverlayEvent {
		private static ListenerList list;
		
		public Text(MatrixStack mStack, RenderGameOverlayEvent parent, ArrayList<String> left, ArrayList<String> right) {
			super(mStack, parent, left, right);
			if (list == null)
				aquireListenerList();
		}
		
		@Override
		public ListenerList getListenerList() {
			return list;
		}
		
		@Override
		public void setListenerList(ListenerList llist) {
			list = llist;
		}
	}
	
	public static class Chat extends RenderGameOverlayEvent.Chat implements ApppRenderGameOverlayEvent {
		private static ListenerList list;
		
		public Chat(MatrixStack mStack, RenderGameOverlayEvent parent, int posX, int posY) {
			super(mStack, parent, posX, posY);
			if (list == null)
				aquireListenerList();
		}
		
		@Override
		public ListenerList getListenerList() {
			return list;
		}
		
		@Override
		public void setListenerList(ListenerList llist) {
			list = llist;
		}
	}
}
