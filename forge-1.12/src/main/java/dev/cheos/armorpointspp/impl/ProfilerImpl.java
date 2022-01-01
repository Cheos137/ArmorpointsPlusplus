package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IProfiler;
import net.minecraft.client.Minecraft;

public class ProfilerImpl implements IProfiler {
	private final Minecraft minecraft = Minecraft.getMinecraft();
	
	@Override
	public void push(String s) {
		this.minecraft.mcProfiler.startSection(s);
	}

	@Override
	public void pop() {
		this.minecraft.mcProfiler.endSection();
	}

	@Override
	public void popPush(String s) {
		this.minecraft.mcProfiler.endStartSection(s);
	}
}
