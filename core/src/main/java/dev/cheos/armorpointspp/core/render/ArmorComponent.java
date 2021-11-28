package dev.cheos.armorpointspp.core.render;

import java.awt.Color;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class ArmorComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.ARMOR_ENABLE))
			return;
		
		ITextureSheet tex = tex(ctx);
		tex.bind(ctx);
		int armor = Math.min(ctx.data.armor(), 240);
		if (armor == 137 || (!ctx.data.isAttributeFixLoaded() && armor == 30)) {
			renderRainbowArmor(ctx, ctx.x, ctx.y);
			return;
		}
		
		for (int i = 0; i < 10; i++)
			tex.drawArmor(ctx, ctx.x + 8 * i, ctx.y, (armor - i * 2 + 19) / 20, (armor % 20) - 2 * i == 1);
	}
	
	// fun rainbow armor bar only visible on 137 armor -- why? because 137 is my favourite number ^^
	private void renderRainbowArmor(RenderContext ctx, int x, int y) {
		ITextureSheet tex = tex(ctx);
		ctx.poseStack.pushPose();
		
		long millis = ctx.data.millis() / 40;
		int color = 0;
		
		for (int i = 0; i < 10; i++) {
			millis += 5;
			color = Color.HSBtoRGB((millis % 360) / 360F, 1, 1);
			ctx.renderer.setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, 1);
			tex.drawArmor(ctx, x + 8 * i, y, 1, false);
		}
		ctx.poseStack.popPose();
	}
}
