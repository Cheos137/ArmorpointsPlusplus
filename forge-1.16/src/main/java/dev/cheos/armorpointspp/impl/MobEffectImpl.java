package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.core.adapter.IMobEffect;
import net.minecraft.potion.Effect;

public class MobEffectImpl implements IMobEffect {
	private static final Map<Effect, MobEffectImpl> instances = new HashMap<>();
	private final Effect effect;
	
	private MobEffectImpl(Effect effect) {
		this.effect = effect;
	}
	
	@Override
	public Object getEffect() {
		return this.effect;
	}
	
	public static MobEffectImpl of(Effect effect) {
		return instances.computeIfAbsent(effect, MobEffectImpl::new);
	}
}
