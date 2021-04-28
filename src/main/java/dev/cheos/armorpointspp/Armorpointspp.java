package dev.cheos.armorpointspp;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.render.RenderGameOverlayHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(Armorpointspp.MODID)
public class Armorpointspp {
	public static final String MODID = "armorpointspp";
	private static final Logger LOGGER = LogManager.getLogger("Armorpoints++");
	
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
	
	private void checkCompat() {
		if (ModList.get().isLoaded("colorfulhealthbar")) {
			LOGGER.warn("-=================================================================-");
			LOGGER.warn("NOTICE: ColorfulHealthBar is installed!");
			logIncompatible();
		}
		
		if (ModList.get().isLoaded("overloadedarmorbar")) {
			LOGGER.warn("-=================================================================-");
			LOGGER.warn("NOTICE: OverloadedArmorBar is installed!");
			logIncompatible();
		}
	}
	
	private void logIncompatible() {
		LOGGER.warn("");
		LOGGER.warn("NOTICE: Due to the way THAT mod is made,");
		LOGGER.warn("NOTICE: it can cause major incompatibilities");
		LOGGER.warn("NOTICE: with Armorpoints++, even if some of");
		LOGGER.warn("NOTICE: THIS mod's features are disabled via config.");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: Armorpoints++ IS NOT responsible for any issues that occur!");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: Thus you will get NO SUPPORT if you are using this mod");
		LOGGER.warn("NOTICE: in combination with the mod named above!");
		LOGGER.warn("-=================================================================-");
	}
}
