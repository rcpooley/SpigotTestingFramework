package com.stframework.server.player;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;

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
            networkManager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(address), port, true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        networkManager.sendPacket(new C00Handshake(address, port, EnumConnectionState.LOGIN));
        networkManager.sendPacket(new CPacketLoginStart(name));
    }

    public void disconnect() {
        networkManager.closeChannel();
    }

    public void sendMessage(String message) {
        networkManager.sendPacket(new PacketChatMessage(message));
    }
}
