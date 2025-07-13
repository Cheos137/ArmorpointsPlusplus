package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.core.adapter.IEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentImpl implements IEnchantment {
	private static final Map<Holder<Enchantment>, EnchantmentImpl> instances = new HashMap<>();
	private final Holder<Enchantment> enchantment;
	
	private EnchantmentImpl(Holder<Enchantment> enchantment) {
		this.enchantment = enchantment;
	}
	
	public static EnchantmentImpl of(Holder<Enchantment> effect) {
		return instances.computeIfAbsent(effect, EnchantmentImpl::new);
	}
	
	@Override
	public Object getEnchantment() {
		return this.enchantment;
	}
	
}
