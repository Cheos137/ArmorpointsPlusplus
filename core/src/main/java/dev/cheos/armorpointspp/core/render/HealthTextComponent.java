package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.RenderableText;
import dev.cheos.armorpointspp.core.RenderableText.Alignment;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class HealthTextComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.HEALTH_TEXT_ENABLE))
			return;
		
		RenderableText text = new RenderableText("")
				.withAlignment(Alignment.RIGHT)
				.withShadow(ctx.config.bool(BooleanOption.TEXT_SHADOW));
		
		int freeze = Math.round(100 * ctx.data.percentFrozen());
		int maxHp  = ctx.math.ceil(ctx.data.maxHealth());
		int absorb = ctx.math.ceil(ctx.data.absorption());
		int health = ctx.math.ceil(ctx.data.health());
		int hpcol  = ctx.data.isFullyFrozen()
				? ctx.config.hex(IntegerOption.TEXT_COLOR_FROSTBITE)
				: ctx.data.isEffectActive(ctx.data.effects().poison())
						? ctx.config.hex(IntegerOption.TEXT_COLOR_POISON)
						: ctx.data.isEffectActive(ctx.data.effects().wither())
								? ctx.config.hex(IntegerOption.TEXT_COLOR_WITHER)
								: ctx.config.hex(IntegerOption.TEXT_COLOR_HEART);
		
		if(health > maxHp) health = maxHp;
		
		text.append(new RenderableText(health).padRight(1).withColor(hpcol));
		
		if(health < maxHp) {
			text.append(new RenderableText("/"   ).padRight(1).withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR )));
			text.append(new RenderableText(maxHp ).padRight(1).withColor(hpcol));
		}
		
		if(absorb > 0) {
			text.append(new RenderableText("+"   ).padRight(1).withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR )));
			text.append(new RenderableText(absorb).padRight(1).withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_ABSORPTION)));
		}
		
		if (freeze > 0 && ctx.config.bool(BooleanOption.FROSTBITE_TEXT_ENABLE)) {
			text.append(new RenderableText(","   ).padRight(1).withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR)));
			text.append(new RenderableText(freeze).padRight(1).withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_FROSTBITE)));
			text.append(new RenderableText("%"   ).padRight(1).withColor(ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR)));
		}
		
		text.render(ctx.poseStack, ctx.renderer, ctx.x, ctx.y + 1);
	}
}
