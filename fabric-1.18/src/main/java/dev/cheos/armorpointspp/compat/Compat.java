package dev.cheos.armorpointspp.compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compat {
	private static boolean fabricApi, appleskin, spectrum;
	
	public static void init() {
		fabricApi = FabricLoader.getInstance().isModLoaded("fabric");
		appleskin = FabricLoader.getInstance().isModLoaded("appleskin");
		spectrum = FabricLoader.getInstance().isModLoaded("spectrum");
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
}
