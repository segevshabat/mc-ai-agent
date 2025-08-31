package com.magicai;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MagicAIMixin {
    
    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onPlayerDisconnect(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        String playerName = player.getName().getString();
        
        // Send goodbye message when player disconnects
        if (MagicAIMod.getAIPlayer() != null) {
            MagicAIMod.getAIPlayer().sendMessageToAll("👋 " + playerName + " התנתק מהשרת. להתראות! 👋");
        }
    }
    
    @Inject(method = "onSpawn", at = @At("HEAD"))
    private void onPlayerSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        String playerName = player.getName().getString();
        
        // Send welcome message when player joins
        if (MagicAIMod.getAIPlayer() != null) {
            MagicAIMod.getAIPlayer().sendMessageToPlayer(playerName, "🌟 ברוך הבא לשרת! אני Magic AI, שחקן AI קסום שיעזור לך במשחק! 🌟");
            MagicAIMod.getAIPlayer().sendMessageToPlayer(playerName, "💬 כתוב 'עזרה' או 'help' כדי לראות מה אני יכול לעשות");
        }
    }
} 