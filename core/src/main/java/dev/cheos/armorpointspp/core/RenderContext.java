package dev.cheos.armorpointspp.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;

public class RenderContext {
	public final IConfig config;
	public final IDataProvider data;
	public final IEnchantmentHelper ench;
	public final IMath math;
	public final IRenderer renderer;
	public final IPoseStack poseStack;
	public final IProfiler profiler;
	public final int x, y;
	private Map<String, Object> properties; // lazyinit
	
	public RenderContext(IConfig config, IDataProvider data, IEnchantmentHelper ench, IMath math, IRenderer renderer, IPoseStack poseStack, IProfiler profiler, int x, int y) {
		this.config = config;
		this.data   = data;
		this.ench   = ench;
		this.math   = math;
		this.renderer  = renderer;
		this.poseStack = poseStack;
		this.profiler  = profiler;
		this.x = x;
		this.y = y;
	}
	
	private void putProperty0(String id, Object obj) {
		if (this.properties == null) this.properties = new HashMap<>();
		this.properties.put(id, obj);
	}
	
	public <T> void putProperty(String id, T t) { putProperty0(id, t); }
	public <T> void putProperty(String id, Consumer<T> consumer) { putProperty0(id, consumer); } // add more of these as needed
	
	@SuppressWarnings("unchecked")
	public <T> T property(String id) {
		return this.properties == null ? null : (T) this.properties.get(id);
	}
	
	public boolean shouldRender() {
		return !this.data.hidden() && this.data.shouldDrawSurvivalElements();
	}
	
	public boolean shouldRenderArmor() {
		return shouldRender() && (this.data.armor() > 0 || this.config.bool(BooleanOption.ARMOR_SHOW_ON_0));
	}
	
	public boolean shouldRenderToughness() {
		return shouldRender() && (this.data.toughness() > 0 || this.config.bool(BooleanOption.TOUGHNESS_SHOW_ON_0));
	}
}
