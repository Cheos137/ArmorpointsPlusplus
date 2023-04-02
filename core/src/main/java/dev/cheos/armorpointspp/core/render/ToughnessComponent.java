package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class ToughnessComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderToughness() || !ctx.config.bool(BooleanOption.TOUGHNESS_ENABLE) || !ctx.config.bool(BooleanOption.TOUGHNESS_BAR))
			return false;
		
		ctx.profiler.push("toughness");
		ITextureSheet tex = tex(ctx).bind(ctx);
		long toughness = ctx.math.floor(ctx.data.toughness() * 2);
		Mirroring mirroring = ctx.config.enm(EnumOption.TOUGHNESS_MIRRORING);
		boolean mirror = mirroring == Mirroring.ALWAYS || (mirroring == Mirroring.AUTO && ctx.config.enm(EnumOption.TOUGHNESS_SIDE) == Side.RIGHT);
		
		for (int i = 0; i < 10; i++)
			tex.drawToughness(
					ctx,
					ctx.x + 8 * (mirror ? 9 - i : i),
					ctx.y,
					(int) ((toughness - 2 * (i + 1) + 20) * 0.05F),
					(toughness % 20) - 2 * i == 1,
					false,
					mirror);
		
		return popReturn(ctx, true);
	}
}
