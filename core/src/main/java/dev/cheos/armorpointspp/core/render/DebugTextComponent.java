package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class DebugTextComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.DEBUG))
			return;
		
		float maxHp  = ctx.data.maxHealth();
		float absorb = ctx.data.absorption();
		int   hpRows = ctx.math.ceil((maxHp + absorb) / 20F);
		
		ctx.renderer.text(ctx.poseStack, "armor should be here (vanilla)", ctx.x, ctx.y, 0xff0000);
		ctx.renderer.text(ctx.poseStack, "hp: "   + maxHp , 5,  5, 0xffffff);
		ctx.renderer.text(ctx.poseStack, "rows: " + hpRows, 5, 15, 0xffffff);
		
		ctx.renderer.text(ctx.poseStack, "armor = 0" , ctx.x, ctx.y - 40, ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_0   ));
		ctx.renderer.text(ctx.poseStack, "armor < 25", ctx.x, ctx.y - 30, ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_LT25));
		ctx.renderer.text(ctx.poseStack, "armor = 25", ctx.x, ctx.y - 20, ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_EQ25));
		ctx.renderer.text(ctx.poseStack, "armor > 25", ctx.x, ctx.y - 10, ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_GT25));
	}
}
