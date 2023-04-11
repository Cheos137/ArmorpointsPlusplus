package dev.cheos.armorpointspp.core.texture;

import dev.cheos.armorpointspp.core.RenderContext;

public final class VanillaTextureSheet implements ITextureSheet {
	@Override public int texHeight() { return 256; } // vanilla texture is square
	
	@Override
	public void drawOverlay(RenderContext ctx, int x, int y, boolean half, boolean hardcore, OverlaySprite sprite) {
		if (sprite == OverlaySprite.FROSTBITE || sprite == OverlaySprite.FROSTBITE_FULL || sprite == OverlaySprite.FROSTBITE_ICON)
			blit(ctx, x, y, half ? 187 : 178, hardcore ? 45 : 0);
	}
	
	@Override
	public void drawHeartBG(RenderContext ctx, int x, int y, boolean bright) {
		blit(ctx, x, y, bright ? 25 : 16, 0);
	}
	
	@Override
	public void drawAbsorb(RenderContext ctx, int x, int y, int amount, boolean bright) { }
	
	@Override
	public void drawArmor(RenderContext ctx, int x, int y, int spriteLevel, boolean half) {
		blit(ctx, x, y, spriteLevel < 1 ? 16 : half ? 25 : 34, 9);
	}
	
	@Override
	public void drawHeart(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, HeartStyle style) {
		blit(ctx, x, y, (half ? 9 : 0) + (bright ? 18 : 0) + (style == HeartStyle.WITHER
				? 124
				: style == HeartStyle.POISON
					? 88
					: 52),
				hardcore ? 45 : 0);
	}
	
	@Override
	public void drawAbsorbHeart(RenderContext ctx, int x, int y, boolean half, boolean right, boolean hardcore) {
		blit(ctx ,
				x,
				y,
				160
				+ (half && right ? 5 : 0),
				(hardcore ? 45 : 0),
				half ? right ? 4 : 5 : spriteWidth(),
				9);
	}
	
	@Override
	public void drawToughness(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean icon, boolean mirror) { }
	
	@Override
	public void drawMagicShield(RenderContext ctx, int x, int y, int spriteLevel) { }
	
	@Override
	public String texLocation() {
		return "minecraft:textures/gui/icons.png";
	}
	
	@Override public int getMaxHealthLevel() { return 1; }
	@Override public int getMaxArmorLevel() { return 1; }
	@Override public int getMaxToughnessLevel() { return 1; }
}
