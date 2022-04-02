package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class ToughnessOverlayComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.TOUGHNESS_ENABLE) || ctx.config.bool(BooleanOption.TOUGHNESS_BAR))
			return false;
		
		ctx.profiler.push("toughness");
		int toughness = ctx.data.toughness();
		toughness = ctx.math.ceil(toughness * ctx.config.dec(FloatOption.TOUGHNESS_VALUE));
		if (toughness <= 0) return popReturn(ctx, false);
		ITextureSheet tex = tex(ctx).bind(ctx);
		
		for (int i = 0; i < 10 && i < toughness; i++)
			tex.drawToughness(ctx, ctx.x + 8 * i, ctx.y, (int) ((toughness - i - 1) * 0.1F), false, true);
		return popReturn(ctx, true);
	}
}
