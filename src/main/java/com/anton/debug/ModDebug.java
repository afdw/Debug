package com.anton.debug;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ModDebug.MODID, name = ModDebug.NAME, version = ModDebug.VERSION)
public class ModDebug {
    public static final String MODID = "debug";
    public static final String NAME = "Debug";
    public static final String VERSION = "0.1";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDebug());
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        for(WorldServer world : server.worldServers) {
            for(EnumDaylightPhase phase : EnumDaylightPhase.values()) {
                if(DebugWorldSavedData.get(world).enabledDaylightPhases.get(phase.ordinal())) {
                    continue;
                }
                if((phase.start < phase.end) ? (world.getWorldTime() >= phase.start && world.getWorldTime() < phase.end) : (world.getWorldTime() >= phase.start || world.getWorldTime() < phase.end)) {
                    world.setWorldTime(phase.end);
                }
            }
            if(world.getWorldInfo().isRaining() && !DebugWorldSavedData.get(world).enabledRain) {
                world.getWorldInfo().setRaining(false);
            }
        }
    }

    @SubscribeEvent
    public void detonate(ExplosionEvent.Detonate event) {
        if(event.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper && !DebugWorldSavedData.get(((EntityCreeper) event.getExplosion().getExplosivePlacedBy()).worldObj).enabledCreeperExplosions) {
            event.getExplosion().clearAffectedBlockPositions();
        }
    }
}
