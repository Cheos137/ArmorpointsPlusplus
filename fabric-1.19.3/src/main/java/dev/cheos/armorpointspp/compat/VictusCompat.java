package dev.cheos.armorpointspp.compat;

import com.glisco.victus.Victus;
import com.glisco.victus.hearts.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.ApppGui;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.render.Components;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class VictusCompat {
	public static void render(ApppGui gui, PoseStack poseStack, Player player, int baseX) {
		gui.minecraft.getProfiler().push("victus");
		HeartAspectComponent aspectComponent = Victus.ASPECTS.get(player);
		
		int max = ApppConfig.instance().bool(BooleanOption.HEALTH_BG_ALWAYS_SHOW_10)
				? 9
				: (int) Math.min(10, (player.getMaxHealth() + 1) * 0.5F) - 1;
		int[] heartYs  = Components.HEALTH.lastHeartY();
		int health     = Mth.ceil(player.getHealth());
		int heartStack = Math.min((health - 1) / 20, 10);
		
		for (int i = max; i >= 0; i--) {
			int heartX = baseX + i * 8;
			int heartY = heartYs[i];
			int heartValue = i * 2 + heartStack * 20 + 1;
			boolean isEmpty = heartValue > health && heartStack == 0;
			// reduce index by one heart row if no higher value heart is rendered at the current position
			int idx = heartStack * 10 + i - (heartValue >= health && heartStack > 0 ? 10 : 0);
			HeartAspect aspect = aspectComponent.getAspect(idx);
			if (aspect == null) continue;
			
			// OVERLAY
			gui.setup(true, false, aspect.getAtlas());
			renderAspect(poseStack, heartX, heartY, aspect.getTextureIndex(), aspect.getRechargeProgress());
			
			if (aspect instanceof OverlaySpriteProvider osp && osp.shouldRenderOverlay()) {
				int tint = osp.getOverlayTint();
				RenderSystem.setShaderColor(((tint >> 16) & 0xFF) / 255F, ((tint >> 8) & 0xFF) / 255F, (tint & 0xFF) / 255F, 1);
				renderAspect(poseStack, heartX, heartY, osp.getOverlayIndex(), aspect.getRechargeProgress());
				RenderSystem.setShaderColor(1, 1, 1, 1);
			}
			
			// RECHARGING OUTLINE
			if (!isEmpty && aspectComponent.recharging()) {
				gui.setup(true, false, HeartAspect.HEART_ATLAS_TEXTURE);
				GuiComponent.blit(poseStack, heartX, heartY, 55, 55, 9, 9, 64, 64);
			}
			gui.setup(true, false, GuiComponent.GUI_ICONS_LOCATION);
		}
		
		gui.minecraft.getProfiler().pop();
	}
	
	private static void renderAspect(PoseStack poseStack, int x, int y, int texIdx, float progress) {
		GuiComponent.blit(poseStack, x + 1, y + 1, (texIdx % 8) * 8, (texIdx / 8) * 8, Math.round(progress * 7), 7, 64, 64);
	}
}
