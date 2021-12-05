package dev.cheos.armorpointspp.core.texture;

import dev.cheos.armorpointspp.core.RenderContext;

public class StandardTextureSheet implements ITextureSheet { // TODO magic shield overlay
	private final String location;
	
	public StandardTextureSheet(String location) {
		this.location = location;
	}
	
	@Override
	public void drawOverlay(RenderContext ctx, int x, int y, boolean half, boolean hardcore, OverlaySprite sprite) {
		switch (sprite) {
			case FROSTBITE:
				blit(ctx, x, y,      (half ? 9 : 0) + (hardcore ? 18 : 0), 117);
				break;
			case FROSTBITE_FULL:
				blit(ctx, x, y, 36 + (half ? 9 : 0) + (hardcore ? 18 : 0), 117);
				break;
			case FROSTBITE_ICON:
				ctx.poseStack.pushPose();
				ctx.poseStack.scale(0.5F, 0.5F, 1);
				blit(ctx, 2 * x + 8, 2 * y + 1, 27, 18);
				ctx.poseStack.popPose();
				break;
			case PROTECTION:
				blit(ctx, x, y,  9, 18);
				break;
			case RESISTANCE_FULL:
				blit(ctx, x, y, 18,  0);
				break;
			case RESISTANCE_HALF:
				blit(ctx, x, y,  9,  0);
				break;
			case RESISTANCE_NONE:
				blit(ctx, x, y,  0,  0);
				break;
			default:
				throw new IllegalArgumentException("Unknown overlay sprite: " + sprite.name());
		}
	}
	
	@Override
	public void drawHeartBG(RenderContext ctx, int x, int y, boolean bright) {
		blit(ctx, x, y, bright ? 18 : 0, 18);
	}
	
	@Override
	public void drawAbsorb(RenderContext ctx, int x, int y, int amount, boolean bright) {
		amount = ctx.math.clamp(amount, 1, 20);
		blit(ctx, x, y, (bright ? 18 : 0) + 9 * (amount % 2), 18 + 9 * ((amount + 1) / 2));
	}
	
	@Override
	public void drawArmor(RenderContext ctx, int x, int y, int spriteLevel, boolean half) {
		if (spriteLevel >= 12) half = false;
		blit(ctx, x, y, (half ? 36 : 27) + 18 * ctx.math.clamp(spriteLevel, 0, 12), 0);
	}
	
	@Override
	public void drawHeart(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, HeartStyle style) {
		if (spriteLevel >= 10) half = false;
		blit(ctx ,
				x,
				y,
				(hardcore ? 144 : 36)
				+ (half ? 9 : 0)
				+ (bright ? 18 : 0)
				+ (style == HeartStyle.POISON ? 36 : style == HeartStyle.WITHER ? 72 : 0),
				18 + 9 * ctx.math.clamp(spriteLevel, 0, 10));
	}
	
	@Override
	public void drawToughness(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean icon) {
		if (spriteLevel >= 12) half = false;
		if (icon) {
			ctx.poseStack.pushPose();
			ctx.poseStack.scale(0.5F, 0.5F, 1);
			blit(ctx, 2 * (x) + 9, 2 * y + 8, 45.5F + 18 * ctx.math.clamp(spriteLevel, 0, 11), 9); // no empty icons -> 0 = silver icon
			// u should be 45px, but when scaled by 0.5, 45.5px somehow works better (45px includes a weird border from the lefthand sprite)
			// v should be  9px which i deemed to be the best option, even when scaled
			ctx.poseStack.popPose();
		} else blit(ctx, x, y, (half ? 36 : 27) + 18 * ctx.math.clamp(spriteLevel, 0, 12), 9);
	}
	
	@Override
	public String texLocation() {
		return this.location;
	}
}
