package dev.cheos.armorpointspp.core.texture;

import dev.cheos.armorpointspp.core.RenderContext;

public final class VanillaTextureSheet implements ITextureSheet {
	@Override public int texHeight() { return 256; } // vanilla texture is square
	
	@Override
	public void drawOverlay(RenderContext ctx, int x, int y, boolean half, boolean hardcore, OverlaySprite sprite) {
		if (sprite == OverlaySprite.FROSTBITE || sprite == OverlaySprite.FROSTBITE_FULL || sprite == OverlaySprite.FROSTBITE_ICON)
			blit(ctx, x, y, half ? 187 : 178, hardcore ? 45 : 0);
		else throw new UnsupportedOperationException("Appp overlays not available with vanilla texture");
	}
	
	@Override
	public void drawHeartBG(RenderContext ctx, int x, int y, boolean bright) {
		blit(ctx, x, y, bright ? 25 : 16, 0);
	}
	
	@Override
	public void drawAbsorb(RenderContext ctx, int x, int y, int amount, boolean bright) {
		throw new UnsupportedOperationException("Appp absorption overlay not available with vanilla texture");
	}
	
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
	public void drawToughness(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean icon) {
		throw new UnsupportedOperationException("Appp toughness not available with vanilla texture");
	}

	@Override
	public void drawMagicShield(RenderContext ctx, int x, int y, int spriteLevel) {
		throw new UnsupportedOperationException("Appp magic resistance overlay (potioncore) not available with vanilla texture");
	}
	
	@Override
	public String texLocation() {
		return "minecraft:textures/gui/icons.png";
	}
}
