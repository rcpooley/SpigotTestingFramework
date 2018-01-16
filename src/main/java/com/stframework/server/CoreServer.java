package com.stframework.server;

import com.stframework.server.player.PlayerHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.Field;

public class CoreServer {

    public static CoreServer instance;

    private ServerWrap serverWrap;
    private PlayerHandler playerHandler;

    public CoreServer(int commandPort) {
        initMC();
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

    private void initMC() {
        try {
            Field field = Bootstrap.class.getDeclaredField("alreadyRegistered");
            field.setAccessible(true);
            field.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        SoundEvent.registerSounds();
        Block.registerBlocks();
        BlockFire.init();
        Potion.registerPotions();
        Enchantment.registerEnchantments();
        Item.registerItems();
        PotionType.registerPotionTypes();
        PotionHelper.init();
        EntityList.init();
        Biome.registerBiomes();
    }

    public ServerWrap getServerWrap() {
        return serverWrap;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
