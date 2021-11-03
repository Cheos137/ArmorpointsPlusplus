package dev.cheos.armorpointspp.core.adapter;

public interface IPoseStack {
	void pushPose();
	void popPose();
	void scale(float x, float y, float z);
	void translate(double x, double y, double z);
	
	Object getPoseStack();
}
