package dev.cheos.armorpointspp.compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compat {
	private static boolean libhud, fabricApi, appleskin, spectrum, raised;
	
	public static void init() {
		libhud = FabricLoader.getInstance().isModLoaded("libhud");
		fabricApi = FabricLoader.getInstance().isModLoaded("fabric");
		appleskin = FabricLoader.getInstance().isModLoaded("appleskin");
		spectrum = FabricLoader.getInstance().isModLoaded("spectrum");
		raised = FabricLoader.getInstance().isModLoaded("raised");
	}
	
	public static boolean isLibhudLoaded() {
		return libhud;
	}
	
	public static boolean isFabricApiLoaded() {
		return fabricApi;
	}
	
	public static boolean isAppleskinLoaded() {
		return appleskin;
	}
	
	public static boolean isSpectrumLoaded() {
		return spectrum;
	}
	
	public static boolean isRaisedLoaded() {
		return raised;
	}
}
