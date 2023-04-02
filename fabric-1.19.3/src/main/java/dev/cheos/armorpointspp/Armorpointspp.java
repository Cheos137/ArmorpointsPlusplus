package dev.cheos.armorpointspp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.cheos.armorpointspp.compat.Compat;
import dev.cheos.armorpointspp.compat.FabricAPISafeAccess;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;

/**
 * https://github.com/FabLabsMC/fiber
 * https://fabricmc.net/wiki/documentation:libraries
 * https://fabricmc.net/wiki/documentation:fabric_loom
 */
public class Armorpointspp {
	public static final String MODID = "armorpointspp";
	public static final Logger LOGGER = LoggerFactory.getLogger("Armorpoints++");
	
	public void client() {
		Compat.init();
		// register login listener to reload config on world load
		if (Compat.isFabricApiLoaded())
			FabricAPISafeAccess.registerClientLoginInitEventListener(Armorpointspp::onLogin);
		
		// load config
		ApppConfig.init();
		
		// stuff not relevant for functionality
		LOGGER.info("oh hi there... :)");
		LOGGER.info("I heared you wanted some fancy health/armor bars?");
		if (!ApppConfig.instance().bool(BooleanOption.HIDE_COMPAT_WARNINGS)) checkCompat();
	}
	
	private void checkCompat() {
//		checkIncompatible("colorfulhealthbar", "ColorfulHealthBar");
//		checkIncompatible("overloadedarmorbar", "OverloadedArmorBar");
//		checkIncompatible("mantle", "Mantle");
	}
	
	@SuppressWarnings("unused")
	private void checkIncompatible(String modId, String name) {
		if (FabricLoader.getInstance().isModLoaded(modId)) logIncompatible(name);
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
	
	
	private static void onLogin(ClientHandshakePacketListenerImpl handler, Minecraft client) {
		ApppConfig.load();
		ApppConfig.save();
	}
}
