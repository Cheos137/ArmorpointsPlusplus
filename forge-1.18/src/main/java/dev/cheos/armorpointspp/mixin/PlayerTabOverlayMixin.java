package dev.cheos.armorpointspp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.cheos.armorpointspp.Overlays;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.render.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
	@Shadow
	private long visibilityId;
	
	@Inject(method = "renderTablistScore", at = @At("HEAD"), cancellable = true)
	private void renderTablistScore(Objective obj, int y, String name, int x, int maxX, PlayerInfo player, PoseStack poseStack, CallbackInfo ci) {
		RenderContext ctx = Overlays.ctx(poseStack, x, y);
		ctx.putProperty("score", obj.getScoreboard().getOrCreatePlayerScore(name, obj).getScore());
		ctx.putProperty("rendertype.hearts", obj.getRenderType() == ObjectiveCriteria.RenderType.HEARTS);
		ctx.putProperty("visibilityid", this.visibilityId);
		ctx.putProperty("player.rendervisibilityid"   , player.getRenderVisibilityId());
		ctx.putProperty("player.lasthealthtime"       , player.getLastHealthTime());
		ctx.putProperty("player.healthblinktime"      , player.getHealthBlinkTime());
		ctx.putProperty("player.lasthealth"           , player.getLastHealth());
		ctx.putProperty("player.displayhealth"        , player.getDisplayHealth());
		ctx.putProperty("player.setlasthealthtime"    , player::setLastHealthTime);
		ctx.putProperty("player.sethealthblinktime"   , player::setHealthBlinkTime);
		ctx.putProperty("player.setrendervisibilityid", player::setRenderVisibilityId);
		ctx.putProperty("player.setlasthealth"        , player::setLastHealth);
		ctx.putProperty("player.setdisplayhealth"     , player::setDisplayHealth);
		ctx.putProperty("chatformatting.yellow", ChatFormatting.YELLOW.toString());
		ctx.putProperty("maxX", maxX);
		
		if (Components.TABLIST_SCORE.render(ctx))
			ci.cancel();
	}
}
