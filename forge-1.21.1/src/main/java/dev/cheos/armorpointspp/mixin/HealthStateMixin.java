package dev.cheos.armorpointspp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.PlayerTabOverlay.HealthState;

@Mixin(HealthState.class)
public interface HealthStateMixin {
	@Accessor int getLastValue();
	@Accessor long getLastUpdateTick();
	@Accessor long getBlinkUntilTick();
}
