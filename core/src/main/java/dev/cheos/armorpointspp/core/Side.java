package dev.cheos.armorpointspp.core;

import dev.cheos.armorpointspp.core.RenderableText.Alignment;

public enum Side {
	RIGHT(Alignment.LEFT),
	LEFT(Alignment.RIGHT);
	
	private final Alignment preferredTextAlignment;
	Side(Alignment preferredTextAlignment) {
		this.preferredTextAlignment = preferredTextAlignment;
	}
	
	public Alignment preferredAlignment() {
		return this.preferredTextAlignment;
	}
}
