package com.magicai;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicAIMod implements ModInitializer {
    public static final String MOD_ID = "magic-ai";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static MagicAIPlayer aiPlayer;
    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Magic AI Mod for Minecraft 1.21.4");
        
        // Initialize AI player
        aiPlayer = new MagicAIPlayer();
        
        // Register server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        
        // Register chat event handler
        ServerMessageEvents.CHAT_MESSAGE.register(this::onChatMessage);
        
        LOGGER.info("Magic AI Mod initialized successfully!");
    }
    
    private void onServerStarted(MinecraftServer minecraftServer) {
        server = minecraftServer;
        aiPlayer.setServer(minecraftServer);
        LOGGER.info("Magic AI connected to server: " + minecraftServer.getServerIp());
        
        // Send welcome message
        aiPlayer.sendMessageToAll("✨ שלום! אני שחקן AI קסום שיעזור לכם במשחק! ✨");
        aiPlayer.sendMessageToAll("💬 תוכלו לדבר איתי בצ'אט ולבקש עזרה בפקודות");
    }
    
    private void onServerStopping(MinecraftServer minecraftServer) {
        if (aiPlayer != null) {
            aiPlayer.sendMessageToAll("👋 להתראות! Magic AI מתנתק מהשרת...");
        }
        server = null;
    }
    
    private boolean onChatMessage(ServerMessageEvents.ChatMessage message) {
        String content = message.getContent().getString();
        String playerName = message.getPlayer().getName().getString();
        
        // Ignore AI's own messages to prevent loops
        if (playerName.equals("MagicAI")) {
            return true;
        }
        
        // Process chat message with AI
        aiPlayer.processChatMessage(playerName, content);
        
        return true;
    }
    
    public static MagicAIPlayer getAIPlayer() {
        return aiPlayer;
    }
    
    public static MinecraftServer getServer() {
        return server;
    }
} 