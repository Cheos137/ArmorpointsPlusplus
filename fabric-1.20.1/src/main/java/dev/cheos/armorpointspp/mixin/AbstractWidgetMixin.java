package dev.cheos.armorpointspp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetMixin {
	@Accessor Tooltip getTooltip();
}
