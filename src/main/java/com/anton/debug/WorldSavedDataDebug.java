package com.anton.debug;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.BitSet;

public class WorldSavedDataDebug extends WorldSavedData {
    private static final String DATA_NAME = ModDebug.MODID + "_main";

    public BitSet enabledDaylightPhases = new BitSet(4);
    public boolean enabledRain = true;
    public boolean enabledCreeperExplosions = true;

    public WorldSavedDataDebug() {
        this(DATA_NAME);
        enabledDaylightPhases.set(0, 4, true);
    }

    public WorldSavedDataDebug(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if(nbt.hasKey("enabled_daylight_phases")) {
            enabledDaylightPhases = BitSet.valueOf(nbt.getByteArray("enabled_daylight_phases"));
        }
        if(nbt.hasKey("enabled_rain")) {
            enabledRain = nbt.getBoolean("enabled_rain");
        }
        if(nbt.hasKey("enabled_creeper_explosions")) {
            enabledCreeperExplosions = nbt.getBoolean("enabled_creeper_explosions");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setByteArray("enabled_daylight_phases", enabledDaylightPhases.toByteArray());
        nbt.setBoolean("enabled_rain", enabledRain);
        nbt.setBoolean("enabled_creeper_explosions", enabledCreeperExplosions);
        return nbt;
    }

    public void sendToClients() {
        ModDebug.NETWORK_WRAPPER.sendToAll(new MessageDebugData(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()));
    }

    public static WorldSavedDataDebug get(World world) {
        MapStorage storage = world.getMapStorage();
        WorldSavedDataDebug instance = (WorldSavedDataDebug) storage.getOrLoadData(WorldSavedDataDebug.class, DATA_NAME);

        if(instance == null) {
            instance = new WorldSavedDataDebug();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
