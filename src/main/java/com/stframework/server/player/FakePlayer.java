package com.stframework.server.player;

import com.stframework.server.network.NetworkManager;

import java.net.*;

public class FakePlayer {

    public static String HOST = "localhost";
    public static int PORT = 25565;

    private NetworkManager networkManager;

    public FakePlayer(String name) {
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

        networkManager.sendPacket(new C00Handshake(address, port, EnumConnectionState.LOGIN));
        networkManager.sendPacket(new CPacketLoginStart(mc.getSession().getProfile()));
    }

    public void disconnect() {
        networkManager.closeChannel(new TextComponentString("Quitting"));
    }

    public void sendMessage(String message) {
        networkManager.sendPacket(new CPacketChatMessage(message));
    }

    /**
     * Returns whether a string is either null or empty.
     */
    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
