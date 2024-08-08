package com.paveli1.xenoras.listeners;

import com.paveli1.xenoras.Xenoras;
import com.paveli1.xenoras.apis.UpdatesGithub;
import com.paveli1.xenoras.structures.ServerStatus;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

@Log4j2
public class SystemListener implements  ClientReceiveMessageEvents.Game {
    public static int msgCounter = 0;
    public static boolean notified = false;

    public SystemListener() {
        ClientReceiveMessageEvents.GAME.register(this);
    }

    @Override
    public void onReceiveGameMessage(Text message, boolean overlay) {
        if (Xenoras.server == null) return;
        if (!Xenoras.OFFICIAL_HOST.equals(Xenoras.server.address()) && !Objects.requireNonNullElse(Xenoras.NGROK_HOST, "null").equals(Xenoras.server.address())) return;
        if (Xenoras.CONFIG.useUpdateNotificator() && !notified) {
            msgCounter += 1;
            if (msgCounter >= 10) {
                Runnable check = () -> {
                    UpdatesGithub.Update update = UpdatesGithub.getLastUpdate();
                    if (update.need) {
                        Xenoras.xsend("§2Update %v is available! You can use command §4#update install§2 or download it here: §b§n%r".replace("%v", update.version).replace("%r", UpdatesGithub.GithubApi.pageReleases));
                    }
                };
                Thread thread = new Thread(check);
                thread.start();
                notified = true;
            }
        }
        if (!Xenoras.CONFIG.useAutoLogin()) return;
        if (Xenoras.server.isLogin()) return;
        //if (!loaded()) return;


        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        String username = client.player.getName().getString();

        Xenoras.LOGGER.info("trying to login %p...".replace("%p", username));
        if (Xenoras.CONFIG.eAccount.Username().equals(username) && Xenoras.CONFIG.eAccount.useEAccount()) {
            client.player.networkHandler.sendChatCommand("login "+Xenoras.CONFIG.eAccount.Password());
        }
        else {
            client.player.networkHandler.sendChatCommand("login "+Xenoras.CONFIG.Password());
        }
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