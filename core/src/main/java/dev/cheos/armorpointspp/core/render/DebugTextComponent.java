package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.RenderableText;
import dev.cheos.armorpointspp.core.RenderableText.Alignment;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class DebugTextComponent implements IRenderComponent {
	private static RenderableText right  = new RenderableText("this should be left and red with no shadow").withAlignment(Alignment.LEFT  ).withColor(0xff0000).withShadow(false);
	private static RenderableText center = new RenderableText("this should be centered and green"         ).withAlignment(Alignment.CENTER).withColor(0x00ff00);
	private static RenderableText left   = new RenderableText("this should be right and blue"             ).withAlignment(Alignment.RIGHT ).withColor(0x0000ff);
	
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.DEBUG))
			return false;
		
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
		
		right .render(ctx.poseStack, ctx.renderer, ctx.x, ctx.y - 60);
		center.render(ctx.poseStack, ctx.renderer, ctx.x, ctx.y - 70);
		left  .render(ctx.poseStack, ctx.renderer, ctx.x, ctx.y - 80);
		
		new RenderableText("R")
		.withColor(0xff0000)
		.withAlignment(Alignment.CENTER)
		.append(
				new RenderableText("G")
				.withColor(0x00ff00)
				.append(
						new RenderableText("B")
						.withColor(0x0000ff)))
		.append(
				new RenderableText("A")
				.withColor(0xffffff)
				.withShadow(false))
		.render(ctx.poseStack, ctx.renderer, ctx.x, ctx.y - 50);
		return true;
	}
}
