package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.*;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentHelperImpl implements IEnchantmentHelper {
	public static final IEnchantmentHelper INSTANCE = new EnchantmentHelperImpl();
	private EnchantmentHelperImpl() { }
	
	@Override
	@SuppressWarnings("unchecked")
	public int getLevel(IEnchantment ench, IItemStack stack) {
		return EnchantmentHelper.getTagEnchantmentLevel((Holder<Enchantment>) ench.getEnchantment(), (ItemStack) stack.getStack());
	}
}
