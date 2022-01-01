package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.core.adapter.IMobEffect;
import net.minecraft.potion.Potion;

public class MobEffectImpl implements IMobEffect {
	private static final Map<Potion, MobEffectImpl> instances = new HashMap<>();
	private final Potion effect;
	
	private MobEffectImpl(Potion effect) {
		this.effect = effect;
	}
	
	@Override
	public Object getEffect() {
		return this.effect;
	}
	
	public static MobEffectImpl of(Potion effect) {
		return instances.computeIfAbsent(effect, MobEffectImpl::new);
	}
}
