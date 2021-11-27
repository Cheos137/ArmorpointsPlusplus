package dev.cheos.armorpointspp.core;

import java.util.Random;

import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public interface IRenderComponent {
	static final Random RANDOM = new Random();
	default Random random() { return RANDOM; }
	default ITextureSheet tex(RenderContext ctx) { return ITextureSheet.currentSheet(ctx); }
	
	void render(RenderContext ctx);
}
