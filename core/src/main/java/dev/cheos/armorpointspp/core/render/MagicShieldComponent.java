package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class MagicShieldComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.POTIONCORE_COMPAT))
			return false;
		
		ctx.profiler.push("magic_resistance");
		if (!ctx.config.bool(BooleanOption.ARMOR_ENABLE) && ctx.data.armor() <= 0)
			popReturn(ctx, false); // we use vanilla armor which doesn't draw if 0
		
		int magicShield = (int) (ctx.data.potionCore().magicShield() * 0.5);
		if (magicShield <= 0) return popReturn(ctx, false);
		ITextureSheet tex = tex(ctx).bind(ctx);
		
		for (int i = 0; i < 10 && i < magicShield; i++)
			tex.drawMagicShield(ctx, ctx.x + 8 * i, ctx.y, (int) ctx.data.millis() / 110 + i);
		return popReturn(ctx, true);
	}
}
