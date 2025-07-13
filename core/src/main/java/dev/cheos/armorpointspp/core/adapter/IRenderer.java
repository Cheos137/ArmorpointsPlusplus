package dev.cheos.armorpointspp.core.adapter;

import dev.cheos.armorpointspp.core.SpriteInfo;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public interface IRenderer {
	void blit(IPoseStack poseStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight);
	void blit(IPoseStack poseStack, int x, int y, float u, float v, int width, int height);
	void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite);
	void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite, int uOffset, int vOffset, int spriteWidth, int spriteHeight);
	void blitM(IPoseStack poseStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight);
	void blitM(IPoseStack poseStack, int x, int y, float u, float v, int width, int height);
	default void setColor(int color) {
		setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
	}
	default void setColorRGB(int color) {
		setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, 1);
	}
	void setColor(float r, float g, float b, float a);
	void setupAppp();
	void setupTexture(ITextureSheet texSheet);
	void setupVanilla();
	default void text(IPoseStack poseStack, String text, float x, float y) { text(poseStack, text, x, y, 0); }
	default void text(IPoseStack poseStack, String text, float x, float y, int color) { text(poseStack, text, x, y, color, TextRenderType.TEXT); }
	void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType);
	void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType, boolean shadow);
	int width(Object... objs);
	
	public static enum TextRenderType {
		LANG,
		TEXT;
	}
}
