package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class DataProviderImpl implements IDataProvider {
	private final Minecraft minecraft  = Minecraft.getInstance();
	
	@Override
	public int armor() {
		return this.minecraft.player.getArmorValue();
	}
	
	@Override
	public int maxArmor() {
		return Attributes.ARMOR instanceof RangedAttribute ? MathHelper.floor(((RangedAttribute) Attributes.ARMOR).maxValue) : 30;
	}
	
	@Override
	public int toughness() {
		return MathHelper.floor(this.minecraft.player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
	}
	
	@Override
	public int maxToughness() {
		return Attributes.ARMOR_TOUGHNESS instanceof RangedAttribute ? MathHelper.floor(((RangedAttribute) Attributes.ARMOR_TOUGHNESS).maxValue) : 20;
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
		return 0; // freezing is only available in 1.17+
	}
	
	@Override
	public IEffectProvider effects() {
		return EffectProviderImpl.INSTANCE;
	}
	
	@Override
	public IEnchantmentProvider enchantments() {
		return EnchantmentProviderImpl.INSTANCE;
	}
	
	@Override
	public boolean hidden() {
		return this.minecraft.options.hideGui;
	}
	
	@Override
	public boolean isPotionCoreLoaded() {
		return false; // unsupported in 1.16
	}
	
	@Override
	public boolean isFullyFrozen() {
		return false; // freezing is only available in 1.17+
	}
	
	@Override
	public boolean isHardcore() {
		return this.minecraft.level.getLevelData().isHardcore();
	}
	
	@Override
	public boolean isEffectActive(IMobEffect effect) {
		return this.minecraft.player.hasEffect((Effect) effect.getEffect());
	}
	
	@Override
	public boolean shouldDrawSurvivalElements() {
		return this.minecraft.gameMode.canHurtPlayer() && this.minecraft.getCameraEntity() instanceof PlayerEntity;
	}
	
	@Override
	public IMobEffectInstance getActiveEffect(IMobEffect effect) {
		return new MobEffectInstanceImpl(this.minecraft.player.getEffect((Effect) effect.getEffect()));
	}
	
	@Override
	public IPotionCore potionCore() {
		return PotionCoreImpl.INSTANCE;
	}
	
	@Override
	public Iterable<IItemStack> armorSlots() {
		return ItemStackImpl.wrap(this.minecraft.player.getArmorSlots());
	}
}
