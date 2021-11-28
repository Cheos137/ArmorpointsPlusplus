package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class AbsorptionComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.ABSORPTION_ENABLE) || !ctx.config.bool(BooleanOption.HEALTH_ENABLE))
			return;
		
		ITextureSheet tex = tex(ctx).bind(ctx);
		float absorbAmp = ctx.config.dec(FloatOption.ABSORPTION_VALUE);
		int absorb = ctx.math.ceil(ctx.data.absorption());
		int fullBorders = ctx.math.floor(0.05F * absorb * absorbAmp);
		
		if (absorb <= 0 || absorbAmp <= 0) return;
		
		float inv = 20F / absorbAmp;
		boolean highlight = Components.HEALTH.healthBlinkTime() > Components.HEALTH.lastGuiTicks()
				&& (Components.HEALTH.healthBlinkTime() - Components.HEALTH.lastGuiTicks()) / 3L % 2L == 1L;
		
		for (int i = 9; i >= 0; i--) {
			if (i > fullBorders) continue;
			
			int heartX = ctx.x + i * 8;
			int heartY = Components.HEALTH.lastHeartY()[i]; // borders should of course line up with hearts :)
			
			if (i < fullBorders) tex.drawAbsorb(ctx, heartX, heartY, 20, highlight);
			else if (i == fullBorders && (absorb * absorbAmp) % 20 != 0)
				tex.drawAbsorb(ctx, heartX, heartY, ctx.math.ceil(absorb * absorbAmp) % 20, highlight);
		}
	}
}
