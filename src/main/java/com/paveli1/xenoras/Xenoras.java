package com.paveli1.xenoras;

import com.paveli1.xenoras.apis.NgrokApi;
import com.paveli1.xenoras.apis.XenorasConfig;
import com.paveli1.xenoras.apis.ConfigModel;
import com.paveli1.xenoras.listeners.SystemListener;
import net.fabricmc.api.ModInitializer;
import com.paveli1.xenoras.structures.ServerStatus;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;


public class Xenoras implements ModInitializer {
	public static final String MOD_ID = "xenoras";
	public static final String CHAT_CODE = "§l§8[§cXenoras§8]§f§r ";
	public static final String OFFICIAL_HOST = "93.158.194.211";
	public static final String NGROK_HOST = NgrokApi.getEndpoint();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final XenorasConfig CONFIG = XenorasConfig.createAndLoad();
	public static ServerStatus server;

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
			if (message.contains("#usedip")) {
				xsend("connected via "+server.address());
				return false;
			}
			if (message.contains("#updatecheck")) {
				xsend("no updates now.");
				return false;
			}

			return true;
		});

		ServerInfo mainbridge = null;
		if (CONFIG.Bridge().equals(ConfigModel.BridgeChoices.OFFICIAL)) {
        	mainbridge = new ServerInfo(CONFIG.ServerName(), OFFICIAL_HOST, false);
		} else {
            mainbridge = new ServerInfo(CONFIG.ServerName(), Objects.requireNonNullElse(NGROK_HOST, OFFICIAL_HOST), false);
		}

        ServerList servers = new ServerList(MinecraftClient.getInstance());
		boolean isXenorasAdded = false;
		servers.loadFile();
		for (int i = 0; i < servers.size(); i++) {
			if (servers.get(i).name.equals("Xenoras")) {
				if (!servers.get(i).equals(mainbridge)) {
					servers.get(i).name = mainbridge.name;
					servers.get(i).address = mainbridge.address;
				}
				isXenorasAdded = true;
				LOGGER.info("xenoras was found in the server list.");
			}
		}
		if (!isXenorasAdded) {
			servers.add(mainbridge, false);
		}
		servers.saveFile();

		try {
			update();
		} catch (Exception e) {
			LOGGER.info("update check failed.");
		}

		new SystemListener();

		LOGGER.info("xenoras mod loaded :)");
	}

	private static String readStringFromURL(String requestURL) throws IOException {
		try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8))
		{
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}

	private static void update() {
		String updatecmd = "";
        try {
            updatecmd = readStringFromURL("https://raw.githubusercontent.com/xenorasmc/usermod/main/update.sh");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows & !updatecmd.isEmpty()) {
			LOGGER.info("update string: "+updatecmd);
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("powershell.exe", "-c", updatecmd);
            try {
                Process process = processBuilder.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
	}

	public static void xsend(String msg) {
		if (MinecraftClient.getInstance().player != null) {
			if (!OFFICIAL_HOST.equals(server.address()) && !Objects.requireNonNullElse(NGROK_HOST, "null").equals(server.address())) {
				LOGGER.info("xenoras commands not work on "+server.address());
			} else {
				MinecraftClient.getInstance().player.sendMessage(Text.of(CHAT_CODE+msg));
			}
		}
	}
}

