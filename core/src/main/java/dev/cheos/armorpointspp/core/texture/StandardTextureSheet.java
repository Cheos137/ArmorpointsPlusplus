package dev.cheos.armorpointspp.core.texture;

import dev.cheos.armorpointspp.core.RenderContext;

public class StandardTextureSheet implements ITextureSheet {
	private final String location;
	
	public StandardTextureSheet(String location) {
		this.location = location;
	}
	
	@Override
	public void drawOverlay(RenderContext ctx, int x, int y, boolean half, boolean hardcore, OverlaySprite sprite) {
		switch (sprite) {
			case FROSTBITE:
				blit(ctx, x, y, (half ? 9 : 0) + (hardcore ? 18 : 0), 108);
				break;
			case FROSTBITE_FULL:
				blit(ctx, x, y, (half ? 9 : 0) + (hardcore ? 18 : 0), 117);
				break;
			case FROSTBITE_ICON:
				ctx.poseStack.pushPose();
				ctx.poseStack.scale(0.5F, 0.5F, 1);
				blit(ctx, 2 * x + 8, 2 * y + 1, 36, 108);
				ctx.poseStack.popPose();
				break;
			case PROTECTION:
				blit(ctx, x, y,  9, 9);
				break;
			case RESISTANCE_FULL:
				blit(ctx, x, y, 18, 0);
				break;
			case RESISTANCE_HALF:
				blit(ctx, x, y,  9, 0);
				break;
			case RESISTANCE_NONE:
				blit(ctx, x, y,  0, 0);
				break;
			case TOUGHNESS_ICON:
				ctx.poseStack.pushPose();
				ctx.poseStack.scale(0.5F, 0.5F, 1);
				blit(ctx, 2 * (x) + 9, 2 * y + 8, 27.5F, 9);
				// u should be 27px, but when scaled by 0.5, 27.5px somehow works better (27px includes a weird white border from the lefthand sprite)
				// v should be  9px which i deemed to be the best option, even when scaled
				ctx.poseStack.popPose();
				break;
			default:
				throw new IllegalArgumentException("Unknown overlay sprite: " + sprite.name());
		}
	}
	
	@Override
	public void drawHeartBG(RenderContext ctx, int x, int y, boolean bright) {
		blit(ctx, x, y, bright ? 18 : 0, 9);
	}
	
	@Override
	public void drawAbsorb(RenderContext ctx, int x, int y, int amount, boolean bright) {
		blit(ctx, x, y, (bright ? 18 : 0) + 9 * (amount % 2), 9 + 9 * amount);
	}
	
	@Override
	public void drawArmor(RenderContext ctx, int x, int y, int spriteLevel, boolean half) {
		blit(ctx, x, y, (half ? 18 : 27) + 18 * spriteLevel, 0);
	}
	
	@Override
	public void drawHeart(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, HeartStyle style) {
		blit(ctx,
				x,
				y,
				(hardcore ? 144 : 36)
				+ (half ? 9 : 0)
				+ (bright ? 18 : 0)
				+ (style == HeartStyle.POISON ? 36 : style == HeartStyle.WITHER ? 72 : 0),
				9 + 9 * spriteLevel);
	}
	
	@Override
	public String texLocation() {
		return this.location;
	}
}
