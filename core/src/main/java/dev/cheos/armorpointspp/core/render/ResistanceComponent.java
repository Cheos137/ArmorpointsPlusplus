package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.OverlaySprite;

public class ResistanceComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.RESISTANCE_ENABLE) || ctx.config.bool(BooleanOption.ARMOR_HIDDEN))
			return false;
		
		ctx.profiler.push("resistance");
		int armor = ctx.data.armor();
		int resistance = 0;
		
		if (ctx.data.isEffectActive(ctx.data.effects().resistance()))
			resistance = 1 + ctx.data.getActiveEffect(ctx.data.effects().resistance()).amplifier();
		if (ctx.data.isPotionCoreLoaded())
			resistance += (ctx.data.potionCore().resistance() - 1) * 5; // potioncore: 0 if unsupported, 1 := no resistance, 2 := 100% resistance, <1 := weakening
		resistance = Math.min(resistance, 5);                          // resistance maxes out at 5
		
		resistance = ctx.math.ceil(resistance * ctx.config.dec(FloatOption.RESISTANCE_VALUE));
		resistance = ctx.math.clamp(resistance, 0, 10);
		if (resistance <= 0) return popReturn(ctx, false);
		
		ITextureSheet tex = tex(ctx).bind(ctx);
		
		for (int i = 0; i < 10 && i < resistance; i++, armor -= 2) // actually, this double check for limit = 10 is not necessary... still feels better with it
			if      (armor      <= 0) tex.drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.RESISTANCE_NONE);
			else if (armor % 20 == 1) tex.drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.RESISTANCE_HALF);
			else                      tex.drawOverlay(ctx, ctx.x + 8 * i, ctx.y, false, false, OverlaySprite.RESISTANCE_FULL);
		return popReturn(ctx, true);
	}
}
