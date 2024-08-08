package com.paveli1.xenoras.apis;

import com.paveli1.xenoras.Xenoras;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = Xenoras.MOD_ID)
@Config(name = "xenoras-config", wrapperName = "XenorasConfig")
public class ConfigModel {

	@SectionHeader("BridgeSettings")
	public String ServerName = "Xenoras";
	public BridgeChoices Bridge = BridgeChoices.OFFICIAL;

	public enum BridgeChoices {
		OFFICIAL, NGROK;
	}

	@SectionHeader("LoginSettings")
	public boolean useAutoLogin = false;
	public String Password = "";

	@Nest
	public ExtraAccount eAccount = new ExtraAccount();

	public static class ExtraAccount {
		public boolean useEAccount = false;
		public String Username = "";
		public String Password = "";
	}
	@SectionHeader("UpdateSettings")
	public boolean useAutoUpdate = false;
	public boolean useUpdateNotificator = true;

	@SectionHeader("ChatCommands")
	public boolean useChatCommands = true;
	@RegexConstraint("[\\S]")
	public String commandPrefix = "#";

}