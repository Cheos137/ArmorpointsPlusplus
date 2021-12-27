package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.*;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class ToughnessTextComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderToughness() || !ctx.config.bool(BooleanOption.TOUGHNESS_ENABLE) || !ctx.config.bool(BooleanOption.TOUGHNESS_BAR))
			return false;
		
		ctx.profiler.push("toughnessText");
		int toughness = ctx.data.toughness();
		Suffix.Type type = ctx.config.enm(EnumOption.SUFFIX);
		int power = toughness == 0 ? 0 : (int) Math.log10(toughness);
		if (type != Suffix.Type.SCI && power < 27) power = power / 3 * 3; // 100 YOTTA is max. if higher switch to SCI notation
		else type = Suffix.Type.SCI;
		
		String significand = String.valueOf(ctx.math.floor(toughness / Math.pow(10, power) * 10F) / 10F);    // one decimal precision
		if(significand.endsWith(".0")) significand = significand.substring(0, significand.length() - 2); // strip .0
		significand += (type == Suffix.Type.SCI ? "E" + power : Suffix.byPow(power).getSuffix(type));    // add suffix
		
		int color = ctx.config.hex(IntegerOption.TEXT_COLOR_TOUGHNESS);
		Side side = ctx.config.enm(EnumOption.TOUGHNESS_SIDE);
		
		if (ctx.config.bool(BooleanOption.TOUGHNESS_TEXT_CONFIG_ENABLE))
			 new RenderableText(significand)
				.withAlignment(ctx.config.enm(EnumOption.TOUGHNESS_TEXT_ALIGNMENT))
				.withColor(color)
				.withShadow(ctx.config.bool(BooleanOption.TEXT_SHADOW))
				.render(ctx.poseStack, ctx.renderer, ctx.config.dec(FloatOption.TOUGHNESS_TEXT_X), ctx.config.dec(FloatOption.TOUGHNESS_TEXT_Y));
		else new RenderableText(significand)
				.withAlignment(side.preferredAlignment())
				.withColor(color)
				.withShadow(ctx.config.bool(BooleanOption.TEXT_SHADOW))
				.render(ctx.poseStack, ctx.renderer, ctx.x + (side == Side.RIGHT ? 83 : -1), ctx.y + 0.5F); // 82 would be the correct distance, though 83 feels better because the shadow takes one additional pixel when rendering on the left side -> +1 distance when on right side
		return popReturn(ctx, true);
	}
}
