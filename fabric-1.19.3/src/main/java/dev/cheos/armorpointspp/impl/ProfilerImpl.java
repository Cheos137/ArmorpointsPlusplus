package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IProfiler;
import net.minecraft.client.Minecraft;

public class ProfilerImpl implements IProfiler {
	private final Minecraft minecraft = Minecraft.getInstance();
	
	@Override
	public void push(String s) {
		this.minecraft.getProfiler().push(s);
	}

	@Override
	public void pop() {
		this.minecraft.getProfiler().pop();
	}

	@Override
	public void popPush(String s) {
		this.minecraft.getProfiler().popPush(s);
	}
}
