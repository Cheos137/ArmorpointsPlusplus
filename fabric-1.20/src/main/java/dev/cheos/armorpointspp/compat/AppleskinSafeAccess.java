package dev.cheos.armorpointspp.compat;

import net.minecraft.client.gui.GuiGraphics;
import squeek.appleskin.client.HUDOverlayHandler;

public class AppleskinSafeAccess {
	public static void handlerOnPreRender(GuiGraphics graphics) {
		if (HUDOverlayHandler.INSTANCE != null)
			HUDOverlayHandler.INSTANCE.onPreRender(graphics.pose()); // TODO outdated
	}
	
	public static void handlerOnRender(GuiGraphics graphics) {
		if (HUDOverlayHandler.INSTANCE != null)
			HUDOverlayHandler.INSTANCE.onRender(graphics.pose()); // TODO outdated
	}
}
