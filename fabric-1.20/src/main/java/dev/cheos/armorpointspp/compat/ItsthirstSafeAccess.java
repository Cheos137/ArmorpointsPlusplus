package dev.cheos.armorpointspp.compat;

import dev.cheos.armorpointspp.ApppGui;
//import net.its0v3r.itsthirst.gui.ThirstHud;
//import net.its0v3r.itsthirst.registry.ConfigRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class ItsthirstSafeAccess {
	public static void render(ApppGui gui, GuiGraphics graphics, Player player) {
//		ConfigRegistry.CONFIG.hud_y = 49 - gui.rightHeight; // hack to get it's thirst to render at the correct y
		// TODO outdated
//		ThirstHud.renderThirstHud(graphics.pose(), gui.minecraft, player, gui.tickCount);
//		if (player != null && !player.isCreative() && !player.isSpectator()) // check copied over from it's thirst
//			gui.rightHeight += 10;
	}
}
