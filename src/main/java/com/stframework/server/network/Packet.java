package com.stframework.server.network;

import java.io.IOException;

public interface Packet {

    void readPacketData(PacketBuffer buf) throws IOException;
    void writePacketData(PacketBuffer buf) throws IOException;
}
