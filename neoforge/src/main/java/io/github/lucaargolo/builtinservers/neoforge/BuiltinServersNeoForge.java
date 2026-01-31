package io.github.lucaargolo.builtinservers.neoforge;

import io.github.lucaargolo.builtinservers.BuiltinServers;
import net.minecraft.client.MinecraftClient;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

@Mod(BuiltinServers.MODID)
public class BuiltinServersNeoForge {

    public BuiltinServersNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onClientSetup);
    }

    public void onClientSetup(FMLClientSetupEvent event){
        BuiltinServers.initializeClient(FMLPaths.CONFIGDIR.get(), null);

        BuiltinServers.onClientStarted(MinecraftClient.getInstance());
    }
}
