package dev.cheos.armorpointspp.core.render;

import java.awt.Color;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class ArmorComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.ARMOR_ENABLE))
			return false;
		
		ctx.profiler.push("armor");
		ITextureSheet tex = tex(ctx).bind(ctx);
		int armor = Math.min(ctx.data.armor(), 240);
		if (!ctx.config.bool(BooleanOption.DISABLE_EASTEREGGS) && armor == Math.min(137, ctx.data.maxArmor())) {
			renderRainbowArmor(ctx, ctx.x, ctx.y);
			return popReturn(ctx, true);
		}
		
		for (int i = 0; i < 10; i++)
			tex.drawArmor(ctx, ctx.x + 8 * i, ctx.y, (int) ((armor - 2 * (i + 1) + 20) * 0.05F), (armor % 20) - 2 * i == 1);
		return popReturn(ctx, true);
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
