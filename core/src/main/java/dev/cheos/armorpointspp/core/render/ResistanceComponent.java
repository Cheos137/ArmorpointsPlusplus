package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.OverlaySprite;

public class ResistanceComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.RESISTANCE_ENABLE))
			return;
		
		tex(ctx).bind(ctx);
		int armor = ctx.data.armor();
		int resistance = 0;
		
		if (ctx.data.isEffectActive(ctx.data.effects().resistance()))
			resistance = 1 + ctx.data.getActiveEffect(ctx.data.effects().resistance()).amplifier();
		if (resistance <= 0) return;
		for (int i = 0; i < 10 && i < resistance * ctx.config.dec(FloatOption.RESISTANCE_VALUE); i++, armor -= 2)
			if      (armor      <= 0) tex(ctx).drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.RESISTANCE_NONE);
			else if (armor % 20 == 1) tex(ctx).drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.RESISTANCE_HALF);
			else                      tex(ctx).drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.RESISTANCE_FULL);
	}
}
