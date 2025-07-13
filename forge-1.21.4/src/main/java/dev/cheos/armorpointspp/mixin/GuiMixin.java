package dev.cheos.armorpointspp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public interface GuiMixin {
	@Invoker(remap = false) void invokeRenderHealthLevel(GuiGraphics graphics);
}
