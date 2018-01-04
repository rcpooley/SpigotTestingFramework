package com.stframework.server.player;

import com.stframework.server.network.NetworkManager;
import com.stframework.server.network.packet.PacketChatMessage;
import com.stframework.server.network.packet.PacketHandshake;
import com.stframework.server.network.packet.PacketLoginStart;

import java.net.*;

public class FakePlayer {

    public static String HOST = "localhost";
    public static int PORT = 25565;

    private String name;
    private NetworkManager networkManager;

    public FakePlayer(String name) {
        this.name = name;
        connect(HOST, PORT);
    }

    public void connect() {
        this.connect(HOST, PORT);
    }

    public void connect(String address, int port) {
        try {
            networkManager = NetworkManager.connect(InetAddress.getByName(address), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        networkManager.sendPacket(new PacketHandshake(address, port));
        networkManager.sendPacket(new PacketLoginStart(name));
    }

    public void disconnect() {
        networkManager.closeChannel();
    }

    public void sendMessage(String message) {
        networkManager.sendPacket(new PacketChatMessage(message));
    }
}
