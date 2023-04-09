package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.ApppGui;
import net.its0v3r.itsthirst.gui.ThirstHud;
import net.minecraft.world.entity.player.Player;

public class ItsthirstSafeAccess {
	public static void render(ApppGui gui, PoseStack poseStack, Player player) {
		ThirstHud.renderThirstHud(poseStack, gui.minecraft, player, gui.tickCount);
	}
}
