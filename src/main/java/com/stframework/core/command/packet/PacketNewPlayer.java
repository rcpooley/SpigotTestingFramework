package com.stframework.core.command.packet;

public class PacketNewPlayer extends CommandPacket {

    public static PacketNewPlayer parse(String str) {
        return new PacketNewPlayer(str);
    }

    private String name;

    public PacketNewPlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String encode() {
        return name;
    }
}
