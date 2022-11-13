package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.vertex.PoseStack;

import de.dafuqs.spectrum.render.HudRenderers;
import net.minecraft.world.entity.player.Player;

public class SpectrumSafeAccess {
	public static void handleOnRender(PoseStack poseStack, int scaledWidth, int scaledHeight, Player player) {
		HudRenderers.renderAzureDike(poseStack, scaledWidth, scaledHeight, player);
	}
}
