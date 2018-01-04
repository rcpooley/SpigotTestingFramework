package com.stframework.server.network.packet;

import com.stframework.server.network.Packet;
import com.stframework.server.network.PacketBuffer;

import java.io.IOException;

public class SPacketEnableCompression implements Packet {

    private int compressionThreshold;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        compressionThreshold = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(compressionThreshold);
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }
}
