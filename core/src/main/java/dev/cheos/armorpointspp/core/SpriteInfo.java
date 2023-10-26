package dev.cheos.armorpointspp.core;

import java.util.HashMap;
import java.util.Map;

public class SpriteInfo {
	private static final Map<Integer, SpriteInfo> UV_LOOKUP = new HashMap<>();
	
	private final String location;
	private final int u, v;
	
	public SpriteInfo(String location, int u, int v) {
		this.location = location;
		this.u = u;
		this.v = v;
		UV_LOOKUP.put((u << 16) | v, this);
	}
	
	public String location() {
		return this.location;
	}
	
	public int u() {
		return this.u;
	}
	
	public int v() {
		return this.v;
	}
	
	public static SpriteInfo byUV(int u, int v) {
		return UV_LOOKUP.get((u << 16) | v);
	}
}
