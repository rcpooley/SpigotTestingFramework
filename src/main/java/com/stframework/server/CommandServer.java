package com.stframework.server;

import com.stframework.core.command.CommandSocket;
import com.stframework.core.Util;
import com.stframework.core.command.PacketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandServer {

    private static PacketHandler packetHandler;

    public static void start(int port) {
        packetHandler = new ServerPacketHandler();
        new Thread(() -> threadedStart(port)).start();
    }

    private static void threadedStart(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                try {
                    Socket socket = server.accept();
                    new CommandSocket(socket, packetHandler);
                } catch (Exception e) {
                    e.printStackTrace();
                    Util.sleep(1000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
