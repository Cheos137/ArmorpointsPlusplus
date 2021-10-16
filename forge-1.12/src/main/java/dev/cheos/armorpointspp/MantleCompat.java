package dev.cheos.armorpointspp;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class MantleCompat {
	private static final Logger LOGGER = LogManager.getLogger("Armorpoints++ [mantle-compat]");
	private static final String MANTLE_HEALTH_RENDERER_CLASS = "slimeknights.mantle.client.ExtraHeartRenderHandler";
	
	@SuppressWarnings("unchecked")
	public static void hackMantle() { // TODO config option
		try {
			LOGGER.info("Trying to fix mantle compat (trying to remove mantle's extraheart-renderer)...");
			boolean success = false;
			EventBus bus = MinecraftForge.EVENT_BUS;
			
			Field listenersField = EventBus.class.getDeclaredField("listeners");
			listenersField.setAccessible(true);
			ConcurrentHashMap<Object, ?> listeners = (ConcurrentHashMap<Object, ?>) listenersField.get(bus);
			
			for (Object renderer : listeners.keySet()) {
				if (MANTLE_HEALTH_RENDERER_CLASS.equals(renderer.getClass().getName())) {
					LOGGER.info("> removing asm listener: " + renderer.getClass().getName());
					MinecraftForge.EVENT_BUS.unregister(renderer);
					success = true;
				}
			}
			
			LOGGER.info(success ? "...succeeded" : "...failed");
		} catch (Throwable t) {
			LOGGER.warn("...failed", t);
		}
	}
}
