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
	boolean isFullyFrozen();
	boolean isHardcore();
	boolean isEffectActive(IMobEffect effect);
	boolean shouldDrawSurvivalElements();
	IMobEffectInstance getActiveEffect(IMobEffect effect);
	Iterable<IItemStack> armorSlots();
}
