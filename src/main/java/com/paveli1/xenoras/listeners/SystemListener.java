package com.paveli1.xenoras.listeners;

import com.paveli1.xenoras.Xenoras;
import com.paveli1.xenoras.structures.ServerStatus;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Log4j2
public class SystemListener implements  ClientReceiveMessageEvents.Game {

    public SystemListener() {
        ClientReceiveMessageEvents.GAME.register(this);
    }

    @Override
    public void onReceiveGameMessage(Text message, boolean overlay) {
        if (!Xenoras.CONFIG.useAutoLogin()) return;
        if (!loaded()) return;
        if (Xenoras.server.isLogin()) return;
        if (!Xenoras.OFFICIAL_HOST.equals(Xenoras.server.address()) && !Objects.requireNonNullElse(Xenoras.NGROK_HOST, "null").equals(Xenoras.server.address())) return;

        MinecraftClient client = MinecraftClient.getInstance();
        String msgStr = message.getString();

        if (client.player == null) return;

        Xenoras.LOGGER.info("trying to login...");
        client.player.networkHandler.sendChatCommand("login "+Xenoras.CONFIG.Password());
        Xenoras.server = new ServerStatus(Xenoras.server.address(), true, 0);

    }

    public boolean loaded() {
        if (Xenoras.server.waitfor() > 0) {
            Xenoras.server = new ServerStatus(Xenoras.server.address(), Xenoras.server.isLogin(), Xenoras.server.waitfor()-1);
            return false;
        }
        return true;
    }
}