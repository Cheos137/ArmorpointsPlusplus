package dev.cheos.armorpointspp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@Mixin(ForgeIngameGui.class)
public interface ForgeIngameGuiMixin {
	@Accessor("PLAYER_HEALTH_ELEMENT")
	@Mutable
	void setPlayerHealthComponent(IIngameOverlay component);
	
	@Accessor("ARMOR_LEVEL_ELEMENT")
	@Mutable
	void setArmorLevelComponent(IIngameOverlay component);
}
