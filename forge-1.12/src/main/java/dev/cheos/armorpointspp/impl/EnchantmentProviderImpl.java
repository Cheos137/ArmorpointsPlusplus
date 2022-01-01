package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IEnchantment;
import dev.cheos.armorpointspp.core.adapter.IEnchantmentProvider;
import net.minecraft.init.Enchantments;

public class EnchantmentProviderImpl implements IEnchantmentProvider {
	public  static final IEnchantmentProvider INSTANCE = new EnchantmentProviderImpl();
	private static final IEnchantment PROTECTION       = EnchantmentImpl.of(Enchantments.PROTECTION);
	private static final IEnchantment BLAST_PROTECTION = EnchantmentImpl.of(Enchantments.BLAST_PROTECTION);
	private static final IEnchantment FIRE_PROTECTION  = EnchantmentImpl.of(Enchantments.FIRE_PROTECTION);
	private static final IEnchantment PROJ_PROTECTION  = EnchantmentImpl.of(Enchantments.PROJECTILE_PROTECTION);
	
	private EnchantmentProviderImpl() { }
	
	@Override
	public IEnchantment protection() {
		return PROTECTION;
	}
	
	@Override
	public IEnchantment blastProtection() {
		return BLAST_PROTECTION;
	}
	
	@Override
	public IEnchantment fireProtection() {
		return FIRE_PROTECTION;
	}
	
	@Override
	public IEnchantment projectileProtection() {
		return PROJ_PROTECTION;
	}
}
