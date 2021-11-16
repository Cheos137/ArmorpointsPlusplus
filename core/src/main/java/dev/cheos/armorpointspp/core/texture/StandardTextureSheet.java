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
				ctx.renderer.blit(ctx.poseStack, x, y, (half ? 9 : 0) + (hardcore ? 18 : 0), 108, 9, 9);
				break;
			case FROSTBITE_FULL:
				ctx.renderer.blit(ctx.poseStack, x, y, (half ? 9 : 0) + (hardcore ? 18 : 0), 117, 9, 9);
				break;
			case FROSTBITE_ICON:
				ctx.poseStack.pushPose();
				ctx.poseStack.scale(0.5F, 0.5F, 1);
				ctx.renderer.blit(ctx.poseStack, 2 * x + 8, 2 * y + 1, 36, 108, 9, 9);
				ctx.poseStack.popPose();
				break;
			case PROTECTION:
				ctx.renderer.blit(ctx.poseStack, x, y,  9, 9, 9, 9);
				break;
			case RESISTANCE_FULL:
				ctx.renderer.blit(ctx.poseStack, x, y, 18, 0, 9, 9);
				break;
			case RESISTANCE_HALF:
				ctx.renderer.blit(ctx.poseStack, x, y,  9, 0, 9, 9);
				break;
			case RESISTANCE_NONE:
				ctx.renderer.blit(ctx.poseStack, x, y,  0, 0, 9, 9);
				break;
			case TOUGHNESS_ICON:
				ctx.poseStack.pushPose();
				ctx.poseStack.scale(0.5F, 0.5F, 1);
				ctx.renderer.blit(ctx.poseStack, 2 * (x) + 9, 2 * y + 8, 27, 9, 9, 9);
				ctx.poseStack.popPose();
				break;
			default:
				throw new IllegalArgumentException("Unknown overlay sprite: " + sprite.name());
		}
	}
	
	@Override
	public void drawHeartBG(RenderContext ctx, int x, int y, boolean bright) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawAbsorb(RenderContext ctx, int x, int y, int amount, boolean bright) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawArmor(RenderContext ctx, int x, int y, int spriteLevel, boolean half) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawHeart(RenderContext ctx, int x, int y, int spriteLevel, boolean half, boolean bright, boolean hardcore, HeartStyle style) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String texLocation() {
		return this.location;
	}
	
}
