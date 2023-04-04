package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.ApppGui;
import dev.cheos.armorpointspp.core.ReflectionHelper;
import net.dehydration.thirst.ThirstHudRender;
import net.minecraft.world.entity.player.Player;

public class DehydrationSafeAccess {
	public static void render(ApppGui gui, PoseStack poseStack, Player player, int scaledHeight, int scaledWidth, int vehicleHealth) {
		float flashAlpha = 0;
		float otherFlashAlpha = 0;
		try {
			flashAlpha = ReflectionHelper.getPrivateValue(ApppGui.class, "flashAlpha", gui);
			otherFlashAlpha = ReflectionHelper.getPrivateValue(ApppGui.class, "otherFlashAlpha", gui);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		ThirstHudRender.renderThirstHud(poseStack, gui.minecraft, player, scaledWidth, scaledHeight, gui.tickCount, vehicleHealth, flashAlpha, otherFlashAlpha);
		gui.rightHeight += 10;
	}
}
