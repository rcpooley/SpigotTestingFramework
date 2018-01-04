package com.stframework.server.network.packet;

import com.stframework.server.network.Packet;
import com.stframework.server.network.PacketBuffer;

import java.io.IOException;

public class PacketChatMessage implements Packet {

    private String message;

    public PacketChatMessage() {
    }

    public PacketChatMessage(String message) {
        if (message.length() > 256) message = message.substring(0, 256);
        this.message = message;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        message = buf.readString(256);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(message);
    }

    public String getMessage() {
        return message;
    }
}
