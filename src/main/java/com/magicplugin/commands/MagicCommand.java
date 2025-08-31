package com.magicplugin.commands;

import com.magicplugin.MagicPlugin;
import com.magicplugin.managers.MagicManager;
import com.magicplugin.managers.WandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;

public class MagicCommand implements CommandExecutor {
    
    private final MagicPlugin plugin;
    private final MagicManager magicManager;
    private final WandManager wandManager;
    
    public MagicCommand(MagicPlugin plugin) {
        this.plugin = plugin;
        this.magicManager = plugin.getMagicManager();
        this.wandManager = plugin.getWandManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("magic.commands.magic")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "load":
                loadConfigurations(sender);
                break;
            case "save":
                saveData(sender);
                break;
            case "commit":
                commitChanges(sender);
                break;
            case "cancel":
                cancelBatches(sender);
                break;
            case "list":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /magic list <wands|automata|maps|tasks> [player]");
                    return true;
                }
                listItems(sender, args[1], args.length > 2 ? args[2] : null);
                break;
            case "clean":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /magic clean <player|ALL>");
                    return true;
                }
                cleanItems(sender, args[1]);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Magic Command Help ===");
        sender.sendMessage(ChatColor.YELLOW + "/magic load " + ChatColor.WHITE + "- Reload config files");
        sender.sendMessage(ChatColor.YELLOW + "/magic save " + ChatColor.WHITE + "- Save player data");
        sender.sendMessage(ChatColor.YELLOW + "/magic commit " + ChatColor.WHITE + "- Commit all changes");
        sender.sendMessage(ChatColor.YELLOW + "/magic cancel " + ChatColor.WHITE + "- Cancel construction batches");
        sender.sendMessage(ChatColor.YELLOW + "/magic list <type> [player] " + ChatColor.WHITE + "- List items");
        sender.sendMessage(ChatColor.YELLOW + "/magic clean <player|ALL> " + ChatColor.WHITE + "- Clear lost items");
    }
    
    private void loadConfigurations(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading Magic configurations...");
        
        try {
            // Reload main config
            plugin.reloadConfig();
            
            // Reload other configurations
            plugin.getConfigManager().loadConfigurations();
            
            // Reload spells and wands
            plugin.getSpellManager().loadSpells();
            plugin.getWandManager().loadWands();
            
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded all configurations!");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading configurations: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void saveData(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Saving Magic data...");
        
        try {
            // Save all wands
            plugin.getWandManager().saveAllWands();
            
            // Save other data
            magicManager.saveData();
            
            sender.sendMessage(ChatColor.GREEN + "Successfully saved all data!");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void commitChanges(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Committing all changes...");
        
        try {
            // Commit all construction batches
            int committed = magicManager.commitAllBatches();
            
            if (committed > 0) {
                sender.sendMessage(ChatColor.GREEN + "Successfully committed " + committed + " construction batches!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "No construction batches to commit.");
            }
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error committing changes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cancelBatches(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Cancelling all construction batches...");
        
        try {
            // Cancel all construction batches
            int cancelled = magicManager.cancelAllBatches();
            
            if (cancelled > 0) {
                sender.sendMessage(ChatColor.GREEN + "Successfully cancelled " + cancelled + " construction batches!");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "No construction batches to cancel.");
            }
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error cancelling batches: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void listItems(CommandSender sender, String type, String playerName) {
        switch (type.toLowerCase()) {
            case "wands":
                listWands(sender, playerName);
                break;
            case "automata":
                listAutomata(sender, playerName);
                break;
            case "maps":
                listMaps(sender, playerName);
                break;
            case "tasks":
                listTasks(sender, playerName);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown list type: " + type);
                sender.sendMessage(ChatColor.YELLOW + "Valid types: wands, automata, maps, tasks");
                break;
        }
    }
    
    private void listWands(CommandSender sender, String playerName) {
        if (playerName != null) {
            // List wands for specific player
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
                return;
            }
            
            List<UUID> playerWands = wandManager.getPlayerWands(targetPlayer.getUniqueId());
            if (playerWands.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "No wands found for player: " + playerName);
                return;
            }
            
            sender.sendMessage(ChatColor.GOLD + "=== Wands for " + playerName + " ===");
            for (UUID wandId : playerWands) {
                sender.sendMessage(ChatColor.GREEN + "• " + wandId.toString());
            }
            
        } else {
            // List all wands
            List<UUID> allWands = wandManager.getAllWandIds();
            if (allWands.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "No wands found on the server.");
                return;
            }
            
            sender.sendMessage(ChatColor.GOLD + "=== All Wands ===");
            sender.sendMessage(ChatColor.GREEN + "Total wands: " + allWands.size());
            
            // Show first 10 wands
            int count = 0;
            for (UUID wandId : allWands) {
                if (count >= 10) {
                    sender.sendMessage(ChatColor.YELLOW + "... and " + (allWands.size() - 10) + " more");
                    break;
                }
                sender.sendMessage(ChatColor.GREEN + "• " + wandId.toString());
                count++;
            }
        }
    }
    
    private void listAutomata(CommandSender sender, String playerName) {
        sender.sendMessage(ChatColor.YELLOW + "Automata listing not implemented yet.");
        // TODO: Implement automata listing
    }
    
    private void listMaps(CommandSender sender, String playerName) {
        sender.sendMessage(ChatColor.YELLOW + "Maps listing not implemented yet.");
        // TODO: Implement maps listing
    }
    
    private void listTasks(CommandSender sender, String playerName) {
        sender.sendMessage(ChatColor.YELLOW + "Tasks listing not implemented yet.");
        // TODO: Implement tasks listing
    }
    
    private void cleanItems(CommandSender sender, String target) {
        if (target.equalsIgnoreCase("ALL")) {
            // Clean all lost wands
            sender.sendMessage(ChatColor.YELLOW + "Cleaning all lost wands...");
            
            try {
                int cleaned = wandManager.cleanAllLostWands();
                sender.sendMessage(ChatColor.GREEN + "Successfully cleaned " + cleaned + " lost wands!");
                
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error cleaning wands: " + e.getMessage());
                e.printStackTrace();
            }
            
        } else {
            // Clean wands for specific player
            Player targetPlayer = Bukkit.getPlayer(target);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + target);
                return;
            }
            
            sender.sendMessage(ChatColor.YELLOW + "Cleaning lost wands for player: " + target);
            
            try {
                int cleaned = wandManager.cleanPlayerLostWands(targetPlayer.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Successfully cleaned " + cleaned + " lost wands for " + target + "!");
                
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Error cleaning wands: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 