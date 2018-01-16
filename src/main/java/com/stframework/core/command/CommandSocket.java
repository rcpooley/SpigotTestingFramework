package com.stframework.core.command;

import com.stframework.core.Util;
import com.stframework.core.command.packet.CommandPacket;

import javax.annotation.Nullable;
import java.io.*;
import java.net.Socket;
import java.util.regex.Pattern;

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
                CommandPacket packet = CommandPacket.parse(Util.decode(line));
                logPacket(packet, false);
                packetHandler.handlePacket(this, packet);
            }
        } catch (IOException e) {
            String s = e.getMessage();
            if (!(s.equals("Connection reset") || s.equals("Socket closed"))) {
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(CommandPacket packet) {
        logPacket(packet, true);
        writer.println(Util.encode(packet.toString()));
    }

    private void logPacket(@Nullable CommandPacket packet, boolean outgoing) {
        String build = outgoing ? "Sending packet" : "Received packet";
        if (packet == null) {
            System.out.println(build + " null");
            return;
        }
        String[] spl = packet.getClass().getName().split(Pattern.quote("."));
        String packetName = spl[spl.length - 1];
        build += " " + packetName + "(" + packet.toString() + ")";
        System.out.println(build);
    }

    public Socket getSocket() {
        return socket;
    }
}
