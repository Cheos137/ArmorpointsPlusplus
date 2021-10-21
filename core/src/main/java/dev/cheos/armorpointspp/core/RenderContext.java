package dev.cheos.armorpointspp.core;

import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;

public class RenderContext {
	public final IConfig config;
	public final IDataProvider data;
	public final IEnchantmentHelper ench;
	public final IMath math;
	public final IRenderer renderer;
	public final IPoseStack poseStack;
	public final IUtil util;
	public final int x, y;
	
	public RenderContext(IConfig config, IDataProvider data, IEnchantmentHelper ench, IMath math, IRenderer renderer, IPoseStack poseStack, IUtil util, int x, int y) {
		this.config = config;
		this.data   = data;
		this.ench   = ench;
		this.math   = math;
		this.renderer  = renderer;
		this.poseStack = poseStack;
		this.util = util;
		this.x = x;
		this.y = y;
	}
	
	public boolean shouldRender() {
		return !this.data.hidden();
	}
	
	public boolean shouldRenderArmor() {
		return shouldRender() && (this.data.armor() > 0 || this.config.bool(BooleanOption.ARMOR_SHOW_ON_0));
	}
}
