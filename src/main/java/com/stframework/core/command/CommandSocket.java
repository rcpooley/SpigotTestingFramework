package com.stframework.core.command;

import com.stframework.core.Util;
import com.stframework.core.command.packet.CommandPacket;

import java.io.*;
import java.net.Socket;

public class CommandSocket {

    private Socket socket;
    private PrintWriter writer;
    private PacketHandler packetHandler;

    public CommandSocket(Socket socket, PacketHandler packetHandler) {
        this.socket = socket;
        this.packetHandler = packetHandler;
        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this::read).start();
    }

    private void read() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            String line;
            while((line = reader.readLine()) != null) {
                packetHandler.handlePacket(this, CommandPacket.parse(Util.decode(line)));
            }
        } catch (IOException e) {
            String s = e.getMessage();
            if (!(s.equals("Connection reset") || s.equals("Socket closed"))) {
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(CommandPacket packet) {
        writer.println(Util.encode(packet.toString()));
    }

    public Socket getSocket() {
        return socket;
    }
}
