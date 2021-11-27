package dev.cheos.armorpointspp.core.texture;

import java.util.ArrayList;
import java.util.List;

import dev.cheos.armorpointspp.core.RenderContext;

public interface ITextureSheet {
	void drawOverlay(RenderContext ctx, int x, int y,                  boolean half,                 boolean hardcore, OverlaySprite sprite); //
	void drawHeartBG(RenderContext ctx, int x, int y,                                boolean bright);                                        //
	void drawAbsorb (RenderContext ctx, int x, int y, int amount,                    boolean bright);                                       // 0 -> nothing
	void drawArmor  (RenderContext ctx, int x, int y, int spriteLevel, boolean half);                                                      // 0 -> "empty" background
	void drawHeart  (RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, HeartStyle style); // 0 -> nothing
	
	String texLocation();
	
	default int spriteWidth()  { return   9; } // scaled sprite width
	default int spriteHeight() { return   9; } // scaled sprite height
	default int texWidth()     { return 256; } // assumed tex width  (scaled if other)
	default int texHeight()    { return 128; } // assumed tex height (scaled if other)
	
	default ITextureSheet bind(RenderContext ctx) { ctx.renderer.setupTexture(this); return this; }
	default ITextureSheet blit(RenderContext ctx, int x, int y, float u, float v) {
		ctx.renderer.blit(ctx.poseStack, x, y, u, v, spriteWidth(), spriteHeight()); return this;
	}
	
	static final List<ITextureSheet> sheets = new ArrayList<>();
	static final ITextureSheet defaultSheet = new StandardTextureSheet("icons");
	// TODO add other builtin texture sheets
	public static ITextureSheet defaultSheet() { return defaultSheet; }
	
	public static ITextureSheet currentSheet(RenderContext ctx) {
		return defaultSheet(); // TODO actually implement
	}
	
	
	public static enum OverlaySprite {
		FROSTBITE,
		FROSTBITE_FULL,
		FROSTBITE_ICON,
		PROTECTION,
		RESISTANCE_NONE,
		RESISTANCE_HALF,
		RESISTANCE_FULL,
		TOUGHNESS_ICON;
	}
	
	public static enum HeartStyle {
		NORMAL,
		POISON,
		WITHER;
	}
}
