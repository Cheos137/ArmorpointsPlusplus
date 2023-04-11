package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redlimerl.detailab.render.ArmorBarRenderer;

import dev.cheos.armorpointspp.ApppGui;
import net.minecraft.world.entity.player.Player;

public class DetailarmorbarSafeAccess {
	public static void render(ApppGui gui, PoseStack poseStack, Player player) {
		gui.minecraft.getProfiler().push("__dummy");
		ArmorBarRenderer.INSTANCE.render(poseStack, player);
		gui.minecraft.getProfiler().pop();
	}
}
