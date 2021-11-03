package dev.cheos.armorpointspp.core.render;

import java.util.Random;

import dev.cheos.armorpointspp.core.IRenderComponent;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.EnumOption;

public class HealthComponent implements IRenderComponent {
	private final Random random = new Random();
	private int lastHealth, displayHealth, lastGuiTicks;
	private long healthBlinkTime, lastHealthTime;
	private int[] lastHeartY = new int[10];
	
	@Override
	public void render(RenderContext ctx) {
		if (!ctx.shouldRender() || !ctx.config.bool(BooleanOption.HEALTH_ENABLE))
			return;
		
		ctx.renderer.setupAppp();
		boolean frozen   = ctx.data.isFullyFrozen();
		boolean hardcore = ctx.data.isHardcore();
		int health       = ctx.math.ceil(ctx.data.health());
		int heartStack   = Math.min((health - 1) / 20, 10);
		
		this.lastGuiTicks = ctx.data.guiTicks();
		
		boolean blink = !frozen && this.healthBlinkTime > this.lastGuiTicks && (this.healthBlinkTime - this.lastGuiTicks) / 3L % 2L == 1L;
		int regen = ctx.data.isEffectActive(ctx.data.effects().regeneration())
				? this.lastGuiTicks % 25 // in vanilla: % (maxHealth + 5), here: more than 20 + 5 does not make sense as bars are stacked
				: -1;
		int margin = 36;
		
		if (hardcore) margin += 108;
		if (ctx.data.isEffectActive(ctx.data.effects().poison())) margin += 36;
		else if (ctx.data.isEffectActive(ctx.data.effects().wither())) margin += 72;
		
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
			
			ctx.renderer.blit(ctx.poseStack, heartX, heartY, blink ? 18 : 0, 9, 9, 9); // draw background
			if (heartValue >= health && heartStack > 0) ctx.renderer.blit(ctx.poseStack, heartX, heartY, margin, heartStack * 9, 9, 9); // part. draw row below
			
			if (blink) {
				if      (heartValue <  this.displayHealth) ctx.renderer.blit(ctx.poseStack, heartX, heartY, margin + 18, 9 + heartStack * 9, 9, 9); // full
				else if (heartValue == this.displayHealth) ctx.renderer.blit(ctx.poseStack, heartX, heartY, margin + 27, 9 + heartStack * 9, 9, 9); // half
			}
			
			if      (heartValue <  health) ctx.renderer.blit(ctx.poseStack, heartX, heartY, margin    , 9 + heartStack * 9, 9, 9); // full
			else if (heartValue == health) ctx.renderer.blit(ctx.poseStack, heartX, heartY, margin + 9, 9 + heartStack * 9, 9, 9); // half
			
			if (frozen)
				switch (ctx.config.enm(EnumOption.FROSTBITE_STYLE)) {
					case FULL:
						if (heartValue < health)
							ctx.renderer.blit(ctx.poseStack, heartX, heartY, hardcore ? 18 : 0, 117, 9, 9);
						else if (heartValue == health)
							ctx.renderer.blit(ctx.poseStack, heartX, heartY, hardcore ? 27 : 9, 117, 5, 9); // only half width frostbite
						break;
					case OVERLAY:
						if (heartValue < health)
							ctx.renderer.blit(ctx.poseStack, heartX, heartY, hardcore ? 18 : 0, 108, 9, 9);
						else if (heartValue == health)
							ctx.renderer.blit(ctx.poseStack, heartX, heartY, hardcore ? 27 : 9, 108, 5, 9); // only half width frostbite
						break;
					case ICON: // fallthrough, icon is default
					default:
						ctx.poseStack.pushPose();
						ctx.poseStack.scale(0.5F, 0.5F, 1);
						ctx.renderer.blit(ctx.poseStack, 2 * heartX + 8, 2 * heartY + 1, 36, 108, 9, 9);
						ctx.poseStack.popPose();
						break;
				}
		}
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
