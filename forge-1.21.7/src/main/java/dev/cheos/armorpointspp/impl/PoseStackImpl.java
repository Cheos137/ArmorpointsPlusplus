package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import net.minecraft.client.gui.GuiGraphics;

public class PoseStackImpl implements IPoseStack {
	private final GuiGraphics graphics;
	
	public PoseStackImpl(GuiGraphics graphics) {
		this.graphics = graphics;
	}
	
	@Override
	public void pushPose() {
		this.graphics.pose().pushMatrix();
	}
	
	@Override
	public void popPose() {
		this.graphics.pose().popMatrix();
	}
	
	@Override
	public void scale(float x, float y, float z) {
		this.graphics.pose().scale(x, y);
	}
	
	@Override
	public void translate(double x, double y, double z) {
		this.graphics.pose().translate((float) x, (float) y);
	}
	
	@Override
	public Object getPoseStack() {
		return this.graphics.pose();
	}
	
	public GuiGraphics getGraphics() {
		return this.graphics;
	}
}
