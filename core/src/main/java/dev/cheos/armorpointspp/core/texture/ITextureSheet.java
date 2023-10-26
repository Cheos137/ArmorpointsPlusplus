package dev.cheos.armorpointspp.core.texture;

import java.util.List;

import com.google.common.collect.*;

import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.SpriteInfo;
import dev.cheos.armorpointspp.core.adapter.IConfig.StringOption;

public interface ITextureSheet {
	void drawOverlay    (RenderContext ctx, int x, int y,                  boolean half,                 boolean hardcore, OverlaySprite sprite); //
	void drawHeartBG    (RenderContext ctx, int x, int y,                                boolean bright, boolean hardcore);                      //
	void drawAbsorb     (RenderContext ctx, int x, int y, int amount,                    boolean bright);                                       // 0 -> nothing
	void drawArmor      (RenderContext ctx, int x, int y, int spriteLevel, boolean half);                                                      // 0 -> "empty" background
	void drawHeart      (RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, HeartStyle style); // 0 -> nothing
	void drawAbsorbHeart(RenderContext ctx, int x, int y,                  boolean half, boolean right , boolean hardcore);                  //
	void drawToughness  (RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean icon  , boolean mirror);                   //
	void drawMagicShield(RenderContext ctx, int x, int y, int spriteLevel);                                                                // 0 -> lowest level
	
	String texLocation();
	
	default int spriteWidth()  { return   9; } // scaled sprite width
	default int spriteHeight() { return   9; } // scaled sprite height
	default int texWidth()     { return 256; } // assumed tex width  (scaled if other)
	default int texHeight()    { return 128; } // assumed tex height (scaled if other)
	
	default int getMaxHealthLevel()    { return 10; }
	default int getMaxArmorLevel()     { return 12; }
	default int getMaxToughnessLevel() { return 12; }
	
	default ITextureSheet bind(RenderContext ctx) { ctx.renderer.setupTexture(this); return this; }
	default ITextureSheet blit(RenderContext ctx, int x, int y, float u, float v, int w, int h) {
		ctx.renderer.blit(ctx.poseStack, x, y, u, v, w, h, texWidth(), texHeight()); return this;
	}
	default ITextureSheet blit(RenderContext ctx, int x, int y, float u, float v) {
		ctx.renderer.blit(ctx.poseStack, x, y, u, v, spriteWidth(), spriteHeight(), texWidth(), texHeight()); return this;
	}
	default ITextureSheet blitSprite(RenderContext ctx, int x, int y, SpriteInfo sprite) {
		ctx.renderer.blitSprite(ctx.poseStack, x, y, spriteWidth(), spriteHeight(), sprite); return this;
	}
	default ITextureSheet blitSprite(RenderContext ctx, int x, int y, int width, int height, SpriteInfo sprite, int uOffset, int vOffset) {
		ctx.renderer.blitSprite(ctx.poseStack, x, y, width, height, sprite, uOffset, vOffset, spriteWidth(), spriteHeight()); return this;
	}
	default ITextureSheet blitM(RenderContext ctx, int x, int y, float u, float v, int w, int h) {
		ctx.renderer.blitM(ctx.poseStack, x, y, u, v, w, h, texWidth(), texHeight()); return this;
	}
	default ITextureSheet blitM(RenderContext ctx, int x, int y, float u, float v) {
		ctx.renderer.blitM(ctx.poseStack, x, y, u, v, spriteWidth(), spriteHeight(), texWidth(), texHeight()); return this;
	}
	
	
	static final BiMap<String, ITextureSheet> sheets = HashBiMap.create();
	static final ITextureSheet vanillaSheet = register("vanilla", new VanillaTextureSheet());
	static final ITextureSheet defaultSheet = register("default", new StandardTextureSheet("icons"));
	static final ITextureSheet hallowSheet  = register("hallow" , new StandardTextureSheet("hallow"));
	static final List<ITextureSheet> builtins = ImmutableList.of(defaultSheet, hallowSheet, vanillaSheet); // only to be used for config option, does not contain vanilla sheet
	// TODO add other builtin texture sheets
	public static ITextureSheet vanillaSheet() { return vanillaSheet; }
	public static ITextureSheet defaultSheet() { return defaultSheet; }
	
	public static ITextureSheet currentSheet(RenderContext ctx) {
		return sheets.computeIfAbsent(ctx.config.str(StringOption.TEXTURE_SHEET), id -> register(new StandardTextureSheet(id)));
	}
	
	static ITextureSheet register(ITextureSheet sheet) {
		return register(sheet.texLocation(), sheet);
	}
	
	static ITextureSheet register(String id, ITextureSheet sheet) {
		if (sheets.putIfAbsent(id, sheet) != null)
			throw new IllegalStateException("Tried to register a texture sheet with the same id twice");
		return sheet;
	}
	
	
	public static enum OverlaySprite {
		FROSTBITE,
		FROSTBITE_FULL,
		FROSTBITE_ICON,
		PROTECTION,
		RESISTANCE_NONE,
		RESISTANCE_HALF,
		RESISTANCE_FULL;
	}
	
	public static enum HeartStyle {
		NORMAL,
		POISON,
		WITHER;
	}
}
