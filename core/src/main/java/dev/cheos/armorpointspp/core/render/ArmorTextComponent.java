package dev.cheos.armorpointspp.core.render;

import java.awt.Color;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.RenderableText;
import dev.cheos.armorpointspp.core.RenderableText.Alignment;
import dev.cheos.armorpointspp.core.Suffix;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class ArmorTextComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || !ctx.config.bool(BooleanOption.ARMOR_TEXT_ENABLE))
			return false;
		
		int armor = ctx.data.armor();
		Suffix.Type type = ctx.config.enm(EnumOption.SUFFIX);
		int resistance = ctx.data.isEffectActive(ctx.data.effects().resistance())
				? ctx.data.getActiveEffect(ctx.data.effects().resistance()).amplifier()
				: 0;
		
		int power = armor == 0 ? 0 : (int) Math.log10(armor);
		if (type != Suffix.Type.SCI && power < 27) power = power / 3 * 3; // 100 YOTTA is max. if higher switch to SCI notation
		else type = Suffix.Type.SCI;
		
		String significand = String.valueOf(ctx.math.floor(armor / Math.pow(10, power) * 10F) / 10F);    // one decimal precision
		if(significand.endsWith(".0")) significand = significand.substring(0, significand.length() - 2); // strip .0
		significand += (type == Suffix.Type.SCI ? "E" + power : Suffix.byPow(power).getSuffix(type));    // add suffix
		
		int color;
		if (armor == 137 || (!ctx.data.isAttributeFixLoaded() && armor == 30))
			color = Color.HSBtoRGB((((ctx.data.millis() + 80) / 40) % 360) / 360F, 1, 1);
		else if (resistance >= 4) color = ctx.config.hex(IntegerOption.TEXT_COLOR_FULL_RESISTANCE);
		else if (armor == 0) color = ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_0);
		else if (armor < 25) color = ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_LT25);
		else if (armor > 25) color = ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_GT25);
		else color = ctx.config.hex(IntegerOption.TEXT_COLOR_ARMOR_EQ25);
		
		if (ctx.config.bool(BooleanOption.ARMOR_TEXT_CONFIG_ENABLE))
			 new RenderableText(significand)
				.withAlignment(ctx.config.enm(EnumOption.ARMOR_TEXT_ALIGNMENT))
				.withColor(color)
				.withShadow(ctx.config.bool(BooleanOption.TEXT_SHADOW))
				.append(toughness(ctx))
				.render(ctx.poseStack, ctx.renderer, ctx.config.dec(FloatOption.ARMOR_TEXT_X), ctx.config.dec(FloatOption.ARMOR_TEXT_Y));
		else new RenderableText(significand)
				.withAlignment(Alignment.RIGHT)
				.withColor(color)
				.withShadow(ctx.config.bool(BooleanOption.TEXT_SHADOW))
				.append(toughness(ctx))
				.render(ctx.poseStack, ctx.renderer, ctx.x - 1, ctx.y + 0.5F);
		return true;
	}
	
	private static RenderableText toughness(RenderContext ctx) {
		if (!ctx.config.bool(BooleanOption.TOUGHNESS_ENABLE) || ctx.config.bool(BooleanOption.TOUGHNESS_BAR))
			return RenderableText.EMPTY;
		
		int toughness = ctx.data.toughness();
		if (toughness == 0)
			return RenderableText.EMPTY;
		
		Suffix.Type type = ctx.config.enm(EnumOption.SUFFIX);
		int power = toughness == 0 ? 0 : (int) Math.log10(toughness);
		if (type != Suffix.Type.SCI && power < 27) power = power / 3 * 3; // 100 YOTTA is max. if higher switch to SCI notation
		else type = Suffix.Type.SCI;
		
		String significand = String.valueOf(ctx.math.floor(toughness / Math.pow(10, power) * 10F) / 10F);    // one decimal precision
		if(significand.endsWith(".0")) significand = significand.substring(0, significand.length() - 2); // strip .0
		significand += (type == Suffix.Type.SCI ? "E" + power : Suffix.byPow(power).getSuffix(type));    // add suffix
		
		int color = ctx.config.hex(IntegerOption.TEXT_COLOR_TOUGHNESS);
		return new RenderableText("+")
				.pad(1)
				.withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR))
				.append(new RenderableText(significand).withColor(color));
		
	}
}
