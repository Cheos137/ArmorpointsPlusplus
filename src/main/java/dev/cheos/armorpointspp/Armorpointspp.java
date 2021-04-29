package dev.cheos.armorpointspp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.render.RenderGameOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Armorpointspp.MODID,
	 name = "Armorpoints++",
	 version = "v2.0.0-rc1",
	 clientSideOnly = true,
	 acceptableRemoteVersions = "*",
	 acceptedMinecraftVersions = "[1.12,1.12.2]")
public class Armorpointspp {
	private static boolean attributefix;
	public static final String MODID = "armorpointspp";
	private static final Logger LOGGER = LogManager.getLogger("Armorpoints++");
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		attributefix = Loader.isModLoaded("attributefix");
		ApppConfig.init();
		checkCompat();
	}
	
	@EventHandler
	private void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RenderGameOverlayHandler(Minecraft.getMinecraft()));
		LOGGER.info("oh hi there... :)");
		LOGGER.info("I heared you wanted some fancy health/armor bars?");
	}
	
	public static boolean isAttributeFixLoaded() {
		return attributefix;
	}
	
	private void checkCompat() {
		if (Loader.isModLoaded("colorfulhealthbar")) {
			LOGGER.warn("-=================================================================-");
			LOGGER.warn("NOTICE: ColorfulHealthBar is installed!");
			logIncompatible();
		}
		
		if (Loader.isModLoaded("overloadedarmorbar")) {
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
