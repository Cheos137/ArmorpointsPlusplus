package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.core.adapter.IMobEffect;
import net.minecraft.world.effect.MobEffect;

public class MobEffectImpl implements IMobEffect {
	private static final Map<MobEffect, MobEffectImpl> instances = new HashMap<>();
	private final MobEffect effect;
	
	private MobEffectImpl(MobEffect effect) {
		this.effect = effect;
	}
	
	@Override
	public Object getEffect() {
		return this.effect;
	}
	
	public static MobEffectImpl of(MobEffect effect) {
		return instances.computeIfAbsent(effect, MobEffectImpl::new);
	}
}
