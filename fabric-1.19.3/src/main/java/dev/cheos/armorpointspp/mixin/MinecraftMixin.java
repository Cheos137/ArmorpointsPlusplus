package dev.cheos.armorpointspp.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.cheos.armorpointspp.ApppGui;
import dev.cheos.armorpointspp.Armorpointspp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.renderer.entity.ItemRenderer;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Mutable
	@Shadow
	private Gui gui;
	@Final
	@Shadow
	private ItemRenderer itemRenderer;
	
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/entity/ItemRenderer;)V", shift = Shift.BY, by = 2))
	public void init(GameConfig gameConfig, CallbackInfo ci) {
		Class<?> guiClass = this.gui.getClass(); // save old gui class
		Armorpointspp.LOGGER.info("Injecting ApppGui instance into Minecraft");
		this.gui = new ApppGui(Minecraft.getInstance(), this.itemRenderer);
		if (guiClass == this.gui.getClass())
			throw new IllegalStateException("Failed to inject Armorpoints++ Renderer - unable to resume");
	}
}
