package dev.cheos.armorpointspp.core.adapter;

import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public interface IRenderer {
	void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight);
	void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height);
	void blitM(IPoseStack pStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight);
	void blitM(IPoseStack pStack, int x, int y, float u, float v, int width, int height);
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
