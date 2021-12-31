package dev.cheos.armorpointspp.compat;

import dev.cheos.armorpointspp.core.adapter.IDataProvider.IPotionCore;

public class PotionCore implements IPotionCore { // Unsupported in 1.17
	public static final PotionCore INSTANCE = new PotionCore();
	private PotionCore() { }
	
	@Override public int magicShield() { return 0; }
	@Override public int resistance() { return 0; }
}
