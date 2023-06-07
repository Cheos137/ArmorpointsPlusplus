package dev.cheos.armorpointspp.compat;

import com.redlimerl.detailab.render.ArmorBarRenderer;

import dev.cheos.armorpointspp.ApppGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class DetailarmorbarSafeAccess {
	public static void render(ApppGui gui, GuiGraphics graphics, Player player) {
		gui.minecraft.getProfiler().push("__dummy");
		ArmorBarRenderer.INSTANCE.render(graphics.pose(), player); // TODO outdated
		gui.minecraft.getProfiler().pop();
	}
}
