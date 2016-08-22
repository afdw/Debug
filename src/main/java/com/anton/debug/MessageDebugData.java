package com.anton.debug;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class MessageDebugData implements IMessage {
    private NBTTagCompound data = new NBTTagCompound();

    public MessageDebugData() {
    }

    public MessageDebugData(World world) {
        WorldSavedDataDebug.get(world).writeToNBT(data);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            data = packetBuffer.readNBTTagCompoundFromBuffer();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeNBTTagCompoundToBuffer(data);
    }

    public static class Handler implements IMessageHandler<MessageDebugData, IMessage> {
        @Override
        public IMessage onMessage(MessageDebugData message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> WorldSavedDataDebug.get(Minecraft.getMinecraft().theWorld).readFromNBT(message.data));
            return null;
        }
    }
}
