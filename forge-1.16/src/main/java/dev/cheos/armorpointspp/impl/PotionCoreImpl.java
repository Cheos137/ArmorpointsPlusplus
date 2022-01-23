package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IDataProvider.IPotionCore;

public class PotionCoreImpl implements IPotionCore { // Unsupported in 1.16
	public static final PotionCoreImpl INSTANCE = new PotionCoreImpl();
	private PotionCoreImpl() { }
	
	@Override public double magicShield() { return 0; }
	@Override public double resistance() { return 0; }
}
