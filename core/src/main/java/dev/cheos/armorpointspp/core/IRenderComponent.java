package dev.cheos.armorpointspp.core;

import java.util.Random;

import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public interface IRenderComponent {
	static final Random RANDOM = new Random();
	default Random random() { return RANDOM; }
	default ITextureSheet tex(RenderContext ctx) { return ITextureSheet.currentSheet(ctx); }
	default boolean popReturn(RenderContext ctx, boolean ret) { ctx.profiler.pop(); return ret; }
	
	/**
	 * @return true if the component did render, else false
	 */
	boolean render(RenderContext ctx);
}
