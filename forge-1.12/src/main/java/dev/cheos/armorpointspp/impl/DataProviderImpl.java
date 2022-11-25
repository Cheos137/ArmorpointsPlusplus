package dev.cheos.armorpointspp.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.adapter.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.Loader;

public class DataProviderImpl implements IDataProvider {
	private final Cache<String, Boolean> effectActiveCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();
	private final Minecraft minecraft  = Minecraft.getMinecraft();
	private final boolean attributefix = Loader.isModLoaded("attributefix");
	private final boolean potioncore   = Armorpointspp.POTIONCORE && ApppConfig.instance().bool(BooleanOption.POTIONCORE_COMPAT);
	
	@Override
	public int armor() {
		return this.minecraft.player.getTotalArmorValue();
	}
	
	@Override
	public int toughness() {
		return MathHelper.floor(this.minecraft.player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
	}
	
	@Override
	public int guiTicks() {
		return this.minecraft.ingameGUI.getUpdateCounter();
	}
	
	@Override
	public int invulnTime() {
		return this.minecraft.player.hurtResistantTime;
	}
	
	@Override
	public long millis() {
		return Minecraft.getSystemTime();
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
		return this.minecraft.gameSettings.hideGUI;
	}
	
	@Override
	public boolean isAttributeFixLoaded() {
		return this.attributefix;
	}
	
	@Override
	public boolean isPotionCoreLoaded() {
		return this.potioncore;
	}
	
	@Override
	public boolean isFullyFrozen() {
		return false; // freezing is only available in 1.17+
	}
	
	@Override
	public boolean isHardcore() {
		return this.minecraft.world.getWorldInfo().isHardcoreModeEnabled();
	}
	
	@Override
	public boolean isEffectActive(String id) {
		try {
			return this.effectActiveCache.get(id, () -> {
				ResourceLocation loc = new ResourceLocation(id);
				return ForgeRegistries.POTIONS.containsKey(loc) && this.minecraft.player.isPotionActive(ForgeRegistries.POTIONS.getValue(loc));
			});
		} catch (ExecutionException e) { return false; }
	}
	
	@Override
	public boolean isEffectActive(IMobEffect effect) {
		return this.minecraft.player.isPotionActive((Potion) effect.getEffect());
	}
	
	@Override
	public boolean shouldDrawSurvivalElements() {
		return this.minecraft.playerController.gameIsSurvivalOrAdventure() && this.minecraft.getRenderViewEntity() instanceof EntityPlayer;
	}
	
	@Override
	public IMobEffectInstance getActiveEffect(IMobEffect effect) {
		return new MobEffectInstanceImpl(this.minecraft.player.getActivePotionEffect((Potion) effect.getEffect()));
	}
	
	@Override
	public IPotionCore potionCore() {
		return PotionCoreImpl.INSTANCE;
	}
	
	@Override
	public Iterable<IItemStack> armorSlots() {
		return ItemStackImpl.wrap(this.minecraft.player.getArmorInventoryList());
	}
}
