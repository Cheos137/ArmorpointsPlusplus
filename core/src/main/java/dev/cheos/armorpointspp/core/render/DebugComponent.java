package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;

public class DebugComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor())
			return;
		
		ctx.poseStack.pushPose();
		ctx.poseStack.scale(0.8F, 0.8F, 1);
		ctx.poseStack.translate(1.25D, 6.25D, 0);
		ctx.renderer.blit(ctx.poseStack, 5, 25, 0, 0, 256, 128);
		ctx.poseStack.popPose();
	}
}
