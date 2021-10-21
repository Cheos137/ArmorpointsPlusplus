package dev.cheos.armorpointspp.core.render;

import java.awt.Color;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.Suffix;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class ArmorTextComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor())
			return;
		
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
		
		ctx.renderer.text(ctx.poseStack, significand, ctx.x - ctx.renderer.width(significand) - 1, ctx.y + 1, color, ctx.config.bool(BooleanOption.TEXT_SHADOW));
	}
}
