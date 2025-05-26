package dev.cheos.armorpointspp.compat;

import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;

public class FabricAPISafeAccess {
	public static void registerClientLoginInitEventListener(BiConsumer<ClientHandshakePacketListenerImpl, Minecraft> listener) {
		ClientLoginConnectionEvents.INIT.register(listener::accept);
	}
}
