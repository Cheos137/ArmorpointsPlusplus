package dev.cheos.armorpointspp.mixin;

import java.util.function.Consumer;

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
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
	@Shadow
	private Gui gui;
	
	@Inject(method = "renderTablistScore", at = @At("HEAD"), cancellable = true)
	private void renderTablistScore(Objective obj, int y, String name, int x, int maxX, PlayerInfo player, PoseStack poseStack, CallbackInfo ci) {
		RenderContext ctx = Overlays.ctx(poseStack, x, y);
		ctx.putProperty("score", obj.getScoreboard().getOrCreatePlayerScore(name, obj).getScore());
		ctx.putProperty("rendertype.hearts", obj.getRenderType() == ObjectiveCriteria.RenderType.HEARTS);
		ctx.putProperty("visibilityid", 0L);
		ctx.putProperty("player.rendervisibilityid"   , 0L);
		ctx.putProperty("player.lasthealthtime"       , player.getLastHealthTime());
		ctx.putProperty("player.healthblinktime"      , player.getHealthBlinkTime());
		ctx.putProperty("player.lasthealth"           , player.getLastHealth());
		ctx.putProperty("player.displayhealth"        , player.getDisplayHealth());
		ctx.putProperty("player.setlasthealthtime"    , nil());
		ctx.putProperty("player.sethealthblinktime"   , nil());
		ctx.putProperty("player.setrendervisibilityid", nil());
		ctx.putProperty("player.setlasthealth"        , nil());
		ctx.putProperty("player.setdisplayhealth"     , nil());
		ctx.putProperty("chatformatting.yellow", ChatFormatting.YELLOW.toString());
		ctx.putProperty("maxX", maxX);
		
		if (Components.TABLIST_SCORE.render(ctx))
			ci.cancel();
	}
	
	private static <T> Consumer<T> nil() {
		return v -> { };
	}
}
