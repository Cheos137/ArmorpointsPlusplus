package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redlimerl.detailab.render.ArmorBarRenderer;

import net.minecraft.world.entity.player.Player;

public class DetailarmorbarSafeAccess {
	public static void render(PoseStack poseStack, Player player) {
		ArmorBarRenderer.INSTANCE.render(poseStack, player);
	}
}
