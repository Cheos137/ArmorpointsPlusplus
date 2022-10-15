package dev.cheos.armorpointspp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cheos.armorpointspp.compat.MantleCompat;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = Armorpointspp.MODID,
	 name = "Armorpoints++",
	 clientSideOnly = true,
	 acceptableRemoteVersions = "*",
	 acceptedMinecraftVersions = "[1.12,1.12.2]",
	 dependencies = "after:mantle")
public class Armorpointspp {
	public static final String MODID = "armorpointspp";
	public static final Logger LOGGER = LogManager.getLogger("Armorpoints++");
	public static boolean MANTLE;
	public static boolean POTIONCORE;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ApppConfig.init(event.getSuggestedConfigurationFile());
		MANTLE = Loader.isModLoaded("mantle");
		POTIONCORE = Loader.isModLoaded("potioncore");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		LOGGER.info("oh hi there... :)");
		LOGGER.info("I heared you wanted some fancy health/armor bars?");
		if (!ApppConfig.instance().bool(BooleanOption.HIDE_COMPAT_WARNINGS)) checkCompat();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (MANTLE && ApppConfig.instance().bool(BooleanOption.MANTLE_COMPAT))
			MantleCompat.hackMantle();
	}
	
	private void checkCompat() {
		checkIncompatible("colorfulhealthbar", "ColorfulHealthBar");
		checkIncompatible("overloadedarmorbar", "OverloadedArmorBar");
		checkIncompatible("mantle", "Mantle");
	}
	
	private void checkIncompatible(String modId, String name) {
		if (Loader.isModLoaded(modId)) logIncompatible(name);
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
}
