package dev.cheos.armorpointspp.core.texture;

import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;

import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.StringOption;

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
		ctx.renderer.blit(ctx.poseStack, x, y, u, v, spriteWidth(), spriteHeight(), texWidth(), texHeight()); return this;
	}
	
	
	static final BiMap<String, ITextureSheet> sheets = HashBiMap.create();
	static final ITextureSheet vanillaSheet = register("vanilla", new VanillaTextureSheet());
	static final ITextureSheet defaultSheet = register("default", new StandardTextureSheet("icons"));
	static final List<ITextureSheet> builtins = ImmutableList.of(defaultSheet); // only to be used for config option, does not contain vanilla sheet
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
		RESISTANCE_FULL,
		TOUGHNESS_ICON;
	}
	
	public static enum HeartStyle {
		NORMAL,
		POISON,
		WITHER;
	}
}
