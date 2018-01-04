package com.stframework.server.network.packet;

import com.stframework.server.network.GameProfile;
import com.stframework.server.network.Packet;
import com.stframework.server.network.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class SPacketLoginSuccess implements Packet {

    private GameProfile profile;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        String s = buf.readString(36);
        String s1 = buf.readString(16);
        UUID uuid = UUID.fromString(s);
        profile = new GameProfile(uuid, s1);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }
}
