package com.stframework.server.network;

import java.io.IOException;

public interface Packet {
    static Packet getPacket(int id) {
        return null;
    }

    static int getPacketID(Packet packet) {
        return 0;
    }

    void readPacketData(PacketBuffer buf) throws IOException;
    void writePacketData(PacketBuffer buf) throws IOException;
    void processPacket();
}
