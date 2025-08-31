package com.magicai;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MagicAIPlayer {
    private MinecraftServer server;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, String> playerPreferences = new HashMap<>();
    private final Random random = new Random();
    
    // Magic spells and abilities
    private final List<String> magicSpells = Arrays.asList(
        "✨ קסם האור ✨", "🔥 קסם האש 🔥", "❄️ קסם הקרח ❄️", 
        "🌊 קסם המים 🌊", "🌪️ קסם הרוח 🌪️", "💎 קסם האבנים 💎"
    );
    
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
    
    public void processChatMessage(String playerName, String message) {
        String lowerMessage = message.toLowerCase();
        
        // Process Hebrew and English messages
        if (lowerMessage.contains("שלום") || lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
            sendMessageToPlayer(playerName, "✨ שלום " + playerName + "! איך אני יכול לעזור לך היום? ✨");
        }
        else if (lowerMessage.contains("עזרה") || lowerMessage.contains("help")) {
            sendHelpMessage(playerName);
        }
        else if (lowerMessage.contains("קסם") || lowerMessage.contains("spell") || lowerMessage.contains("magic")) {
            castRandomSpell(playerName);
        }
        else if (lowerMessage.contains("פקודה") || lowerMessage.contains("command")) {
            suggestCommands(playerName);
        }
        else if (lowerMessage.contains("בנה") || lowerMessage.contains("build")) {
            suggestBuildingIdeas(playerName);
        }
        else if (lowerMessage.contains("הישרדות") || lowerMessage.contains("survival")) {
            giveSurvivalTips(playerName);
        }
        else if (lowerMessage.contains("תודה") || lowerMessage.contains("thanks")) {
            sendMessageToPlayer(playerName, "🌟 בשמחה! תמיד שמח לעזור! 🌟");
        }
        else {
            // General conversation
            respondToConversation(playerName, message);
        }
    }
    
    private void sendHelpMessage(String playerName) {
        String helpMessage = """
            🌟 **עזרה - Magic AI** 🌟
            
            💬 **צ'אט:**
            • שלום/hello - ברכות
            • עזרה/help - תפריט עזרה זה
            • קסם/spell - קסם אקראי
            • פקודה/command - הצעות לפקודות
            • בנה/build - רעיונות לבנייה
            • הישרדות/survival - טיפים להישרדות
            
            🎮 **פקודות שימושיות:**
            • /gamemode creative - מצב יצירה
            • /gamemode survival - מצב הישרדות
            • /time set day - יום
            • /weather clear - מזג אוויר בהיר
            • /tp @s ~ ~ ~ - טלפורט למיקום נוכחי
            
            ✨ **קסמים מיוחדים:**
            • קסם האור - מאיר את האזור
            • קסם האש - יוצר אש
            • קסם הקרח - יוצר קרח
            • קסם המים - יוצר מים
            • קסם הרוח - יוצר רוח
            • קסם האבנים - יוצר אבנים
            """;
        
        sendMessageToPlayer(playerName, helpMessage);
    }
    
    private void castRandomSpell(String playerName) {
        String spell = magicSpells.get(random.nextInt(magicSpells.size()));
        sendMessageToPlayer(playerName, "🔮 " + spell + " מופעל! 🔮");
        
        // Find player and apply spell effects
        ServerPlayerEntity player = findPlayer(playerName);
        if (player != null) {
            applySpellEffect(player, spell);
        }
    }
    
    private void applySpellEffect(ServerPlayerEntity player, String spell) {
        BlockPos playerPos = player.getBlockPos();
        World world = player.getWorld();
        
        switch (spell) {
            case "✨ קסם האור ✨":
                // Create light around player
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        BlockPos lightPos = playerPos.add(x, 0, z);
                        if (world.getBlockState(lightPos).isAir()) {
                            world.setBlockState(lightPos, Registries.BLOCK.get(new Identifier("minecraft", "torch")).getDefaultState());
                        }
                    }
                }
                sendMessageToPlayer(player.getName().getString(), "💡 האזור מואר בקסם! 💡");
                break;
                
            case "🔥 קסם האש 🔥":
                // Create fire pattern
                for (int i = 0; i < 5; i++) {
                    BlockPos firePos = playerPos.add(random.nextInt(7) - 3, 0, random.nextInt(7) - 3);
                    if (world.getBlockState(firePos).isAir()) {
                        world.setBlockState(firePos, Registries.BLOCK.get(new Identifier("minecraft", "fire")).getDefaultState());
                    }
                }
                sendMessageToPlayer(player.getName().getString(), "🔥 אש קסומה נוצרה! 🔥");
                break;
                
            case "❄️ קסם הקרח ❄️":
                // Create ice path
                for (int i = 0; i < 10; i++) {
                    BlockPos icePos = playerPos.add(i - 5, 0, 0);
                    if (world.getBlockState(icePos).isAir()) {
                        world.setBlockState(icePos, Registries.BLOCK.get(new Identifier("minecraft", "ice")).getDefaultState());
                    }
                }
                sendMessageToPlayer(player.getName().getString(), "❄️ שביל קרח קסום נוצר! ❄️");
                break;
                
            case "🌊 קסם המים 🌊":
                // Create water source
                BlockPos waterPos = playerPos.add(0, 0, 2);
                world.setBlockState(waterPos, Registries.BLOCK.get(new Identifier("minecraft", "water")).getDefaultState());
                sendMessageToPlayer(player.getName().getString(), "🌊 מקור מים קסום נוצר! 🌊");
                break;
                
            case "🌪️ קסם הרוח 🌪️":
                // Create air blocks (wind effect)
                for (int i = 0; i < 8; i++) {
                    BlockPos windPos = playerPos.add(random.nextInt(5) - 2, random.nextInt(3), random.nextInt(5) - 2);
                    if (world.getBlockState(windPos).isAir()) {
                        world.setBlockState(windPos, Registries.BLOCK.get(new Identifier("minecraft", "glass")).getDefaultState());
                    }
                }
                sendMessageToPlayer(player.getName().getString(), "🌪️ רוח קסומה נוצרה! 🌪️");
                break;
                
            case "💎 קסם האבנים 💎":
                // Create diamond ore
                BlockPos diamondPos = playerPos.add(0, -1, 0);
                world.setBlockState(diamondPos, Registries.BLOCK.get(new Identifier("minecraft", "diamond_ore")).getDefaultState());
                sendMessageToPlayer(player.getName().getString(), "💎 אבן יהלום קסומה נוצרה! 💎");
                break;
        }
    }
    
    private void suggestCommands(String playerName) {
        String commands = """
            🎮 **פקודות מומלצות:**
            
            🏠 **בנייה:**
            • /fill ~-5 ~-1 ~-5 ~5 ~5 ~5 stone - יצירת קוביה
            • /clone ~ ~ ~ ~5 ~5 ~5 ~10 ~ ~ - העתקת מבנה
            
            🌍 **עולם:**
            • /weather rain - גשם
            • /time set night - לילה
            • /difficulty peaceful - קושי שקט
            
            👥 **שחקנים:**
            • /tp @a @s - כל השחקנים אליך
            • /gamemode creative @a - כולם למצב יצירה
            
            🎯 **מטרות:**
            • /give @s diamond_sword - חרב יהלום
            • /effect @s speed 30 2 - מהירות מוגברת
            """;
        
        sendMessageToPlayer(playerName, commands);
    }
    
    private void suggestBuildingIdeas(String playerName) {
        String[] buildingIdeas = {
            "🏰 טירה קסומה עם מגדלים גבוהים",
            "🌳 בית עץ על העצים",
            "🏺 מקדש עתיק עם עמודים",
            "🌊 בית תת-מימי עם זכוכית",
            "🏔️ בית הרים עם גשר תלוי",
            "🎭 ארמון עם גנים מפוארים",
            "⚡ מגדל קסם עם אפקטים מיוחדים",
            "🌙 בית צף בשמיים"
        };
        
        String idea = buildingIdeas[random.nextInt(buildingIdeas.length)];
        sendMessageToPlayer(playerName, "🏗️ **רעיון לבנייה:** " + idea + " 🏗️");
    }
    
    private void giveSurvivalTips(String playerName) {
        String[] survivalTips = {
            "🌙 תמיד שמור על אוכל ומקלות להדלקת מדורה",
            "⛏️ חפש פחם בלילה כדי ליצור לפידים",
            "🏠 בנה בית פשוט בלילה הראשון",
            "💧 שמור על מים קרוב לבית שלך",
            "🌾 התחל לגדל חיטה מוקדם במשחק",
            "🛡️ צור שריון ברזל לפני שאתה יוצא למסעות ארוכים",
            "🗺️ צור מפה כדי לא ללכת לאיבוד",
            "🔥 תמיד שמור על מקלות להדלקת מדורה"
        };
        
        String tip = survivalTips[random.nextInt(survivalTips.length)];
        sendMessageToPlayer(playerName, "💡 **טיפ הישרדות:** " + tip + " 💡");
    }
    
    private void respondToConversation(String playerName, String message) {
        String[] responses = {
            "🤔 מעניין! תספר לי עוד על זה",
            "🌟 זה נשמע מרתק!",
            "💭 מה דעתך על זה?",
            "🎮 בוא נחקור את זה יחד במשחק!",
            "✨ רעיון נהדר!",
            "🚀 אני אוהב את הדרך שאתה חושב!",
            "🌈 זה מזכיר לי משהו קסום...",
            "🎯 בוא ננסה את זה במשחק!"
        };
        
        String response = responses[random.nextInt(responses.length)];
        sendMessageToPlayer(playerName, response);
    }
    
    private ServerPlayerEntity findPlayer(String playerName) {
        if (server != null) {
            return server.getPlayerManager().getPlayer(playerName);
        }
        return null;
    }
    
    public void sendMessageToPlayer(String playerName, String message) {
        if (server != null) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player != null) {
                player.sendMessage(Text.of("✨ Magic AI: " + message), false);
            }
        }
    }
    
    public void sendMessageToAll(String message) {
        if (server != null) {
            server.getPlayerManager().broadcast(Text.of("✨ Magic AI: " + message), false);
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
} 