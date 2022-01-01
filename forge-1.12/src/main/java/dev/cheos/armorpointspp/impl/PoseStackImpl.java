package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import net.minecraft.client.renderer.GlStateManager;

public class PoseStackImpl implements IPoseStack {
	@Override
	public void pushPose() {
		GlStateManager.pushMatrix();
	}
	
	@Override
	public void popPose() {
		GlStateManager.popMatrix();
	}
	
	@Override
	public void scale(float x, float y, float z) {
		GlStateManager.scale(x, y, z);
	}
	
	@Override
	public void translate(double x, double y, double z) {
		GlStateManager.translate(x, y, z);
	}
	
	@Override
	public Object getPoseStack() {
		throw new UnsupportedOperationException("No pose stack available in 1.12"); // neither needed nor supported in 1.12
	}
}
