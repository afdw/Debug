package com.anton.debug;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModDebug.MODID, name = ModDebug.NAME, version = ModDebug.VERSION)
public class ModDebug {
    public static final String MODID = "debug";
    public static final String NAME = "Debug";
    public static final String VERSION = "2.0";

    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("debug");

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        NETWORK_WRAPPER.registerMessage(MessageDebugData.Handler.class, MessageDebugData.class, 0, Side.CLIENT);
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
                if(WorldSavedDataDebug.get(world).enabledDaylightPhases.get(phase.ordinal())) {
                    continue;
                }
                if((phase.start < phase.end) ? (world.getWorldTime() >= phase.start && world.getWorldTime() < phase.end) : (world.getWorldTime() >= phase.start || world.getWorldTime() < phase.end)) {
                    world.setWorldTime(phase.end);
                }
            }
            if(world.getWorldInfo().isRaining() && !WorldSavedDataDebug.get(world).enabledRain) {
                world.getWorldInfo().setRaining(false);
            }
        }
    }

    @SubscribeEvent
    public void detonate(ExplosionEvent.Detonate event) {
        if(event.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper && !WorldSavedDataDebug.get(((EntityCreeper) event.getExplosion().getExplosivePlacedBy()).worldObj).enabledCreeperExplosions) {
            event.getExplosion().clearAffectedBlockPositions();
        }
    }

    @SubscribeEvent
    public void drawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        GuiScreen gui = event.getGui();
        if(gui instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) gui;
            Overlay.instance.draw(guiContainer, event.getMouseX(), event.getMouseY());
        }
    }

    @SubscribeEvent
    public void mouseEventPost(GuiScreenEvent.MouseInputEvent.Post event) {
        GuiScreen gui = event.getGui();
        if(gui instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) gui;
            Overlay.instance.mouse(guiContainer);
        }
    }

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) {
            WorldSavedDataDebug.get(event.getWorld()).sendToClients();
        }
    }
}
