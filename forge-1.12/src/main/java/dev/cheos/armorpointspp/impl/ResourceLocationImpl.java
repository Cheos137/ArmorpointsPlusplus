package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IResourceLocation;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationImpl implements IResourceLocation {
	private final ResourceLocation resource;
	
	public ResourceLocationImpl(ResourceLocation resource) {
		this.resource = resource;
	}
	
	@Override
	public Object getResourceLocation() {
		return this.resource;
	}
}
