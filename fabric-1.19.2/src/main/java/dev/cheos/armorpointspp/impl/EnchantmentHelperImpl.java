package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.IEnchantment;
import dev.cheos.armorpointspp.core.adapter.IEnchantmentHelper;
import dev.cheos.armorpointspp.core.adapter.IItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantmentHelperImpl implements IEnchantmentHelper {
	public static final IEnchantmentHelper INSTANCE = new EnchantmentHelperImpl();
	private EnchantmentHelperImpl() { }
	
	@Override
	public int getLevel(IEnchantment ench, IItemStack stack) {
		return EnchantmentHelper.getItemEnchantmentLevel((Enchantment) ench.getEnchantment(), (ItemStack) stack.getStack());
	}
}
