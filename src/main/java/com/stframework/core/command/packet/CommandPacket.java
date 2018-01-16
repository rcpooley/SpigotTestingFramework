package com.stframework.core.command.packet;

import com.stframework.core.Util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class CommandPacket {

    private static Map<String, Class<? extends CommandPacket>> typeMap;

    static {
        typeMap = new HashMap<>();
        typeMap.put("newPlayer", PacketNewPlayer.class);
        typeMap.put("outputWatch", PacketOutputWatch.class);
        typeMap.put("playerAction", PacketPlayerAction.class);
        typeMap.put("serverCommand", PacketServerCommand.class);
        typeMap.put("singleAction", PacketSingleAction.class);
    }

    private static String getType(Class<? extends CommandPacket> clazz) {
        for (String type : typeMap.keySet()) {
            if (typeMap.get(type) == clazz) return type;
        }
        return null;
    }

    public static CommandPacket parse(String str) {
        String[] spl = str.split(":", -1);
        if (spl.length != 2) {
            throw new RuntimeException("Invalid packet received: " + str);
        }
        Class<? extends CommandPacket> clazz = typeMap.get(spl[0]);
        if (clazz == null) {
            throw new RuntimeException("Unrecognized command packet type for packet: " + str);
        }
        try {
            return (CommandPacket) clazz.getMethod("parse", String.class).invoke(null, Util.decode(spl[1]));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract String encode();

    public String toString() {
        return getType(getClass()) + ":" + Util.encode(encode());
    }
}
