package com.paveli1.xenoras;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Xenoras implements ModInitializer {
	public static final String MOD_ID = "xenoras";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	final NgrokClient ngrokClient = new NgrokClient.Builder().build();
	final String ntoken = "1lwT0aYuNgJQmEiLi1dwIMN65wu_2pm1RiDs7GRT1XNozEEiq";

	@Override
	public void onInitialize() {
		ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {

//			if (message.contains("#bridge")) {
//				this.client
//                if (this.client.player != null & this.client.getServer() != null) {
//                	this.client.player.sendMessage(Text.literal("[xbridge] connected to xenoras via " + this.client.getServer().getServerIp() + ":" + String.valueOf(this.client.getServer().getServerPort())));
//				}
//				else {
//					LOGGER.info("#bridge command error");
//				}
//				return false;
//			}
			if (message.contains("#test")) {
				LOGGER.info(System.getProperty("user.dir"));
			}

			return true;
		});

		ngrokClient.setAuthToken(ntoken);
		final List<Tunnel> tunnels = ngrokClient.getTunnels();
		LOGGER.info(tunnels.get(0).getPublicUrl());
		ServerInfo mainbridge = new ServerInfo("Xenoras", "93.158.194.211", false);

		ServerList servers = new ServerList(MinecraftClient.getInstance());
		servers.add(mainbridge, false);
		servers.saveFile();

		LOGGER.info("xenoras bridge loaded :)");
	}
}