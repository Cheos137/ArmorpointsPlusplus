package dev.cheos.armorpointspp.compat;

//import de.dafuqs.spectrum.render.HudRenderers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class SpectrumSafeAccess {
	public static void handleOnRender(GuiGraphics graphics, int screenWidth, int screenHeight, Player player) {
//		HudRenderers.renderAzureDike(graphics.pose(), screenWidth, screenHeight, player); // TODO outdated
	}
}
