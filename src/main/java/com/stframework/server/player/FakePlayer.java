package com.stframework.server.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Session;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.biome.Biome;

import java.io.File;
import java.lang.reflect.Field;
import java.net.*;
import java.util.List;
import java.util.UUID;

public class FakePlayer {

    private static class InitInfo {
        public GameConfiguration gameConfiguration;
        public Session session;
        public InitInfo(GameConfiguration gc, Session s) {
            this.gameConfiguration = gc;
            this.session = s;
        }
    }

    public static String HOST = "localhost";
    public static int PORT = 25565;

    private MyMinecraft mc;
    private NetworkManager networkManager;

    public FakePlayer(String name) {
        InitInfo info = getGameConfig(name);
        mc = new MyMinecraft(info.gameConfiguration);
        mc.setSession(info.session);
        connect(HOST, PORT);
    }

    public Session getSession() {
        return mc.getSession();
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

        networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, mc, null));
        networkManager.sendPacket(new C00Handshake(address, port, EnumConnectionState.LOGIN));
        networkManager.sendPacket(new CPacketLoginStart(mc.getSession().getProfile()));
    }

    public void disconnect() {
        networkManager.closeChannel(new TextComponentString("Quitting"));
    }

    public void sendMessage(String message) {
        networkManager.sendPacket(new CPacketChatMessage(message));
    }

    private InitInfo getGameConfig(String name) {
        String[] p_main_0_ = new String[]{"--version", "1.12.2", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.12.2", "--userProperties", "{}"};
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(Integer.valueOf(25565));
        OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().<File>ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().<File>ofType(File.class);
        OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().<File>ofType(File.class);
        OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").<Integer>ofType(Integer.class);
        OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo(name);
        OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg().defaultsTo(UUID.randomUUID().toString());
        OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(Integer.valueOf(854));
        OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(Integer.valueOf(480));
        OptionSpec<String> optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> optionspec19 = optionparser.accepts("versionType").withRequiredArg().defaultsTo("release");
        OptionSpec<String> optionspec20 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List<String> list = optionset.valuesOf(optionspec20);

        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        String s = (String) optionset.valueOf(optionspec5);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, ((Integer) optionset.valueOf(optionspec6)).intValue()));
            } catch (Exception var48) {
                ;
            }
        }

        final String s1 = (String) optionset.valueOf(optionspec7);
        final String s2 = (String) optionset.valueOf(optionspec8);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(s1) && isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }

        int i = ((Integer) optionset.valueOf(optionspec13)).intValue();
        int j = ((Integer) optionset.valueOf(optionspec14)).intValue();
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = (String) optionset.valueOf(optionspec12);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
        PropertyMap propertymap = (PropertyMap) JsonUtils.gsonDeserialize(gson, (String) optionset.valueOf(optionspec15), PropertyMap.class);
        PropertyMap propertymap1 = (PropertyMap) JsonUtils.gsonDeserialize(gson, (String) optionset.valueOf(optionspec16), PropertyMap.class);
        String s4 = (String) optionset.valueOf(optionspec19);
        File file1 = (File) optionset.valueOf(optionspec2);
        File file2 = optionset.has(optionspec3) ? (File) optionset.valueOf(optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has(optionspec4) ? (File) optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
        String s5 = optionset.has(optionspec10) ? (String) optionspec10.value(optionset) : (String) optionspec9.value(optionset);
        s5 = UUID.randomUUID().toString();
        String s6 = optionset.has(optionspec17) ? (String) optionspec17.value(optionset) : null;
        String s7 = (String) optionset.valueOf(optionspec);
        Integer integer = (Integer) optionset.valueOf(optionspec1);
        Session session = new Session(optionspec9.value(optionset), s5, optionspec11.value(optionset), optionspec18.value(optionset));
        return new InitInfo(
                new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s6), new GameConfiguration.GameInformation(flag2, s3, s4), new GameConfiguration.ServerInformation(s7, integer.intValue())),
                session);
    }

    /**
     * Returns whether a string is either null or empty.
     */
    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
