package com.stframework.server.network.packet;

import com.stframework.server.network.Packet;
import com.stframework.server.network.PacketBuffer;

import java.io.IOException;

public class PacketHandshake implements Packet {

    private int protocolVersion;
    private String ip;
    private int port;
    private int requestedState;

    public PacketHandshake() {
    }

    public PacketHandshake(String address, int port) {
        this.protocolVersion = 340;
        this.ip = address;
        this.port = port;
        this.requestedState = 2;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.protocolVersion = buf.readVarInt();
        this.ip = buf.readString(255);
        this.port = buf.readUnsignedShort();
        this.requestedState = buf.readVarInt();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.protocolVersion);
        buf.writeString(this.ip);
        buf.writeShort(this.port);
        buf.writeVarInt(requestedState);
    }
}
