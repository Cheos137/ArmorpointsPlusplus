package dev.cheos.armorpointspp.compat;

import dev.cheos.armorpointspp.ApppGui;
import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.dehydration.thirst.ThirstHudRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class DehydrationSafeAccess {
	public static void render(ApppGui gui, GuiGraphics graphics, Player player, int scaledWidth, int scaledHeight, int vehicleHealth) {
		float flashAlpha = 0;
		float otherFlashAlpha = 0;
		try {
			flashAlpha = ReflectionHelper.getPrivateValue(ApppGui.class, "flashAlpha", gui);
			otherFlashAlpha = ReflectionHelper.getPrivateValue(ApppGui.class, "otherFlashAlpha", gui);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		// TODO outdated
		ThirstHudRender.renderThirstHud(graphics.pose(), gui.minecraft, player, scaledWidth, scaledHeight, gui.tickCount, vehicleHealth, flashAlpha, otherFlashAlpha);
		if (player != null && !player.isInvulnerable())
			gui.rightHeight += 10;
	}
}
