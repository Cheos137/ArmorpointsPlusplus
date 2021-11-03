package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;

public class ToughnessComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.TOUTHNESS_ENABLE))
			return;
		
		int toughness = ctx.math.ceil(ctx.data.toughness() * ctx.config.dec(FloatOption.TOUGHNESS_VALUE));
		if (toughness <= 0) return;
		
		ctx.renderer.setupAppp();
		ctx.poseStack.pushPose();
		ctx.poseStack.scale(0.5F, 0.5F, 1);
		
		for (int i = 0; i < 10 && i < toughness; i++)
			ctx.renderer.blit(ctx.poseStack, 2 * (ctx.x + 8 * i) + 9, 2 * ctx.y + 8, 27, 9, 9, 9);
		
		ctx.poseStack.popPose();
	}
}
