package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.FloatOption;
import dev.cheos.armorpointspp.core.adapter.IItemStack;

public class ProtectionComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor())
			return;
		
		int protection = 0;
		for (IItemStack stack : ctx.data.armorSlots()) { // adds 0 for empty stacks
			ctx.ench.getLevel(ctx.data.enchantments().protection(), stack);
			protection += ctx.ench.getLevel(ctx.data.enchantments().protection(), stack);
			protection += ctx.ench.getLevel(ctx.data.enchantments().blastProtection(), stack);
			protection += ctx.ench.getLevel(ctx.data.enchantments().fireProtection(), stack);
			protection += ctx.ench.getLevel(ctx.data.enchantments().projectileProtection(), stack);
		}
		
		protection = ctx.math.ceil(protection * ctx.config.dec(FloatOption.PROTECTION_VALUE));
		protection = ctx.math.clamp(protection, 0, 10);
		if (protection <= 0) return;
		
		for (int i = 0; i < 10 && i < protection; i++)
			ctx.renderer.blit(ctx.poseStack, ctx.x + 8 * i, ctx.y, 9, 9, 9, 9);
	}
}