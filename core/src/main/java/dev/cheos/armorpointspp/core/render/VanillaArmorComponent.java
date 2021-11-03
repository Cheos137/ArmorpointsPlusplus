package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;

public class VanillaArmorComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || ctx.config.bool(BooleanOption.ARMOR_ENABLE))
			return;
		
		ctx.renderer.setupVanilla();
		int x = ctx.x;
		int armor = ctx.data.armor();
		for (int i = 1; armor > 0 && i < 20; i += 2) {
			ctx.renderer.blit(ctx.poseStack, x, ctx.y, i < armor ? 34 : i == armor ? 25 : 16, 9, 9, 9);
			x += 8;
		}
	}
}
