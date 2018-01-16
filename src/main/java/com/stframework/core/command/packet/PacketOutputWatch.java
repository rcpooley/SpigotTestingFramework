package com.stframework.core.command.packet;

public class PacketOutputWatch extends CommandPacket {

    public static PacketOutputWatch parse(String str) {
        return new PacketOutputWatch(str);
    }

    private String output;

    public PacketOutputWatch(String output) {
        this.output = output;
    }

    @Override
    public String encode() {
        return output;
    }

    public String getOutput() {
        return output;
    }
}
