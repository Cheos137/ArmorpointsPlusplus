package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;

public class HealthTextComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.HEALTH_TEXT_ENABLE))
			return;
		
		int freeze = Math.round(100 * ctx.data.percentFrozen());
		int maxHp  = ctx.math.ceil(ctx.data.maxHealth());
		int absorb = ctx.math.ceil(ctx.data.absorption());
		int health = ctx.math.ceil(ctx.data.health());
		
		if(health > maxHp) health = maxHp;
		int y = ctx.y + 1;
		
		int lenfreeze = freeze > 0 && ctx.config.bool(BooleanOption.FROSTBITE_TEXT_ENABLE) ? ctx.renderer.width(", ", freeze, "%") : 0;
		int lenabsorb = ctx.renderer.width(     absorb) + 1;
		int lenplus   = ctx.renderer.width("+", absorb) + 1;
		int lenfull   = ctx.renderer.width(     health) + (absorb == 0 ? 1 : lenplus) + lenfreeze;
		
		int hpcol = ctx.data.isFullyFrozen()
				? ctx.config.hex(IntegerOption.TEXT_COLOR_FROSTBITE)
				: ctx.data.isEffectActive(ctx.data.effects().poison())
						? ctx.config.hex(IntegerOption.TEXT_COLOR_POISON)
						: ctx.data.isEffectActive(ctx.data.effects().wither())
								? ctx.config.hex(IntegerOption.TEXT_COLOR_WITHER)
								: ctx.config.hex(IntegerOption.TEXT_COLOR_HEART);
		
		if(health < maxHp) {
			int lenmaxhp = ctx.renderer.width(maxHp) + (absorb == 0 ? 1 : lenplus) + lenfreeze;
			int lenslash = ctx.renderer.width("/")   + lenmaxhp;
			
			ctx.renderer.text(ctx.poseStack, "/"       , ctx.x - lenslash, y, ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR));
			ctx.renderer.text(ctx.poseStack, "" + maxHp, ctx.x - lenmaxhp, y, hpcol);
			
			lenfull += ctx.renderer.width("/", maxHp);
		}
		
		ctx.renderer.text(ctx.poseStack, "" + health, ctx.x - lenfull, y, hpcol);
		
		if(absorb > 0) {
			ctx.renderer.text(ctx.poseStack, "+"        , ctx.x - lenplus  , y, ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR));
			ctx.renderer.text(ctx.poseStack, "" + absorb, ctx.x - lenabsorb, y, ctx.config.hex(IntegerOption.TEXT_COLOR_ABSORPTION));
		}
		
		if (freeze > 0 && ctx.config.bool(BooleanOption.FROSTBITE_TEXT_ENABLE)) {
			ctx.renderer.text(ctx.poseStack, ", "        , ctx.x - lenfreeze                      , y, ctx.config.hex(IntegerOption.TEXT_COLOR_SEPARATOR));
			ctx.renderer.text(ctx.poseStack, freeze + "%", ctx.x - ctx.renderer.width(freeze, "%"), y, ctx.config.hex(IntegerOption.TEXT_COLOR_FROSTBITE));
		}
	}
}
