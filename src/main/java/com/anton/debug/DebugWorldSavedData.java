package com.anton.debug;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.BitSet;

public class DebugWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = ModDebug.MODID + "_main";

    public BitSet enabledDaylightPhases = new BitSet(4);
    public boolean enabledRain = true;
    public boolean enabledCreeperExplosions = true;

    public DebugWorldSavedData() {
        this(DATA_NAME);
        enabledDaylightPhases.set(0, 4, true);
    }

    public DebugWorldSavedData(String name) {
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

    public static DebugWorldSavedData get(World world) {
        MapStorage storage = world.getMapStorage();
        DebugWorldSavedData instance = (DebugWorldSavedData) storage.getOrLoadData(DebugWorldSavedData.class, DATA_NAME);

        if(instance == null) {
            instance = new DebugWorldSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
