package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;

public class ResistanceComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor())
			return;
		
		int armor = ctx.data.armor();
		int resistance = 0;
		
		if (ctx.data.isEffectActive(ctx.data.effects().resistance()))
			resistance = 1 + ctx.data.getActiveEffect(ctx.data.effects().resistance()).amplifier();
		if (resistance <= 0) return;
		for (int i = 0; i < 10 && i < resistance * ctx.config.dec(FloatOption.RESISTANCE_VALUE); i++, armor -= 2)
			if      (armor      <= 0) ctx.renderer.blit(ctx.poseStack, ctx.x + 8 * i, ctx.y,  0, 0, 9, 9);
			else if (armor % 20 == 1) ctx.renderer.blit(ctx.poseStack, ctx.x + 8 * i, ctx.y,  9, 0, 9, 9);
			else                      ctx.renderer.blit(ctx.poseStack, ctx.x + 8 * i, ctx.y, 18, 0, 9, 9);
	}
}
