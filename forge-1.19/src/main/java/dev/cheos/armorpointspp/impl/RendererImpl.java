package dev.cheos.armorpointspp.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;

public class RendererImpl implements IRenderer {
	// fallback / default texture sheet location
	private static final ResourceLocation ICONS = new ResourceLocation(Armorpointspp.MODID, "textures/gui/" + ITextureSheet.defaultSheet().texLocation() + ".png");
	private final Minecraft minecraft = Minecraft.getInstance();
	private final ForgeIngameGui gui = (ForgeIngameGui) this.minecraft.gui;
	
	@Override
	public void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		GuiComponent.blit((PoseStack) pStack.getPoseStack(), x, y, u, v, width, height, texWidth, texHeight);
	}
	
	@Override
	public void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height) {
		blit(pStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		RenderSystem.setShaderColor(r, g, b, a);
	}
	
	@Override
	public void setupAppp() {
		this.gui.setupOverlayRenderState(true, false, ICONS);
	}
	
	@Override
	public void setupTexture(ITextureSheet texSheet) {
		ResourceLocation location = texSheet.texLocation().indexOf(':') == -1
				? new ResourceLocation(Armorpointspp.MODID,
					"textures/gui/"
					+ texSheet.texLocation()
					+ ".png")
				: new ResourceLocation(texSheet.texLocation());
		this.gui.setupOverlayRenderState(true, false, this.minecraft.getResourceManager().getResource(location).isPresent() ? location : ICONS);
	}
	
	@Override
	public void setupVanilla() {
		this.gui.setupOverlayRenderState(true, false);
	}
	
	@Override
	public void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType) {
		text(poseStack, text, x, y, color, renderType, ApppConfig.instance().bool(BooleanOption.TEXT_SHADOW));
	}
	
	@Override
	public void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType, boolean shadow) {
		if (shadow)
			this.minecraft.font.drawShadow((PoseStack) poseStack.getPoseStack(), comp(renderType, text), x, y, color);
		else this.minecraft.font.draw((PoseStack) poseStack.getPoseStack(), comp(renderType, text), x, y, color);
	}
	
	@Override
	public int width(Object... objs) {
		if (objs == null) return 0;
		if (objs.length == 1) return this.minecraft.font.width(String.valueOf(objs[0]));
		
		StringBuilder s = new StringBuilder();
		for (Object obj : objs)
			s.append(obj);
		return this.minecraft.font.width(s.toString());
	}
	
	private static Component comp(TextRenderType type, String text) {
		return type == TextRenderType.LANG ? Component.translatable(text) : Component.literal(text);
	}
}
