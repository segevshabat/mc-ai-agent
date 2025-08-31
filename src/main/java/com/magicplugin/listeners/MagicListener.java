package com.magicplugin.listeners;

import com.magicplugin.managers.MagicManager;
import com.magicplugin.managers.WandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MagicListener implements Listener {
    private final MagicManager magicManager;
    private final WandManager wandManager;
    
    public MagicListener(MagicManager magicManager, WandManager wandManager) {
        this.magicManager = magicManager;
        this.wandManager = wandManager;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("§5Welcome to the Magic Server! §6Use /spells to see available spells.");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        magicManager.savePlayerData(player);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (wandManager.isWand(item)) {
            // Handle wand interactions
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    // Cast active spell
                    handleWandLeftClick(player, item);
                    event.setCancelled(true);
                    break;
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    // Open wand inventory or cycle spells
                    handleWandRightClick(player, item);
                    event.setCancelled(true);
                    break;
            }
        }
    }
    
    private void handleWandLeftClick(Player player, ItemStack wand) {
        // Cast the active spell (placeholder implementation)
        player.sendMessage("§6Wand activated! (Left click)");
        // TODO: Implement spell casting logic based on wand configuration
    }
    
    private void handleWandRightClick(Player player, ItemStack wand) {
        // Open wand inventory or cycle through spells (placeholder implementation)
        player.sendMessage("§5Wand menu opened! (Right click)");
        // TODO: Implement wand inventory/spell cycling logic
    }
} 