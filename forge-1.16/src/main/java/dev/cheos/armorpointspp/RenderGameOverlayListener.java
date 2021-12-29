package dev.cheos.armorpointspp;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Armorpointspp.MODID, value = Dist.CLIENT)
public class RenderGameOverlayListener {
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void handle(RenderGameOverlayEvent event) {
		if (event instanceof ApppRenderGameOverlayEvent) return;
		
		
//		System.out.println(event.getClass().getName());
		
		
		if (event.getType() != ElementType.ARMOR && event.getType() != ElementType.HEALTH) {
			if (!(event.isCancelable() && event.isCanceled()))
				repost(event);
			if (event.getType() != ElementType.TEXT)
				return;
		}
		
		// do magic
		
	}
	
	private static void repost(RenderGameOverlayEvent event) {
		if (event instanceof Chat)
			MinecraftForge.EVENT_BUS.post(new ApppRenderGameOverlayEvent.Chat(event.getMatrixStack(), event, ((Chat) event).getPosX(), ((Chat) event).getPosY()));
		else if (event instanceof Text)
			MinecraftForge.EVENT_BUS.post(new ApppRenderGameOverlayEvent.Text(event.getMatrixStack(), event, ((Text) event).getLeft(), ((Text) event).getRight()));
		else if (event instanceof BossInfo)
			MinecraftForge.EVENT_BUS.post(new ApppRenderGameOverlayEvent.BossInfo(event.getMatrixStack(), event, ((BossInfo) event).getType(), ((BossInfo) event).getBossInfo(), ((BossInfo) event).getX(), ((BossInfo) event).getY(), ((BossInfo) event).getIncrement()));
		else if (event instanceof Pre)
			MinecraftForge.EVENT_BUS.post(new ApppRenderGameOverlayEvent.Pre(event.getMatrixStack(), event, event.getType()));
		else if (event instanceof Post)
			MinecraftForge.EVENT_BUS.post(new ApppRenderGameOverlayEvent.Post(event.getMatrixStack(), event, event.getType()));
	}
}
