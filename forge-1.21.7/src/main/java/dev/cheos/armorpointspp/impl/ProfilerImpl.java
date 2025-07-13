package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IProfiler;
import net.minecraft.util.profiling.Profiler;

public class ProfilerImpl implements IProfiler {
	@Override
	public void push(String s) {
		Profiler.get().push(s);
	}

	@Override
	public void pop() {
		Profiler.get().pop();
	}

	@Override
	public void popPush(String s) {
		Profiler.get().popPush(s);
	}
}
