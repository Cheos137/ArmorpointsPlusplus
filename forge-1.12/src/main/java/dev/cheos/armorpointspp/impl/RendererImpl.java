package dev.cheos.armorpointspp.impl;

import dev.cheos.armorpointspp.Armorpointspp;
import dev.cheos.armorpointspp.config.ApppConfig;
import dev.cheos.armorpointspp.core.SpriteInfo;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IPoseStack;
import dev.cheos.armorpointspp.core.adapter.IRenderer;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class RendererImpl implements IRenderer {
	// fallback / default texture sheet location
	private static final ResourceLocation ICONS = new ResourceLocation(Armorpointspp.MODID, "textures/gui/" + ITextureSheet.defaultSheet().texLocation() + ".png");
	private final Minecraft minecraft = Minecraft.getMinecraft();
	
	@Override
	public void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, texWidth, texHeight);
	}
	
	@Override
	public void blit(IPoseStack pStack, int x, int y, float u, float v, int width, int height) {
		blit(pStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite) {
		blit(poseStack, x, y, sprite.u(), sprite.v(), width, height);
	}
	
	@Override
	public void blitSprite(IPoseStack poseStack, int x, int y, int width, int height, SpriteInfo sprite, int uOffset, int vOffset, int spriteWidth, int spriteHeight) {
		blit(poseStack, x, y, sprite.u() + uOffset, sprite.v() + vOffset, width, height);
	}
	
	@Override
	public void blitM(IPoseStack pStack, int x, int y, float u, float v, int width, int height, int texWidth, int texHeight) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x,         y         , 0).tex((u + width) / texWidth, (v         ) / texHeight).endVertex();
		bufferbuilder.pos(x,         y + height, 0).tex((u + width) / texWidth, (v + height) / texHeight).endVertex();
		bufferbuilder.pos(x + width, y + height, 0).tex((u        ) / texWidth, (v + height) / texHeight).endVertex();
		bufferbuilder.pos(x + width, y         , 0).tex((u        ) / texWidth, (v         ) / texHeight).endVertex();
		tessellator.draw();
	}
	
	@Override
	public void blitM(IPoseStack pStack, int x, int y, float u, float v, int width, int height) {
		blitM(pStack, x, y, u, v, width, height, 256, 128);
	}
	
	@Override
	public void setColor(float r, float g, float b, float a) {
		GlStateManager.color(r, g, b, a);
	}
	
	@Override
	public void setupAppp() {
		setup(true, false, ICONS);
	}
	
	@Override
	public void setupTexture(ITextureSheet texSheet) {
		ResourceLocation location = texSheet.texLocation().indexOf(':') == -1
				? new ResourceLocation(Armorpointspp.MODID,
					"textures/gui/"
					+ texSheet.texLocation()
					+ ".png")
				: new ResourceLocation(texSheet.texLocation());
		try {
			setup(true, false, this.minecraft.getResourceManager().getResource(location) != null ? location : ICONS);
		} catch (Throwable t) {
			setup(true, false, ICONS);
		}
	}
	
	@Override
	public void setupVanilla() {
		setup(true, false, Gui.ICONS);
	}
	
	private void setup(boolean blend, boolean depthTest, ResourceLocation texture) {
		if (blend) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);;
		} else GlStateManager.disableBlend();
		if (depthTest)
			GlStateManager.enableDepth();
//		else GlStateManager.disableDepth();
		if (texture != null) {
			GlStateManager.enableTexture2D();
			this.minecraft.getTextureManager().bindTexture(texture);
		} else GlStateManager.disableTexture2D();
		setColor(1, 1, 1, 1);
	}
	
	@Override
	public void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType) {
		text(poseStack, text, x, y, color, renderType, ApppConfig.instance().bool(BooleanOption.TEXT_SHADOW));
	}
	
	@Override
	public void text(IPoseStack poseStack, String text, float x, float y, int color, TextRenderType renderType, boolean shadow) {
		this.minecraft.fontRenderer.drawString(comp(renderType, text).getFormattedText(), x, y, color, shadow);
	}
	
	@Override
	public int width(Object... objs) {
		if (objs == null) return 0;
		if (objs.length == 1) return this.minecraft.fontRenderer.getStringWidth(String.valueOf(objs[0]));
		
		StringBuilder s = new StringBuilder();
		for (Object obj : objs)
			s.append(obj);
		return this.minecraft.fontRenderer.getStringWidth(s.toString());
	}
	
	private static ITextComponent comp(TextRenderType type, String text) {
		return type == TextRenderType.LANG ? new TextComponentTranslation(text) : new TextComponentString(text);
	}
}
