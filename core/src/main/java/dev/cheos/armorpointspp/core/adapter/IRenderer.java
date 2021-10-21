package dev.cheos.armorpointspp.core.adapter;

public interface IRenderer {
	void bind(IResourceLocation texture);
	void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height);
	void setColor(float r, float g, float b, float a);
	void setupAppp();
	void setupVanilla();
	void text(IPoseStack poseStack, String text, float x, float y, int color);
	void text(IPoseStack poseStack, String text, float x, float y, int color, boolean shadow);
	int width(Object... objs);
}
