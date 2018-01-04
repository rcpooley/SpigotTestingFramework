package com.stframework.server.network.packet;

import com.stframework.server.network.Packet;
import com.stframework.server.network.PacketBuffer;

import java.io.IOException;

public class SPacketJoinGame implements Packet {

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        buf.readInt();
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
        buf.readUnsignedByte();
        buf.readString(16);
        buf.readBoolean();

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }
}
