package dev.cheos.armorpointspp.core.render;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;

public class VanillaArmorComponent implements IRenderComponent {
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRenderArmor() || ctx.config.bool(BooleanOption.ARMOR_ENABLE))
			return;
		
		ITextureSheet.vanillaSheet().bind(ctx);
		int x = ctx.x;
		int armor = ctx.data.armor();
		for (int i = 1; armor > 0 && i < 20; i += 2, x += 8)
			ITextureSheet.vanillaSheet().drawArmor(ctx, x, ctx.y, i > armor ? 0 : 1, i == armor);
	}
}
