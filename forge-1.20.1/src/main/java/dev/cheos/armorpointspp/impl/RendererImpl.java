package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.SpriteInfo;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.common.util.Lazy;

public class RendererImpl implements IRenderer {
	// fallback / default texture sheet location
	private static final ResourceLocation ICONS = new ResourceLocation(Armorpointspp.MODID, "textures/gui/" + ITextureSheet.defaultSheet().texLocation() + ".png");
	private static final Map<String, ResourceLocation> resourceLocationCache = new HashMap<>();
	private final Minecraft minecraft = Minecraft.getInstance();
	private final Lazy<ExtendedGui> gui = Lazy.of(() -> (ExtendedGui) this.minecraft.gui);
	private ResourceLocation tex;
	
	@Override
	public void blit(IPoseStack poseStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		((PoseStackImpl) poseStack).getGraphics().blit(this.tex, x, y, u, v, width, height, texWidth, texHeight);
	}
	
	@Override
	public void blit(IPoseStack poseStack, int x, int y, float u, float v, int width, int height) {
		blit(poseStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite) {
		((PoseStackImpl) poseStack).getGraphics().blitSprite(resourceLocationCache.computeIfAbsent(sprite.location(), ResourceLocation::new), x, y, width, height);
	}
	
	@Override
	public void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite, int uOffset, int vOffset, int spriteWidth, int spriteHeight) {
		((PoseStackImpl) poseStack).getGraphics().blitSprite(
				resourceLocationCache.computeIfAbsent(sprite.location(), ResourceLocation::new),
				spriteWidth, spriteHeight,
				uOffset, vOffset,
				x, y,
				width, height);
	}
	
	@Override
	public void blitM(IPoseStack poseStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		Matrix4f mat = ((PoseStack) poseStack.getPoseStack()).last().pose();
		RenderSystem.setShaderTexture(0, this.tex);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(mat, x,         y         , 0).uv((u + width) / texWidth, (v         ) / texHeight).endVertex();
		bufferbuilder.vertex(mat, x,         y + height, 0).uv((u + width) / texWidth, (v + height) / texHeight).endVertex();
		bufferbuilder.vertex(mat, x + width, y + height, 0).uv((u        ) / texWidth, (v + height) / texHeight).endVertex();
		bufferbuilder.vertex(mat, x + width, y         , 0).uv((u        ) / texWidth, (v         ) / texHeight).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	}
	
	@Override
	public void blitM(IPoseStack poseStack, int x, int y, float u, float v, int width, int height) {
		blitM(poseStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		RenderSystem.setShaderColor(r, g, b, a);
	}
	
	@Override
	public void setupAppp() {
		this.gui.get().setupOverlayRenderState(true, false);
		this.tex = ICONS;
	}
	
	@Override
	public void setupTexture(ITextureSheet texSheet) {
		ResourceLocation location = texSheet.texLocation().indexOf(':') == -1
				? new ResourceLocation(Armorpointspp.MODID,
					"textures/gui/"
					+ texSheet.texLocation()
					+ ".png")
				: new ResourceLocation(texSheet.texLocation());
		this.gui.get().setupOverlayRenderState(true, false);
		this.tex = this.minecraft.getResourceManager().getResource(location).isPresent() ? location : ICONS;
	}
	
	@Override
	public void setupVanilla() {
		this.gui.get().setupOverlayRenderState(true, false);
	}
	
	@Override
	public void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType) {
		text(poseStack, text, x, y, color, renderType, ApppConfig.instance().bool(BooleanOption.TEXT_SHADOW));
	}
	
	@Override
	public void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType, boolean shadow) {
		((PoseStackImpl) poseStack).getGraphics().drawString(this.minecraft.font, comp(renderType, text), (int) x, (int) y, color, shadow);
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
