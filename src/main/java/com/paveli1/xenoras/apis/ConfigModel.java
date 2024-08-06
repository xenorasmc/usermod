package com.paveli1.xenoras.apis;

import com.paveli1.xenoras.Xenoras;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;
import net.minecraft.text.Text;

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
}