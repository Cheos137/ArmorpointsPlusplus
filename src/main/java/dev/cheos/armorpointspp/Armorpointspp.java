package dev.cheos.armorpointspp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("armorpointspp")
public class Armorpointspp {
    private static final Logger LOGGER = LogManager.getLogger();

    public Armorpointspp() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::common);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void common(FMLCommonSetupEvent event) {
    	
    }

    private void client(FMLClientSetupEvent event) {
        
    }
}
