package dev.cheos.armorpointspp.render;

import java.util.Random;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.HeartStyle;
import net.minecraftforge.client.GuiIngameForge;

public class VanillaHealthComponent implements IRenderComponent {
	private final Random random = new Random();
	private int lastHealth, displayHealth, lastGuiTicks;
	private long healthBlinkTime, lastHealthTime;
	
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRender() || ctx.config.bool(BooleanOption.HEALTH_ENABLE))
			return false;
		
		ctx.profiler.push("health");
		ITextureSheet tex = ITextureSheet.vanillaSheet();
		tex.bind(ctx);
		
		int health = ctx.math.ceil(ctx.data.health());
		boolean blink = this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		
		if (health < this.lastHealth && ctx.data.invulnTime() > 0) {
			this.lastHealthTime = ctx.data.millis();
			this.healthBlinkTime = this.lastGuiTicks + 20;
		} else if (health > this.lastHealth && ctx.data.invulnTime() > 0) {
			this.lastHealthTime = ctx.data.millis();
			this.healthBlinkTime = this.lastGuiTicks + 10;
		}
		
		if (ctx.data.millis() - this.lastHealthTime > 1000L) {
			this.displayHealth = health;
			this.lastHealthTime = ctx.data.millis();
		}
		
		this.lastHealth = health;
		this.random.setSeed(this.lastGuiTicks * 312871L);
		
		float maxHealth = ctx.data.maxHealth();
		float absorb = ctx.data.absorption();
		float absorbRemaining = absorb;
		int healthRows = ctx.math.ceil((maxHealth + absorb) * 0.05F);
		int rowHeight = Math.max(12 - healthRows, 3);
		
		GuiIngameForge.left_height += healthRows * rowHeight;
		if (rowHeight != 10) GuiIngameForge.left_height += 10 - rowHeight;
		
		int regen = -1;
		
		if (ctx.data.isEffectActive(ctx.data.effects().regeneration()))
			regen = this.lastGuiTicks % 25;
		HeartStyle style = ctx.data.isEffectActive(ctx.data.effects().poison())
				? HeartStyle.POISON
				: ctx.data.isEffectActive(ctx.data.effects().wither())
					? HeartStyle.WITHER
					: HeartStyle.NORMAL;
		int margin = style == HeartStyle.NORMAL
				? 16
				: style == HeartStyle.POISON
					? 52
					: 88;
		int top = ctx.data.isHardcore() ? 45 : 0;
		
		for (int i = ctx.math.ceil((maxHealth + absorb) * 0.5F) - 1; i >= 0; i--) {
			int row = ctx.math.ceil((i + 1) * 0.1F) - 1;
			int x = ctx.x + (i % 10) * 8;
			int y = ctx.y - row * rowHeight;
			
			if (health <= 4) y += random.nextInt(2);
			if (i == regen) y -= 2;
			
			tex.drawHeartBG(ctx, x, y, blink);
			if (blink)
				tex.drawHeart(ctx, x, y, 0, i * 2 + 1 == this.displayHealth, blink, ctx.data.isHardcore(), style);
			
			if (absorbRemaining > 0) {
				if (absorbRemaining == absorb && absorb % 2 == 1) {
					ctx.renderer.blit(ctx.poseStack, x, y, margin + 153, top, 9, 9);
					absorbRemaining -= 1;
				} else {
					ctx.renderer.blit(ctx.poseStack, x, y, margin + 144, top, 9, 9);
					absorbRemaining -= 2;
				}
			} else
				tex.drawHeart(ctx, x, y, 0, i * 2 + 1 == health, false, ctx.data.isHardcore(), style);
		}
		
        return popReturn(ctx, true);
	}
}
