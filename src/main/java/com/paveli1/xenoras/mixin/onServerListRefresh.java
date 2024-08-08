package com.paveli1.xenoras.mixin;

import com.paveli1.xenoras.Xenoras;
import com.paveli1.xenoras.apis.ConfigModel;
import com.paveli1.xenoras.apis.NgrokApi;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class onServerListRefresh {

    @Inject(method = "refresh()V", at = @At(value = "HEAD"))
    private void refreshCatcher(CallbackInfo ci) {
        Xenoras.CONFIG.load();
        if (Xenoras.CONFIG.Bridge().equals(ConfigModel.BridgeChoices.NGROK)) {
            Xenoras.changeAddress(NgrokApi.getEndpoint());
        }
        else {
            Xenoras.changeAddress(Xenoras.OFFICIAL_HOST);
        }
    }

}