package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.compat.PotionCoreCompat;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IDataProvider.IPotionCore;

public class PotionCoreImpl implements IPotionCore {
	public static final PotionCoreImpl INSTANCE = new PotionCoreImpl();
	private PotionCoreImpl() { }
	
	@Override public double magicShield() { return Armorpointspp.POTIONCORE && ApppConfig.instance().bool(BooleanOption.POTIONCORE_COMPAT) ? PotionCoreCompat.magicShield() : 0; }
	@Override public double resistance() { return Armorpointspp.POTIONCORE && ApppConfig.instance().bool(BooleanOption.POTIONCORE_COMPAT) ? PotionCoreCompat.resistance() : 0; }
}
