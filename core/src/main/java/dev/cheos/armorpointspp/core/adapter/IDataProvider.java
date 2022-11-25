package dev.cheos.armorpointspp.core.adapter;

public interface IDataProvider {
	int armor();
	int toughness();
	int guiTicks();
	int invulnTime();
	long millis();
	float health();
	float maxHealth();
	float absorption();
	float percentFrozen();
	IEffectProvider effects();
	IEnchantmentProvider enchantments();
	boolean hidden();
	boolean isAttributeFixLoaded();
	boolean isPotionCoreLoaded();
	boolean isFullyFrozen();
	boolean isHardcore();
	boolean isEffectActive(String id);
	boolean isEffectActive(IMobEffect effect);
	boolean shouldDrawSurvivalElements();
	IMobEffectInstance getActiveEffect(IMobEffect effect);
	IPotionCore potionCore();
	Iterable<IItemStack> armorSlots();
	
	// these should return 0 by definition if potioncore is not supported
	public interface IPotionCore {
		double magicShield();
		double resistance();
	}
}
