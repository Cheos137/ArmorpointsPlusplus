package dev.cheos.armorpointspp.impl;

import java.util.ArrayList;
import java.util.List;

import dev.cheos.armorpointspp.core.adapter.IItemStack;
import net.minecraft.world.item.ItemStack;

public class ItemStackImpl implements IItemStack {
	private final ItemStack stack;
	
	public ItemStackImpl(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public Object getStack() {
		return this.stack;
	}
	
	public static Iterable<IItemStack> wrap(ItemStack... stacks) {
		List<IItemStack> out = new ArrayList<>();
		for (ItemStack stack : stacks)
			out.add(new ItemStackImpl(stack));
		return out;
	}
	
	public static Iterable<IItemStack> wrap(Iterable<ItemStack> stacks) {
		List<IItemStack> out = new ArrayList<>();
		for (ItemStack stack : stacks)
			out.add(new ItemStackImpl(stack));
		return out;
	}
}
