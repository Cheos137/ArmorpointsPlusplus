package dev.cheos.armorpointspp.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

@Mixin(RegisterGuiOverlaysEvent.class)
public interface IRegisterGuiOverlaysEventMixin {
	@Accessor Map<ResourceLocation, IGuiOverlay> getOverlays();
	@Accessor List<ResourceLocation> getOrderedOverlays();
}
