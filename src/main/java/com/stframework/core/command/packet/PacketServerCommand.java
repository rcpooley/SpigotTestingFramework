package com.stframework.core.command.packet;

public class PacketServerCommand extends CommandPacket {

    public static PacketServerCommand parse(String str) {
        return new PacketServerCommand(str);
    }

    private String command;

    public PacketServerCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String encode() {
        return command;
    }
}
