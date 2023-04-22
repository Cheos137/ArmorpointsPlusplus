package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.ApppGui;
import net.dehydration.access.ThirstManagerAccess;
import net.dehydration.init.ConfigInit;
import net.dehydration.init.EffectInit;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DehydrationCompat { // ref: https://github.com/Globox1997/Dehydration/blob/1.18/src/main/java/net/dehydration/mixin/client/InGameHudMixin.java
	private static final ResourceLocation THIRST_ICON = new ResourceLocation("dehydration:textures/gui/thirst.png");
	
	public static boolean render(ApppGui gui, PoseStack poseStack, Player player, int scaledWidth, int scaledHeight) {
		if (player != null && !player.isInvulnerable()) {
			LivingEntity livingEntity = gui.getPlayerVehicleWithHealth();
			
			if (gui.getVehicleMaxHearts(livingEntity) == 0) {
				ThirstManager thirstManager = ((ThirstManagerAccess) player).getThirstManager(player);
				
				if (thirstManager.hasThirst()) {
					int thirst = thirstManager.getThirstLevel();
					int baseY = scaledHeight - gui.rightHeight;
					int baseX = scaledWidth / 2 + 91;
					
					for (int i = 0; i < 10; ++i) {
						int y = baseY;
						if (thirstManager.dehydration >= 4.0F && gui.tickCount % (thirst * 3 + 1) == 0) {
							y = baseY + (gui.minecraft.level.random.nextInt(3) - 1); // bouncy
							thirstManager.dehydration -= 4.0F;
						} else if (gui.tickCount % (thirst * 8 + 3) == 0)
							y = baseY + (gui.minecraft.level.random.nextInt(3) - 1); // bouncy
						
						int uppderCoord = 9;
						if (ConfigInit.CONFIG.other_droplet_texture)
							uppderCoord = uppderCoord + 9;
						int beneathCoord = 0;
						if (player.hasEffect(EffectInit.THIRST))
							beneathCoord = 18;
						
						int x = baseX - i * 8 - 9 + ConfigInit.CONFIG.hud_x;
						y += ConfigInit.CONFIG.hud_y;
						
						gui.setup(true, false, THIRST_ICON);
						gui.blit(poseStack, x, y, 0, 0, 9, 9);
						if (i * 2 + 1 < thirst)
							gui.blit(poseStack, x, y, beneathCoord, uppderCoord, 9, 9); // Big icon
						if (i * 2 + 1 == thirst)
							gui.blit(poseStack, x, y, beneathCoord + 9, uppderCoord, 9, 9); // Small icon
					}
					gui.rightHeight += 10;
					gui.setup(true, false, GuiComponent.GUI_ICONS_LOCATION);
					return true;
				}
			}
		}
		return false;
	}
}
