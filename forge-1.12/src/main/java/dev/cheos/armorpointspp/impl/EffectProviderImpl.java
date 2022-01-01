package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IEffectProvider;
import dev.cheos.armorpointspp.core.adapter.IMobEffect;
import net.minecraft.init.MobEffects;

public class EffectProviderImpl implements IEffectProvider {
	public  static final IEffectProvider INSTANCE = new EffectProviderImpl();
	private static final IMobEffect RESISTANCE    = MobEffectImpl.of(MobEffects.RESISTANCE);
	private static final IMobEffect REGENERATION  = MobEffectImpl.of(MobEffects.REGENERATION);
	private static final IMobEffect POISON        = MobEffectImpl.of(MobEffects.POISON);
	private static final IMobEffect WITHER        = MobEffectImpl.of(MobEffects.WITHER);
	
	private EffectProviderImpl() { }
	
	@Override
	public IMobEffect resistance() {
		return RESISTANCE;
	}
	
	@Override
	public IMobEffect regeneration() {
		return REGENERATION;
	}
	
	@Override
	public IMobEffect poison() {
		return POISON;
	}
	
	@Override
	public IMobEffect wither() {
		return WITHER;
	}
}
