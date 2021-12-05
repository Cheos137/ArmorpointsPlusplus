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
	boolean isEffectActive(IMobEffect effect);
	boolean shouldDrawSurvivalElements();
	IMobEffectInstance getActiveEffect(IMobEffect effect);
	IPotionCore potionCore();
	Iterable<IItemStack> armorSlots();
	default int resistance() { return isPotionCoreLoaded() ? potionCore().resistance() : getActiveEffect(effects().resistance()).amplifier(); }
	
	public interface IPotionCore {
		int magicShield();
		int resistance();
	}
}
