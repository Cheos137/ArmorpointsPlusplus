package dev.cheos.armorpointspp.mixin;

import java.util.List;
import java.util.Map;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resources.ResourceLocation;

@Mixin(RegisterGuiOverlaysEvent.class)
public interface IRegisterGuiOverlaysEventMixin {
	@Accessor Map<ResourceLocation, IGuiOverlay> getOverlays();
	@Accessor List<ResourceLocation> getOrderedOverlays();
}
