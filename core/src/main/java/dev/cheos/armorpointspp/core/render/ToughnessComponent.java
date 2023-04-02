package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class ToughnessComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderToughness() || !ctx.config.bool(BooleanOption.TOUGHNESS_ENABLE) || !ctx.config.bool(BooleanOption.TOUGHNESS_BAR))
			return false;
		
		ctx.profiler.push("toughness");
		ITextureSheet tex = tex(ctx).bind(ctx);
		long toughness = ctx.math.floor(ctx.data.toughness() * 2);
		
		for (int i = 0; i < 10; i++)
			tex.drawToughness(ctx, ctx.x + 8 * i, ctx.y, (int) ((toughness - 2 * (i + 1) + 20) * 0.05F), (toughness % 20) - 2 * i == 1, false);
		return popReturn(ctx, true);
	}
}
