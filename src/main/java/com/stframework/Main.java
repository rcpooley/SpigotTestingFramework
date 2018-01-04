package com.stframework;

import com.stframework.client.ClientPlayer;
import com.stframework.client.CommandClient;
import com.stframework.core.Util;
import com.stframework.server.CoreServer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final int DEFAULT_PORT = 42912;

    public static void main(String[] args) {
        try {
            Object obj = EnumConnectionState.PLAY.getPacket(EnumPacketDirection.CLIENTBOUND, 24);
            System.out.println(obj);
            //return;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Map<String, String> realArgs = new HashMap<>();
        realArgs.put("-p", DEFAULT_PORT + "");

        String lastKey = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                lastKey = args[i];
                realArgs.put(lastKey, "");
            }
            else {
                String cur = realArgs.get(lastKey);
                if (cur.length() == 0) {
                    realArgs.put(lastKey, args[i]);
                } else {
                    realArgs.put(lastKey, cur + " " + args[i]);
                }
            }
        }

        if (realArgs.containsKey("-cmd")) {
            String cmd = realArgs.get("-cmd");
            CommandClient client = new CommandClient();
            client.executeCommand(cmd);
            System.out.println("Executed \"" + cmd + "\" successfully");
            client.disconnect();
            return;
        }

        if (realArgs.containsKey("-test")) {
            CommandClient client = new CommandClient();
            ClientPlayer player = client.createPlayer("bobby");
            Util.sleep(500);
            player.sendMessage("hey there!");
            Util.sleep(1000);
            player.disconnect();
            client.disconnect();
            return;
        }

        if (realArgs.containsKey("-test2")) {
            CommandClient client = new CommandClient();
            client.createPlayer("bobby");
            Util.sleep(5000);
            client.cleanup();
            return;
        }

        int port = DEFAULT_PORT;
        try {
            port = Integer.parseInt(realArgs.get("-p"));
        } catch (Exception e) {
            System.out.println(realArgs.get("-p") + " is an invalid port, defaulting to " + port);
        }

        new CoreServer(port);
    }
}
