package dev.cheos.armorpointspp.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import dev.cheos.armorpointspp.core.adapter.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DataProviderImpl implements IDataProvider {
	private final Cache<String, Boolean> effectActiveCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();
	private final Minecraft minecraft  = Minecraft.getInstance();
	
	@Override
	public int armor() {
		return this.minecraft.player.getArmorValue();
	}
	
	@Override
	public int maxArmor() {
		return Attributes.ARMOR instanceof RangedAttribute ra ? Mth.floor(ra.getMaxValue()) : 30;
	}
	
	@Override
	public double toughness() {
		return this.minecraft.player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
	}
	
	@Override
	public double maxToughness() {
		return Attributes.ARMOR_TOUGHNESS instanceof RangedAttribute ra ? ra.getMaxValue() : 20;
	}
	
	@Override
	public int guiTicks() {
		return this.minecraft.gui.getGuiTicks();
	}
	
	@Override
	public int invulnTime() {
		return this.minecraft.player.invulnerableTime;
	}
	
	@Override
	public long millis() {
		return Util.getMillis();
	}
	
	@Override
	public float health() {
		return this.minecraft.player.getHealth();
	}
	
	@Override
	public float maxHealth() {
		return this.minecraft.player.getMaxHealth();
	}
	
	@Override
	public float absorption() {
		return this.minecraft.player.getAbsorptionAmount();
	}
	
	@Override
	public float percentFrozen() {
		return this.minecraft.player.getPercentFrozen();
	}
	
	@Override
	public IEffectProvider effects() {
		return EffectProviderImpl.INSTANCE;
	}
	
	@Override
	public IEnchantmentProvider enchantments() {
		return new EnchantmentProviderImpl(this.minecraft.level); // don't particularly like this solution.. but it's the best i've got for now without checking for level reloads and other whacky stuff
	}
	
	@Override
	public boolean hidden() {
		return this.minecraft.options.hideGui;
	}
	
	@Override
	public boolean isPotionCoreLoaded() {
		return false; // unsupported in 1.18
	}
	
	@Override
	public boolean isFullyFrozen() {
		return this.minecraft.player.isFullyFrozen();
	}
	
	@Override
	public boolean isHardcore() {
		return this.minecraft.level.getLevelData().isHardcore();
	}
	
	@Override
	public boolean isEffectActive(String id) {
		try {
			return this.effectActiveCache.get(id, () -> {
				ResourceLocation loc = ResourceLocation.parse(id);
				return BuiltInRegistries.MOB_EFFECT.get(loc).map(this.minecraft.player::hasEffect).orElse(false);
			});
		} catch (ExecutionException e) { return false; }
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean isEffectActive(IMobEffect effect) {
		return this.minecraft.player.hasEffect((Holder<MobEffect>) effect.getEffect());
	}
	
	@Override
	public boolean shouldDrawSurvivalElements() {
		return this.minecraft.gameMode.canHurtPlayer() && this.minecraft.getCameraEntity() instanceof Player;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public IMobEffectInstance getActiveEffect(IMobEffect effect) {
		return new MobEffectInstanceImpl(this.minecraft.player.getEffect((Holder<MobEffect>) effect.getEffect()));
	}
	
	@Override
	public IPotionCore potionCore() {
		return PotionCoreImpl.INSTANCE;
	}
	
	@Override
	public Iterable<IItemStack> armorSlots() {
		return ItemStackImpl.wrap(
				getItem(EquipmentSlot.HEAD),
				getItem(EquipmentSlot.BODY),
				getItem(EquipmentSlot.LEGS),
				getItem(EquipmentSlot.FEET)
		);
	}
	
	private ItemStack getItem(EquipmentSlot slot) {
		return this.minecraft.player.getItemBySlot(slot);
	}
}
