package dev.cheos.armorpointspp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cheos.armorpointspp.config.ApppConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(Armorpointspp.MODID)
@EventBusSubscriber(Dist.CLIENT)
public class Armorpointspp {
	public static final String MODID = "armorpointspp";
	public static final Logger LOGGER = LogManager.getLogger("Armorpoints++");
	
	public Armorpointspp() {
		ModLoadingContext.get().registerExtensionPoint(
				IExtensionPoint.DisplayTest.class,
				() -> new IExtensionPoint.DisplayTest(
						() -> NetworkConstants.IGNORESERVERONLY,
						(remote, isServer) -> true));
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
		ApppConfig.init();
		checkCompat();
	}
	
	private void client(FMLClientSetupEvent event) {
		Overlays.init();
		LOGGER.info("oh hi there... :)");
		LOGGER.info("I heared you wanted some fancy health/armor bars?");
	}
	
	private void checkCompat() {
		checkIncompatible("colorfulhealthbar", "ColorfulHealthBar");
		checkIncompatible("overloadedarmorbar", "OverloadedArmorBar");
		checkIncompatible("mantle", "Mantle");
	}
	
	private void checkIncompatible(String modId, String name) {
		if (ModList.get().isLoaded(modId)) logIncompatible(name);
	}
	
	private void logIncompatible(String mod) {
		LOGGER.warn("-=================================================================-");
		LOGGER.warn("NOTICE: [" + mod + "] is installed!");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: Due to the way THAT mod is made,");
		LOGGER.warn("NOTICE: it CAN cause major incompatibilities");
		LOGGER.warn("NOTICE: with Armorpoints++, even if some of");
		LOGGER.warn("NOTICE: THIS mod's features are disabled via config.");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: Armorpoints++ IS NOT responsible for any issues that occur!");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: Thus you will get NO SUPPORT if you are using this mod");
		LOGGER.warn("NOTICE: in combination with the mod named above!");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: This warning can be safely ignored if you can say for sure");
		LOGGER.warn("NOTICE: that conflicting features of the mod named above");
		LOGGER.warn("NOTICE: are fully disabled or the mod named above is not installed.");
		LOGGER.warn("");
		LOGGER.warn("NOTICE: You can usually solve this issue by simply removing [" + mod + "].");
		LOGGER.warn("NOTICE: If that is not an option for you, please double-check your");
		LOGGER.warn("NOTICE: configuration files to make sure everything works.");
		LOGGER.warn("-=================================================================-");
	}
	
	@SubscribeEvent
	public static void onLogin(LoggedInEvent event) { // seems to run on main thread -> no sync problems here
		ApppConfig.instance().invalidateAll();
	}
}
