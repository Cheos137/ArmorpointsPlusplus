package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.OverlaySprite;

public class AbsorptionOverlayComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.ABSORPTION_ENABLE) || !ctx.config.bool(BooleanOption.HEALTH_ENABLE) || !ctx.config.bool(BooleanOption.ABSORPTION_OVERLAY))
			return false;
		
		ctx.profiler.push("absorption");
		ITextureSheet tex = tex(ctx).bind(ctx);
		boolean frozen   = ctx.data.isFullyFrozen();
		boolean hardcore = ctx.data.isHardcore() || ctx.data.isEffectActive("spectrum:divinity");
		int health       = ctx.math.ceil(ctx.data.health());
		int heartStack   = (health - 1) / 20;
		int absorb       = ctx.math.ceil(ctx.data.absorption());
		int totalHealth  = health + absorb;
		
		if (absorb <= 0) return popReturn(ctx, false);
		
		for (int i = 0; i <= 9; i++) {
			int heartX = ctx.x + i * 8;
			int heartY = Components.HEALTH.lastHeartY()[i];
			int heartValue = i * 2 + heartStack * 20 + 1;
			int heartValueA = heartValue + 20;
			
			if (absorb >= 20) tex.drawAbsorbHeart(ctx, heartX, heartY, false, false, hardcore); // small optimization
			else if (heartValueA <= totalHealth) tex.drawAbsorbHeart(ctx, heartX, heartY, heartValueA == totalHealth, false, hardcore);
			else if (heartValue == health) tex.drawAbsorbHeart(ctx, heartX + 5, heartY, true, true, hardcore);
			else if (heartValue < totalHealth && heartValue > health) tex.drawAbsorbHeart(ctx, heartX, heartY, false, false, hardcore);
			else if (heartValue == totalHealth) tex.drawAbsorbHeart(ctx, heartX, heartY, true, false, hardcore);
			
			// draw frostbite here, as we overlay hearts directly
			if (frozen)
				switch (ctx.config.enm(EnumOption.FROSTBITE_STYLE)) {
					case FULL:
						if (heartValue <= totalHealth)
							tex.drawOverlay(ctx, heartX, heartY, heartValue == totalHealth, hardcore, OverlaySprite.FROSTBITE_FULL);
						break;
					case OVERLAY:
						if (heartValue <= totalHealth)
							tex.drawOverlay(ctx, heartX, heartY, heartValue == totalHealth, hardcore, OverlaySprite.FROSTBITE);
						break;
					case ICON: // fallthrough, icon is default
					default:
						if (heartValue < totalHealth)
							tex.drawOverlay(ctx, heartX, heartY, false, hardcore, OverlaySprite.FROSTBITE_ICON);
						break;
				}
		}
		return popReturn(ctx, true);
	}
}
