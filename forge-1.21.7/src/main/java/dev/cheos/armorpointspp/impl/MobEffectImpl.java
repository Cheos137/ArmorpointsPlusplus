package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.core.adapter.IMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class MobEffectImpl implements IMobEffect {
	private static final Map<Holder<MobEffect>, MobEffectImpl> instances = new HashMap<>();
	private final Holder<MobEffect> effect;
	
	private MobEffectImpl(Holder<MobEffect> effect) {
		this.effect = effect;
	}
	
	@Override
	public Object getEffect() {
		return this.effect;
	}
	
	public static MobEffectImpl of(Holder<MobEffect> effect) {
		return instances.computeIfAbsent(effect, MobEffectImpl::new);
	}
}
