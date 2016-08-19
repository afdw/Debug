package com.anton.debug;

import joptsimple.internal.Strings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandDebug extends CommandBase {
    @Override
    public String getCommandName() {
        return "debug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "debug <rule> <action> <argument>";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if(args.length > 1) {
            if(args[0].equals("enabledDaylightPhases")) {
                if(args.length > 2) {
                    if(args[1].equals("enable") || args[1].equals("disable")) {
                        return getListOfStringsMatchingLastWord(args, Stream.of(EnumDaylightPhase.values())
                                .map(Enum::name)
                                .map(String::toLowerCase)
                                .collect(Collectors.toList()));
                    }
                } else {
                    return getListOfStringsMatchingLastWord(args, "list", "enable", "disable");
                }
            }
            if(args[0].equals("enabledRain") || args[0].equals("enabledCreeperExplosions")) {
                if(args.length <= 2) {
                    return getListOfStringsMatchingLastWord(args, "get", "enable", "disable");
                }
            }
        } else {
            return getListOfStringsMatchingLastWord(args, "enabledDaylightPhases", "enabledRain", "enabledCreeperExplosions");
        }
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length >= 1) {
            switch(args[0]) {
                case "enabledDaylightPhases":
                    if(args.length >= 2) {
                        switch(args[1]) {
                            case "list":
                                if(args.length == 2) {
                                    sender.addChatMessage(new TextComponentString(
                                            Strings.join(Stream.of(EnumDaylightPhase.values())
                                                    .filter(enumDaylightPhase ->
                                                            DebugWorldSavedData.get(sender.getEntityWorld()).enabledDaylightPhases.get(enumDaylightPhase.ordinal())
                                                    )
                                                    .map(Enum::name)
                                                    .map(String::toLowerCase)
                                                    .toArray(String[]::new), ", ")
                                    ));
                                } else {
                                    throw new WrongUsageException("Too many arguments");
                                }
                                break;
                            case "enable":
                            case "disable":
                                if(args.length == 3) {
                                    EnumDaylightPhase phase;
                                    switch(args[2]) {
                                        case "day":
                                            phase = EnumDaylightPhase.DAY;
                                            break;
                                        case "evening":
                                            phase = EnumDaylightPhase.EVENING;
                                            break;
                                        case "night":
                                            phase = EnumDaylightPhase.NIGHT;
                                            break;
                                        case "morning":
                                            phase = EnumDaylightPhase.MORNING;
                                            break;
                                        default:
                                            throw new WrongUsageException("Wrong argument");
                                    }
                                    boolean value = false;
                                    if(args[1].equals("enable")) {
                                        value = true;
                                    } else if(args[1].equals("disable")) {
                                        if(DebugWorldSavedData.get(sender.getEntityWorld()).enabledDaylightPhases.stream().toArray().length > 1) {
                                            value = false;
                                        } else {
                                            throw new WrongUsageException("You can not disable last daylight phase");
                                        }
                                    }
                                    DebugWorldSavedData.get(sender.getEntityWorld()).enabledDaylightPhases.set(phase.ordinal(), value);
                                    DebugWorldSavedData.get(sender.getEntityWorld()).setDirty(true);
                                } else if(args.length < 3) {
                                    throw new WrongUsageException("No argument");
                                } else {
                                    throw new WrongUsageException("Too many arguments");
                                }
                                break;
                            default:
                                throw new WrongUsageException("Wrong action");
                        }
                    } else {
                        throw new WrongUsageException("No action");
                    }
                    break;
                case "enabledRain":
                    if(args.length >= 2) {
                        if(args.length > 2) {
                            throw new WrongUsageException("Too many arguments");
                        }
                        switch(args[1]) {
                            case "get":
                                sender.addChatMessage(new TextComponentString(DebugWorldSavedData.get(sender.getEntityWorld()).enabledRain ? "enabled" : "disabled"));
                                break;
                            case "enable":
                                DebugWorldSavedData.get(sender.getEntityWorld()).enabledRain = true;
                                DebugWorldSavedData.get(sender.getEntityWorld()).setDirty(true);
                                break;
                            case "disable":
                                DebugWorldSavedData.get(sender.getEntityWorld()).enabledRain = false;
                                DebugWorldSavedData.get(sender.getEntityWorld()).setDirty(true);
                                break;
                            default:
                                throw new WrongUsageException("Wrong action");
                        }
                    } else {
                        throw new WrongUsageException("No action");
                    }
                    break;
                case "enabledCreeperExplosions":
                    if(args.length >= 2) {
                        if(args.length > 2) {
                            throw new WrongUsageException("Too many arguments");
                        }
                        switch(args[1]) {
                            case "get":
                                sender.addChatMessage(new TextComponentString(DebugWorldSavedData.get(sender.getEntityWorld()).enabledCreeperExplosions ? "enabled" : "disabled"));
                                break;
                            case "enable":
                                DebugWorldSavedData.get(sender.getEntityWorld()).enabledCreeperExplosions = true;
                                DebugWorldSavedData.get(sender.getEntityWorld()).setDirty(true);
                                break;
                            case "disable":
                                DebugWorldSavedData.get(sender.getEntityWorld()).enabledCreeperExplosions = false;
                                DebugWorldSavedData.get(sender.getEntityWorld()).setDirty(true);
                                break;
                            default:
                                throw new WrongUsageException("Wrong action");
                        }
                    } else {
                        throw new WrongUsageException("No action");
                    }
                    break;
                default:
                    throw new WrongUsageException("Wrong rule");
            }
        } else {
            throw new WrongUsageException("No rule");
        }
    }
}
