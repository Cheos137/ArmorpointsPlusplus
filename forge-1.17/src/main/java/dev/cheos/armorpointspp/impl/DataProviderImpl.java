package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.core.adapter.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class DataProviderImpl implements IDataProvider {
	private final Minecraft minecraft  = Minecraft.getInstance();
	private final boolean attributefix = ModList.get().isLoaded("attributefix");
	
	@Override
	public int armor() {
		return this.minecraft.player.getArmorValue();
	}
	
	@Override
	public int toughness() {
		return Mth.floor(this.minecraft.player.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
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
		return EnchantmentProviderImpl.INSTANCE;
	}
	
	@Override
	public boolean hidden() {
		return this.minecraft.options.hideGui;
	}
	
	@Override
	public boolean isAttributeFixLoaded() {
		return this.attributefix;
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
	public boolean isEffectActive(IMobEffect effect) {
		return this.minecraft.player.hasEffect((MobEffect) effect.getEffect());
	}
	
	@Override
	public boolean shouldDrawSurvivalElements() {
		return this.minecraft.gameMode.canHurtPlayer() && this.minecraft.getCameraEntity() instanceof Player;
	}
	
	@Override
	public IMobEffectInstance getActiveEffect(IMobEffect effect) {
		return new MobEffectInstanceImpl(this.minecraft.player.getEffect((MobEffect) effect.getEffect()));
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
