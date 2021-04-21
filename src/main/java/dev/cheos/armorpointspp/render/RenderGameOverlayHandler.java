package dev.cheos.armorpointspp.render;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.cheos.armorpointspp.config.ApppConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("deprecation")
public class RenderGameOverlayHandler {
	private static boolean debug = false; // TODO
	private final HUDRenderer hudRenderer;
	private int lastArmorHeight = -1, lastHealthHeight = -1;
	
	public RenderGameOverlayHandler(Minecraft minecraft) {
		this.hudRenderer = new HUDRenderer(minecraft);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = false)
	public void renderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
		switch (event.getType()) {
			case ARMOR:
				this.lastArmorHeight = baseY(event);
				
				if (conf("debug") || debug)
					hudRenderer.debugTexture(event.getMatrixStack());
				if (conf("enableArmorBar")) {
					hudRenderer.renderArmor(event.getMatrixStack(), baseX(event), lastArmorHeight);
					           // no event cancel for compat with other mods that use RenderGameOverlayEvent.Post
					disable(); // prevent minecraft from rendering vanilla armor bar
				}
				break;
			
			case HEALTH:
				this.lastHealthHeight = baseY(event);
				
				if (conf("enableHealthBar")) {
					hudRenderer.renderHealth(event.getMatrixStack(), baseX(event), lastHealthHeight);
					
					if (conf("showAbsorption")) // absorb borders only work on stacked hearts
						hudRenderer.renderAbsorption(event.getMatrixStack(), baseX(event), lastHealthHeight);
					           // no event cancel for compat with other mods that use RenderGameOverlayEvent.Post
					disable(); // prevent minecraft from rendering vanilla health bar
				}
				break;
			default:
				return;
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void renderGameOverlayPost(RenderGameOverlayEvent.Post event) {
		switch (event.getType()) {
			case ARMOR:
				if (conf("enableArmorBar"))
					enable();
				if (conf("showResistance"))
					hudRenderer.renderResistance(event.getMatrixStack(), baseX(event), lastArmorHeight);
				return;
			
			case HEALTH:
				if (conf("enableHealthBar")) {
					enable();
					ForgeIngameGui.left_height = forgeLeftHeight(event, lastHealthHeight) + 10;
				}
				return;
			
			case TEXT:
				if (conf("debug") || debug) // TODO
					hudRenderer.debugText(event.getMatrixStack(), baseX(event), lastArmorHeight);
				if (conf("showArmorValue"))
					hudRenderer.renderArmorText(event.getMatrixStack(), baseX(event), lastArmorHeight);
				if (conf("showHealthValue"))
					hudRenderer.renderHealthText(event.getMatrixStack(), baseX(event), lastHealthHeight);
				return;
			default:
				return;
		}
	}
	
	private int baseX(RenderGameOverlayEvent event) {
		return event.getWindow().getGuiScaledWidth() / 2 - 91;
	}
	
	private int baseY(RenderGameOverlayEvent event) {
		return event.getWindow().getGuiScaledHeight() - ForgeIngameGui.left_height;
	}
	
	private int forgeLeftHeight(RenderGameOverlayEvent event, int height) {
		return event.getWindow().getGuiScaledHeight() - height;
	}
	
	private void enable() {
		RenderSystem.enableTexture();
		RenderSystem.color4f(1, 1, 1, 1);
	}
	
	private void disable() {
		RenderSystem.disableTexture();
		RenderSystem.color4f(0, 0, 0, 0);
	}
	
	private boolean conf(String name) {
		return ApppConfig.getBool(name);
	}
}
