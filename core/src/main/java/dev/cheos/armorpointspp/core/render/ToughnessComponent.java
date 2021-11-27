package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.OverlaySprite;

public class ToughnessComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.TOUTHNESS_ENABLE))
			return;
		
		tex(ctx).bind(ctx);
		int toughness = ctx.math.ceil(ctx.data.toughness() * ctx.config.dec(FloatOption.TOUGHNESS_VALUE));
		if (toughness <= 0) return;
		
		for (int i = 0; i < 10 && i < toughness; i++)
			tex(ctx).drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.TOUGHNESS_ICON);
	}
}
