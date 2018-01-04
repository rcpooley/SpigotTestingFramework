package com.stframework.server.network.packet;

import com.stframework.server.network.Packet;
import com.stframework.server.network.PacketBuffer;

import java.io.IOException;

public class PacketLoginStart implements Packet {

    private String name;

    public PacketLoginStart(String name) {
        this.name = name;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.name = buf.readString(16);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(name);
    }

    public String getName() {
        return name;
    }
}
