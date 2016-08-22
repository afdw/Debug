package com.anton.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class Overlay {
    public static Overlay instance = new Overlay();

    private boolean isButtonEnabled(int i) {
        if(i == 0 || i == 1 || i == 2 || i == 3) {
            EnumDaylightPhase daylightPhase = EnumDaylightPhase.values()[i];
            return WorldSavedDataDebug.get(Minecraft.getMinecraft().theWorld).enabledDaylightPhases.get(daylightPhase.ordinal());
        } else if(i == 4) {
            return WorldSavedDataDebug.get(Minecraft.getMinecraft().theWorld).enabledRain;
        } else if(i == 5) {
            return WorldSavedDataDebug.get(Minecraft.getMinecraft().theWorld).enabledCreeperExplosions;
        }
        return false;
    }

    private String getButtonAction(int i) {
        return isButtonEnabled(i) ? "disable" : "enable";
    }

    public void draw(GuiContainer guiContainer, int mouseX, int mouseY) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("debug", "textures/gui/overlay.png"));
        for(int i = 0; i < 6; i++) {
            int textureX = 0;
            if(mouseX >= i * 16 && mouseY >= 0 && mouseX < i * 16 + 16 && mouseY < 16) {
                textureX = 32;
            } else {
                textureX = 16;
            }
            if(!isButtonEnabled(i)) {
                textureX = 0;
            }
            guiContainer.drawTexturedModalRect(i * 16, 0, textureX, 0, 16, 16);
            guiContainer.drawTexturedModalRect(i * 16, 0, i * 16, 16, 16, 16);
        }
    }

    public void mouse(GuiContainer guiContainer) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int mouseX = Mouse.getX() / scaledResolution.getScaleFactor();
        int mouseY = scaledResolution.getScaledHeight() - Mouse.getY() / scaledResolution.getScaleFactor();
        for(int i = 0; i < 6; i++) {
            if(mouseX >= i * 16 && mouseY >= 0 && mouseX < i * 16 + 16 && mouseY < 16) {
                String command = "";
                if(Mouse.isButtonDown(0)) {
                    if(isButtonEnabled(i)) {
                        if(i == 0 || i == 1 || i == 2 || i == 3) {
                            EnumDaylightPhase daylightPhase = EnumDaylightPhase.values()[i];
                            int time = (daylightPhase.start + daylightPhase.end) / 2;
                            command = "/time set " + time;
                        } else if(i == 4) {
                            command = "/toggledownfall";
                        } else if(i == 5) {
                            command = "/summon PrimedTnt";
                        }
                    }
                } else if(Mouse.isButtonDown(1)) {
                    if(i == 0 || i == 1 || i == 2 || i == 3) {
                        EnumDaylightPhase daylightPhase = EnumDaylightPhase.values()[i];
                        command = "/debug enabledDaylightPhases " + getButtonAction(i) + " " + daylightPhase.name().toLowerCase();
                    } else if(i == 4) {
                        command = "/debug enabledRain " + getButtonAction(i);
                    } else if(i == 5) {
                        command = "/debug enabledCreeperExplosions " + getButtonAction(i);
                    }
                }
                if(!command.isEmpty()) {
                    Minecraft.getMinecraft().thePlayer.connection.sendPacket(new CPacketChatMessage(command));
                }
            }
        }
    }
}
