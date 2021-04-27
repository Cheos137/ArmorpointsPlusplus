package dev.cheos.armorpointspp;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.render.RenderGameOverlayHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(Armorpointspp.MODID)
public class Armorpointspp {
	public static final String MODID = "armorpointspp";
	private static final Logger LOGGER = LogManager.getLogger();
	
	public Armorpointspp() {
		ModLoadingContext.get().registerExtensionPoint(
				ExtensionPoint.DISPLAYTEST,
				() -> Pair.of(
						() -> FMLNetworkConstants.IGNORESERVERONLY,
						(a, b) -> true));
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
		ApppConfig.init();
	}
	
	private void client(FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new RenderGameOverlayHandler(event.getMinecraftSupplier().get()));
		LOGGER.info("oh hi there... :)");
		LOGGER.info("I heared you wanted some fancy health/armor bars?");
	}
}
