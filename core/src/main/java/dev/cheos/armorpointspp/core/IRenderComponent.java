package dev.cheos.armorpointspp.core;

import java.util.Random;

public interface IRenderComponent {
	static final Random RANDOM = new Random();
	default Random random() { return RANDOM; }
	
	void render(RenderContext ctx);
}
