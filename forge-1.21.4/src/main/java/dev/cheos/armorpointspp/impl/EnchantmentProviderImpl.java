package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IEnchantment;
import dev.cheos.armorpointspp.core.adapter.IEnchantmentProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class EnchantmentProviderImpl implements IEnchantmentProvider {
	private final IEnchantment protection, blastProtection, fireProtection, projProtection;
	
	public EnchantmentProviderImpl(Level level) {
		HolderLookup.RegistryLookup<Enchantment> lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		this.protection      = EnchantmentImpl.of(lookup.getOrThrow(Enchantments.PROTECTION));
		this.blastProtection = EnchantmentImpl.of(lookup.getOrThrow(Enchantments.BLAST_PROTECTION));
		this.fireProtection  = EnchantmentImpl.of(lookup.getOrThrow(Enchantments.FIRE_PROTECTION));
		this.projProtection  = EnchantmentImpl.of(lookup.getOrThrow(Enchantments.PROJECTILE_PROTECTION));
	}
	
	@Override
	public IEnchantment protection() {
		return this.protection;
	}
	
	@Override
	public IEnchantment blastProtection() {
		return this.blastProtection;
	}
	
	@Override
	public IEnchantment fireProtection() {
		return this.fireProtection;
	}
	
	@Override
	public IEnchantment projectileProtection() {
		return this.projProtection;
	}
}
