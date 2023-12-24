package dev.cheos.armorpointspp.mixin;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.cheos.armorpointspp.Overlays;
import dev.cheos.armorpointspp.core.RenderContext;
import dev.cheos.armorpointspp.core.render.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
	@Shadow
	private Map<UUID, PlayerTabOverlay.HealthState> healthStates;
	@Shadow
	private Gui gui;
	
	@Inject(method = "renderTablistScore", at = @At("HEAD"), cancellable = true)
	private void renderTablistScore(Objective obj, int y, PlayerTabOverlay.ScoreDisplayEntry entry, int x, int maxX, UUID player, GuiGraphics graphics, CallbackInfo ci) {
		int score = entry.score();
		PlayerTabOverlay.HealthState state = this.healthStates.computeIfAbsent(player, k -> new PlayerTabOverlay.HealthState(score));
		HealthStateMixin extState = (HealthStateMixin) state;
		state.update(score, this.gui.getGuiTicks());
		
		RenderContext ctx = Overlays.ctx(graphics, x, y);
		ctx.putProperty("score", score);
		ctx.putProperty("rendertype.hearts", obj.getRenderType() == ObjectiveCriteria.RenderType.HEARTS);
		ctx.putProperty("visibilityid", 0L);
		ctx.putProperty("player.rendervisibilityid"   , 0L);
		ctx.putProperty("player.lasthealthtime"       , extState.getLastUpdateTick());
		ctx.putProperty("player.healthblinktime"      , extState.getBlinkUntilTick());
		ctx.putProperty("player.lasthealth"           , extState.getLastValue());
		ctx.putProperty("player.displayhealth"        , state.displayedValue());
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
