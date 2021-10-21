package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;

public class AbsorptionComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRender())
			return;
		
		float absorbAmp = ctx.config.dec(FloatOption.ABSORPTION_VALUE);
		int absorb = ctx.math.ceil(ctx.data.absorption());
		int fullBorders = ctx.math.floor(0.05F * absorb * absorbAmp);
		
		if (absorb <= 0 || absorbAmp <= 0) return;
		
		int inv = ctx.math.floor(20F / absorbAmp);
		boolean highlight = Components.HEALTH.healthBlinkTime() > Components.HEALTH.lastGuiTicks()
				&& (Components.HEALTH.healthBlinkTime() - Components.HEALTH.lastGuiTicks()) / 3L % 2L == 1L;
		
		for (int i = 9; i >= 0; i--) {
			if (i > fullBorders) continue;
			
			int heartX = ctx.x + i * 8;
			int heartY = Components.HEALTH.lastHeartY()[i]; // borders should of course line up with hearts :)
			
			if (i < fullBorders) ctx.renderer.blit(ctx.poseStack, heartX, heartY, highlight ? 18 : 0, 99, 9, 9);
			else if (i == fullBorders && absorb % inv != 0)
				ctx.renderer.blit(ctx.poseStack, heartX, heartY,
						(highlight ? 18 : 0) + 9 * (ctx.math.ceil(absorb * absorbAmp) % 2),
						9 + 9 * ctx.math.ceil((absorb % inv) / 8F), 9, 9);
		}
	}
}
