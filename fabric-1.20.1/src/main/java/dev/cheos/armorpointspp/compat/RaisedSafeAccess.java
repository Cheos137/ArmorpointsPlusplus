package dev.cheos.armorpointspp.compat;

import net.fabricmc.loader.api.FabricLoader;

public class RaisedSafeAccess {
	public static int getDistance() {
		return FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer i
				? i
				: FabricLoader.getInstance().getObjectShare().get("raised:distance") instanceof Integer j
						? j
						: 0;
	}
}
