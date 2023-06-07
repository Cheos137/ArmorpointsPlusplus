package dev.cheos.armorpointspp.compat;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.cheos.armorpointspp.ApppGui;
import moriyashiine.bewitchment.api.BewitchmentAPI;
import moriyashiine.bewitchment.api.component.BloodComponent;
import moriyashiine.bewitchment.api.component.MagicComponent;
import moriyashiine.bewitchment.common.Bewitchment;
import moriyashiine.bewitchment.common.registry.BWComponents;
import moriyashiine.bewitchment.common.registry.BWTags;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BewitchmentCompat {
	private static final ResourceLocation BEWITCHMENT_GUI_ICONS_TEXTURE = new ResourceLocation(Bewitchment.MODID, "textures/gui/icons.png");
	
	public static boolean render(ApppGui gui, GuiGraphics graphics, Player player, int scaledWidth, int scaledHeight) {
		BWComponents.MAGIC_COMPONENT.maybeGet(player).ifPresent(magicComponent -> {
			if (magicComponent.getMagicTimer() > 0) {
				RenderSystem.setShaderColor(1, 1, 1, magicComponent.getMagicTimer() / 10f);
				graphics.blit(BEWITCHMENT_GUI_ICONS_TEXTURE, 13, (scaledHeight - 74) / 2, 25, 0, 7, 74);
				graphics.blit(BEWITCHMENT_GUI_ICONS_TEXTURE, 13, (scaledHeight - 74) / 2, 32, 0, 7, (int) (74 - (magicComponent.getMagic() * 74f / MagicComponent.MAX_MAGIC)));
				graphics.blit(BEWITCHMENT_GUI_ICONS_TEXTURE, 4, (scaledHeight - 102) / 2, 0, 0, 25, 102);
				RenderSystem.setShaderColor(1, 1, 1, 1);
			}
		});
		if (BewitchmentAPI.isVampire(player, true)) {
			drawBlood(gui, graphics, player, scaledWidth / 2 + 82, scaledHeight - 39, 10);
			if (player.isShiftKeyDown() && gui.minecraft.crosshairPickEntity instanceof LivingEntity livingEntity && gui.minecraft.crosshairPickEntity.getType().is(BWTags.HAS_BLOOD))
				drawBlood(gui, graphics, livingEntity, scaledWidth / 2 + 13, scaledHeight / 2 + 9, 5);
			return true;
		} return false;
	}
	
	private static void drawBlood(ApppGui gui, GuiGraphics graphics, LivingEntity entity, int x, int y, int droplets) {
		BWComponents.BLOOD_COMPONENT.maybeGet(entity).ifPresent(bloodComponent -> {
			int v = entity.hasEffect(MobEffects.HUNGER) ? 9 : 0;
			float blood = ((float) bloodComponent.getBlood() / BloodComponent.MAX_BLOOD * droplets);
			int full = (int) blood;
			for (int i = 0; i < full; i++)
				graphics.blit(BEWITCHMENT_GUI_ICONS_TEXTURE, x - i * 8, y, 39, v, 9, 9);
			if (full < droplets) {
				float remaining = blood - full;
				graphics.blit(BEWITCHMENT_GUI_ICONS_TEXTURE, x - full * 8, y, remaining > 5 / 6f ? 48 : remaining > 4 / 6f ? 57 : remaining > 3 / 6f ? 66 : remaining > 2 / 6f ? 75 : remaining > 1 / 6f ? 84 : remaining > 0 ? 93 : 102, v, 9, 9);
			}
			for (int i = (full + 1); i < droplets; i++)
				graphics.blit(BEWITCHMENT_GUI_ICONS_TEXTURE, x - i * 8, y, 102, v, 9, 9);
		});
	}
}
