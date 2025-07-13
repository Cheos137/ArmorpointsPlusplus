package dev.cheos.armorpointspp.impl;

import java.util.HashMap;
import java.util.Map;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.SpriteInfo;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RendererImpl implements IRenderer {
	// fallback / default texture sheet location
	private static final ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID, "textures/gui/" + ITextureSheet.defaultSheet().texLocation() + ".png");
	private static final Map<String, ResourceLocation> resourceLocationCache = new HashMap<>();
	private final Minecraft minecraft = Minecraft.getInstance();
	private final GuiSpriteManager sprites = this.minecraft.getGuiSprites();
	private ResourceLocation tex;
	private int color;
	
	@Override
	public void blit(IPoseStack poseStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		((PoseStackImpl) poseStack).getGraphics().blit(
				RenderPipelines.GUI_TEXTURED,
				this.tex,
				x, y,
				u, v,
				width, height,
				texWidth, texHeight,
				this.color);
	}
	
	@Override
	public void blit(IPoseStack poseStack, int x, int y, float u, float v, int width, int height) {
		blit(poseStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite) {
		((PoseStackImpl) poseStack).getGraphics().blitSprite(
				RenderPipelines.GUI_TEXTURED,
				resourceLocationCache.computeIfAbsent(sprite.location(), ResourceLocation::parse),
				x, y,
				width, height,
				this.color);
	}
	
	@Override
	public void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite, int uOffset, int vOffset, int spriteWidth, int spriteHeight) {
		// fsr they didn't make a method for when you wanna use all them params
		ResourceLocation tex = resourceLocationCache.computeIfAbsent(sprite.location(), ResourceLocation::parse);
		TextureAtlasSprite taSprite = this.sprites.getSprite(tex);
		((PoseStackImpl) poseStack).getGraphics().blitSprite(
				RenderPipelines.GUI_TEXTURED,
				taSprite,
				spriteWidth, spriteHeight,
				uOffset, vOffset,
				x, y,
				width, height,
				this.color);
	}
	
	@Override
	public void blitM(IPoseStack poseStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		((PoseStackImpl) poseStack).getGraphics().blit(
				RenderPipelines.GUI_TEXTURED,
				this.tex,
				x, y,
				u + width, v,
				width, height, // uWidth, uHeight
				-width, height,
				texWidth, texHeight,
				this.color);
	}
	
	@Override
	public void blitM(IPoseStack poseStack, int x, int y, float u, float v, int width, int height) {
		blitM(poseStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void setColor(int color) {
		this.color = color;
	}
	
	@Override
	public void setColorRGB(int color) {
		this.color = color | 0xFF000000; // ARGB
	}
	
	private void setColor(int r, int g, int b, int a) {
		setColor(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		setColor((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F));
	}
	
	@Override
	public void setupAppp() {
		setColor(0xFFFFFFFF);
		this.tex = ICONS;
	}
	
	@Override
	public void setupTexture(ITextureSheet texSheet) {
		ResourceLocation location = texSheet.texLocation().indexOf(':') == -1
				? ResourceLocation.fromNamespaceAndPath(Armorpointspp.MODID,
					"textures/gui/"
					+ texSheet.texLocation()
					+ ".png")
				: ResourceLocation.withDefaultNamespace(texSheet.texLocation());
		setColor(0xFFFFFFFF);
		this.tex = this.minecraft.getResourceManager().getResource(location).isPresent() ? location : ICONS;
	}
	
	@Override
	public void setupVanilla() {
		setColor(0xFFFFFFFF);
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
