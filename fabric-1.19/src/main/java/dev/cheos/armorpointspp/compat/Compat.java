package dev.cheos.armorpointspp.compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compat {
	private static boolean libhud, fabricApi, appleskin, spectrum, raised, bewitchment, dehydration, betterMountHud;
	
	public static void init() {
		libhud = FabricLoader.getInstance().isModLoaded("libhud");
		fabricApi = FabricLoader.getInstance().isModLoaded("fabric");
		appleskin = FabricLoader.getInstance().isModLoaded("appleskin");
		spectrum = FabricLoader.getInstance().isModLoaded("spectrum");
		raised = FabricLoader.getInstance().isModLoaded("raised");
		bewitchment = FabricLoader.getInstance().isModLoaded("bewitchment");
		dehydration = FabricLoader.getInstance().isModLoaded("dehydration");
		betterMountHud = FabricLoader.getInstance().isModLoaded("bettermounthud");
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
	
	public static boolean isBewitchmentLoaded() {
		return bewitchment;
	}
	
	public static boolean isDehydrationLoaded() {
		return dehydration;
	}
	
	public static boolean isBetterMountHudLoaded() {
		return betterMountHud;
	}
}
