package dev.cheos.armorpointspp.core.render;

import java.util.Random;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.HeartStyle;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.OverlaySprite;

public class HealthComponent implements IRenderComponent {
	private final Random random = new Random();
	private int lastHealth, displayHealth, lastGuiTicks;
	private long healthBlinkTime, lastHealthTime;
	private int[] lastHeartY = new int[10];
	
	@Override
	public boolean render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.HEALTH_ENABLE))
			return false;
		
		ctx.profiler.push("health");
		ITextureSheet tex = tex(ctx).bind(ctx);
		boolean frozen   = ctx.data.isFullyFrozen();
		boolean hardcore = ctx.data.isHardcore() || ctx.data.isEffectActive("spectrum:divinity");
		int health       = ctx.math.ceil(ctx.data.health());
		int heartStack   = Math.min((health - 1) / 20, 10);
		
		this.lastGuiTicks = ctx.data.guiTicks();
		
		boolean blink = !frozen && this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		int regen = ctx.data.isEffectActive(ctx.data.effects().regeneration())
				? this.lastGuiTicks % 25
				: -1;
		HeartStyle style = ctx.data.isEffectActive(ctx.data.effects().poison())
				? HeartStyle.POISON
				: ctx.data.isEffectActive(ctx.data.effects().wither())
					? HeartStyle.WITHER
					: HeartStyle.NORMAL;
		
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
		
		for (int i = 9; i >= 0; i--) {
			int heartX = ctx.x + i * 8;
			int heartY = ctx.y;
			int heartValue = i * 2 + heartStack * 20 + 1;
			
			if (health <= 4) heartY += this.random.nextInt(2);
			if (i == regen && !frozen) heartY -= 2;
			
			this.lastHeartY[i] = heartY;
			
			tex.drawHeartBG(ctx, heartX, heartY, blink); // draw background
			if (heartValue >= health && heartStack > 0)    tex.drawHeart(ctx, heartX, heartY, heartStack - 1, false, blink, hardcore, style); // draw heart row below
			if (blink && heartValue <= this.displayHealth) tex.drawHeart(ctx, heartX, heartY, heartStack, heartValue == this.displayHealth, true , hardcore, style);
			if (         heartValue <=             health) tex.drawHeart(ctx, heartX, heartY, heartStack, heartValue ==             health, false, hardcore, style);
			
			if (frozen)
				switch (ctx.config.enm(EnumOption.FROSTBITE_STYLE)) {
					case FULL:
						if (heartValue <= health)
							tex.drawOverlay(ctx, heartX, heartY, heartValue == health, hardcore, OverlaySprite.FROSTBITE_FULL);
						break;
					case OVERLAY:
						if (heartValue <= health)
							tex.drawOverlay(ctx, heartX, heartY, heartValue == health, hardcore, OverlaySprite.FROSTBITE);
						break;
					case ICON: // fallthrough, icon is default
					default:
						if (heartValue <  health)
							tex.drawOverlay(ctx, heartX, heartY, false, hardcore, OverlaySprite.FROSTBITE_ICON);
						break;
				}
		}
		return popReturn(ctx, true);
	}
	
	int[] lastHeartY() {
		return this.lastHeartY;
	}
	
	int lastGuiTicks() {
		return this.lastGuiTicks;
	}
	
	long healthBlinkTime() {
		return this.healthBlinkTime;
	}
}
