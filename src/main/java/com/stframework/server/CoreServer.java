package com.stframework.server;

import com.stframework.server.player.PlayerHandler;

public class CoreServer {

    public static CoreServer instance;

    private ServerWrap serverWrap;
    private PlayerHandler playerHandler;

    public CoreServer(int commandPort) {
        instance = this;
        try {
            serverWrap = new ServerWrap();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return;
        }

        playerHandler = new PlayerHandler();

        CommandServer.start(commandPort);
    }

    public ServerWrap getServerWrap() {
        return serverWrap;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
