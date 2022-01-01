package dev.cheos.armorpointspp.compat;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class MantleCompat {
	private static final Logger LOGGER = LogManager.getLogger("Armorpoints++ [mantle-compat]");
	private static final String MANTLE_HEALTH_RENDERER_CLASS = "slimeknights.mantle.client.ExtraHeartRenderHandler";
	
	public static void hackMantle() {
		try {
			LOGGER.info("Trying to fix mantle compat (trying to remove mantle's extraheart-renderer)...");
			boolean success = false;
			ConcurrentHashMap<Object, ?> listeners = ReflectionHelper.getPrivateValue(EventBus.class, "listeners", MinecraftForge.EVENT_BUS);
			
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
