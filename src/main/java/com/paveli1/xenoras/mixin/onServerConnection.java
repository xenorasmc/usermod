package com.paveli1.xenoras.mixin;

import com.paveli1.xenoras.structures.ServerStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.paveli1.xenoras.Xenoras;

import java.util.regex.Pattern;

@Mixin(ConnectScreen.class)
public class onServerConnection {

    @Inject(method = "connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;Z)V", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void connectInject(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, boolean quickPlay, CallbackInfo ci, ConnectScreen connectScreen) {
        Xenoras.server = new ServerStatus(info.address, false, 0);
        Xenoras.CONFIG.load();
        Xenoras.LOGGER.info("connection event data: "+info.address);
    }

}