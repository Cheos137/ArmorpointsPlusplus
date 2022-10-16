package dev.cheos.armorpointspp.core.render;

import java.awt.Color;
import java.util.function.Consumer;

import dev.cheos.armorpointspp.core.*;
import dev.cheos.armorpointspp.core.RenderableText.Alignment;
import dev.cheos.armorpointspp.core.adapter.IConfig.BooleanOption;
import dev.cheos.armorpointspp.core.adapter.IConfig.IntegerOption;
import dev.cheos.armorpointspp.core.texture.ITextureSheet;
import dev.cheos.armorpointspp.core.texture.ITextureSheet.HeartStyle;

public class TablistScoreComponent implements IRenderComponent {
	@Override
	public boolean render(RenderContext ctx) { // no profiling for tab lists in vanilla? interesting
		if (!ctx.config.bool(BooleanOption.TABLIST_HEALTH_ENABLE))
			return false;
		
		// WHY IS THERE NOT WAY TO DIFFERENTIATE BETWEEN MAX HEALTH AND ABSORPTION IN THIS?!?!?
		
		ctx.profiler.push("tablist.score");
		int score = ctx.property("score");
		
		if (ctx.<Boolean>property("rendertype.hearts")) {
			ITextureSheet tex = tex(ctx).bind(ctx);
			long millis = ctx.data.millis();
			long visibilityId = ctx.property("visibilityid");
			long renderVisibilityId = ctx.property("player.rendervisibilityid");
			long lastHealthTime = ctx.property("player.lasthealthtime");
			long healthBlinkTime = ctx.property("player.healthblinktime");
			int lastHealth = ctx.property("player.lasthealth");
			int displayHealth = ctx.property("player.displayhealth");
			Consumer<Long> setLastHealthTime = ctx.property("player.setlasthealthtime");
			Consumer<Long> setHealthBlinkTime = ctx.property("player.sethealthblinktime");
			Consumer<Long> setRenderVisibilityId = ctx.property("player.setrendervisibilityid");
			Consumer<Integer> setLastHealth = ctx.property("player.setlasthealth");
			Consumer<Integer> setDisplayHealth = ctx.property("player.setdisplayhealth");
			
			if (visibilityId == renderVisibilityId) {
				if (score < lastHealth) {
					setLastHealthTime.accept(millis);
					setHealthBlinkTime.accept(ctx.data.guiTicks() + 20L);
				} else if (score > lastHealth) {
					setLastHealthTime.accept(millis);
					setHealthBlinkTime.accept(ctx.data.guiTicks() + 10L);
				}
			}
			
			if (millis - lastHealthTime > 1000L || visibilityId != renderVisibilityId) {
				setDisplayHealth.accept(score);
				setLastHealthTime.accept(millis);
			}
			
			setRenderVisibilityId.accept(visibilityId);
			setLastHealth.accept(score);
			
			int health = Math.max(score, displayHealth);
			int heartCount = ctx.math.ceil(Math.max(score, displayHealth) / 2F);
			int heartStack = Math.min((health - 1) / ctx.config.num(IntegerOption.TABLIST_MAX_HEART_COUNT) / 2, 10);
			boolean blink = healthBlinkTime > ctx.data.guiTicks() && (healthBlinkTime - ctx.data.guiTicks()) / 3L % 2L == 1L;
			
			if (heartCount > 0) {
				if (heartCount <= (heartStack + 1) * ctx.config.num(IntegerOption.TABLIST_MAX_HEART_COUNT)) {
					int max = ctx.config.bool(BooleanOption.TABLIST_HEALTH_ALWAYS_MAX)
							? ctx.config.num(IntegerOption.TABLIST_MAX_HEART_COUNT)
							: Math.min(ctx.config.num(IntegerOption.TABLIST_MAX_HEART_COUNT), heartCount);
					
					for (int i = 0; i < max; i++) { // for reference, see class HealthComponent
						int heartX = ctx.x + i * 8;
						int heartValue = i * 2 + heartStack * ctx.config.num(IntegerOption.TABLIST_MAX_HEART_COUNT) * 2 + 1;
						
						tex.drawHeartBG(ctx, heartX, ctx.y, blink);
						if (heartStack > 0 && heartValue >=        health) tex.drawHeart(ctx, heartX, ctx.y, heartStack - 1, false                  , blink, false, HeartStyle.NORMAL);
						if (blink          && heartValue <= displayHealth) tex.drawHeart(ctx, heartX, ctx.y, heartStack, heartValue == displayHealth,  true, false, HeartStyle.NORMAL);
						if (                  heartValue <=        health) tex.drawHeart(ctx, heartX, ctx.y, heartStack, heartValue ==        health, false, false, HeartStyle.NORMAL);
					}
				} else {
					String healthText = String.valueOf(score);
					if (ctx.<Integer>property("maxX") - ctx.renderer.width(healthText, "hp") >= ctx.x)
						healthText += "hp";
					if (ctx.config.bool(BooleanOption.DISABLE_EASTEREGGS))
						ctx.renderer.text(ctx.poseStack, healthText, (ctx.<Integer>property("maxX") + ctx.x - ctx.renderer.width(healthText)) / 2, ctx.y, 0x00ff00);
					else {
						millis /= 40;
						RenderableText text = new RenderableText("")
							.withAlignment(Alignment.CENTER)
							.withShadow(ctx.config.bool(BooleanOption.TEXT_SHADOW));
						
						for (String chr : healthText.split(""))
							text.append(new RenderableText(chr).withColor(Color.HSBtoRGB(((millis -= 5) % 360) / 360F, 1, 1)));
						text.render(ctx.poseStack, ctx.renderer, (ctx.<Integer>property("maxX") + ctx.x) / 2, ctx.y);
					}
				}
			}
		} else {
			String text = ctx.<String>property("chatformatting.yellow") + score;
			ctx.renderer.text(ctx.poseStack, text, ctx.<Integer>property("maxX") - ctx.renderer.width(text), ctx.y, 0xffffff);
		}
		return popReturn(ctx, true);
	}
}
