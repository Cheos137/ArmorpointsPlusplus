package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.core.adapter.IEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentImpl implements IEnchantment {
	private static final Map<Enchantment, EnchantmentImpl> instances = new HashMap<>();
	private final Enchantment enchantment;
	
	private EnchantmentImpl(Enchantment enchantment) {
		this.enchantment = enchantment;
	}
	
	public static EnchantmentImpl of(Enchantment effect) {
		return instances.computeIfAbsent(effect, EnchantmentImpl::new);
	}
	
	@Override
	public Object getEnchantment() {
		return this.enchantment;
	}
	
}
