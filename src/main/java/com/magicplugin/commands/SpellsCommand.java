package com.magicplugin.commands;

import com.magicplugin.MagicPlugin;
import com.magicplugin.objects.Spell;
import com.magicplugin.managers.SpellManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;

public class SpellsCommand implements CommandExecutor {
    
    private final MagicPlugin plugin;
    private final SpellManager spellManager;
    
    public SpellsCommand(MagicPlugin plugin) {
        this.plugin = plugin;
        this.spellManager = plugin.getSpellManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("magic.commands.spells")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            // List all spells
            listAllSpells(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "category":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /spells category <category>");
                    return true;
                }
                listSpellsByCategory(sender, args[1]);
                break;
            case "search":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /spells search <query>");
                    return true;
                }
                searchSpells(sender, args[1]);
                break;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /spells info <spell>");
                    return true;
                }
                showSpellInfo(sender, args[1]);
                break;
            case "help":
                sendHelpMessage(sender);
                break;
            default:
                // Try to show info for specific spell
                showSpellInfo(sender, subCommand);
                break;
        }
        
        return true;
    }
    
    private void listAllSpells(CommandSender sender) {
        List<Spell> allSpells = spellManager.getAllSpells();
        
        if (allSpells.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No spells available.");
            return;
        }
        
        // Group spells by category
        Map<String, List<Spell>> spellsByCategory = spellManager.getSpellsByCategory();
        
        sender.sendMessage(ChatColor.GOLD + "=== Available Spells ===");
        sender.sendMessage(ChatColor.GREEN + "Total spells: " + allSpells.size());
        sender.sendMessage("");
        
        for (Map.Entry<String, List<Spell>> entry : spellsByCategory.entrySet()) {
            String category = entry.getKey();
            List<Spell> spells = entry.getValue();
            
            sender.sendMessage(ChatColor.BLUE + "=== " + category + " ===");
            sender.sendMessage(ChatColor.GRAY + "Spells: " + spells.size());
            
            // Show first 5 spells in category
            int count = 0;
            for (Spell spell : spells) {
                if (count >= 5) {
                    if (spells.size() > 5) {
                        sender.sendMessage(ChatColor.YELLOW + "... and " + (spells.size() - 5) + " more");
                    }
                    break;
                }
                
                String permission = sender.hasPermission("magic.cast." + spell.getName()) ? 
                    ChatColor.GREEN + "✓" : ChatColor.RED + "✗";
                
                sender.sendMessage(permission + " " + ChatColor.WHITE + spell.getName() + 
                    ChatColor.GRAY + " - " + spell.getDescription());
                count++;
            }
            
            sender.sendMessage("");
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Use /spells info <spell> for detailed information");
        sender.sendMessage(ChatColor.YELLOW + "Use /spells category <category> to see all spells in a category");
    }
    
    private void listSpellsByCategory(CommandSender sender, String category) {
        List<Spell> spells = spellManager.getSpellsByCategory(category);
        
        if (spells.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Category '" + category + "' not found or empty!");
            sender.sendMessage(ChatColor.YELLOW + "Available categories:");
            List<String> categories = spellManager.getCategories();
            for (String cat : categories) {
                sender.sendMessage(ChatColor.GREEN + "• " + cat);
            }
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== " + category + " Spells ===");
        sender.sendMessage(ChatColor.GREEN + "Total spells: " + spells.size());
        sender.sendMessage("");
        
        for (Spell spell : spells) {
            String permission = sender.hasPermission("magic.cast." + spell.getName()) ? 
                ChatColor.GREEN + "✓" : ChatColor.RED + "✗";
            
            sender.sendMessage(permission + " " + ChatColor.WHITE + spell.getName());
            sender.sendMessage(ChatColor.GRAY + "  " + spell.getDescription());
            
            // Show basic info
            if (spell.getCooldown() > 0) {
                sender.sendMessage(ChatColor.YELLOW + "  Cooldown: " + spell.getCooldown() + "s");
            }
            
            if (spell.getRange() > 0) {
                sender.sendMessage(ChatColor.BLUE + "  Range: " + spell.getRange() + " blocks");
            }
            
            sender.sendMessage("");
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Use /spells info <spell> for detailed information");
    }
    
    private void searchSpells(CommandSender sender, String query) {
        List<Spell> results = spellManager.searchSpells(query);
        
        if (results.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No spells found matching '" + query + "'");
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== Search Results for '" + query + "' ===");
        sender.sendMessage(ChatColor.GREEN + "Found " + results.size() + " spells:");
        sender.sendMessage("");
        
        for (Spell spell : results) {
            String permission = sender.hasPermission("magic.cast." + spell.getName()) ? 
                ChatColor.GREEN + "✓" : ChatColor.RED + "✗";
            
            sender.sendMessage(permission + " " + ChatColor.WHITE + spell.getName() + 
                ChatColor.GRAY + " (" + spell.getCategory() + ")");
            sender.sendMessage(ChatColor.GRAY + "  " + spell.getDescription());
            sender.sendMessage("");
        }
        
        sender.sendMessage(ChatColor.YELLOW + "Use /spells info <spell> for detailed information");
    }
    
    private void showSpellInfo(CommandSender sender, String spellName) {
        if (!spellManager.spellExists(spellName)) {
            sender.sendMessage(ChatColor.RED + "Spell '" + spellName + "' not found!");
            return;
        }
        
        Spell spell = spellManager.getSpell(spellName);
        
        sender.sendMessage(ChatColor.GOLD + "=== Spell Information ===");
        sender.sendMessage(ChatColor.WHITE + "Name: " + ChatColor.GREEN + spell.getName());
        sender.sendMessage(ChatColor.WHITE + "Category: " + ChatColor.BLUE + spell.getCategory());
        sender.sendMessage(ChatColor.WHITE + "Type: " + ChatColor.AQUA + spell.getType());
        sender.sendMessage(ChatColor.WHITE + "Description: " + ChatColor.GRAY + spell.getDescription());
        
        // Show properties
        if (spell.getCooldown() > 0) {
            sender.sendMessage(ChatColor.WHITE + "Cooldown: " + ChatColor.YELLOW + spell.getCooldown() + " seconds");
        }
        
        if (spell.getRange() > 0) {
            sender.sendMessage(ChatColor.WHITE + "Range: " + ChatColor.BLUE + spell.getRange() + " blocks");
        }
        
        if (spell.getManaCost() > 0) {
            sender.sendMessage(ChatColor.WHITE + "Mana Cost: " + ChatColor.LIGHT_PURPLE + spell.getManaCost());
        }
        
        if (spell.getXPLevelCost() > 0) {
            sender.sendMessage(ChatColor.WHITE + "XP Level Cost: " + ChatColor.GREEN + spell.getXPLevelCost());
        }
        
        // Show costs
        Map<String, Integer> costs = spell.getCosts();
        if (!costs.isEmpty()) {
            sender.sendMessage(ChatColor.WHITE + "Costs:");
            for (Map.Entry<String, Integer> entry : costs.entrySet()) {
                sender.sendMessage(ChatColor.GRAY + "  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        // Show parameters
        Map<String, String> parameters = spell.getValidParameters();
        if (!parameters.isEmpty()) {
            sender.sendMessage(ChatColor.WHITE + "Parameters:");
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sender.sendMessage(ChatColor.GRAY + "  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        // Show permission info
        String permission = "magic.cast." + spell.getName();
        boolean hasPermission = sender.hasPermission(permission);
        
        sender.sendMessage(ChatColor.WHITE + "Permission: " + permission);
        sender.sendMessage(ChatColor.WHITE + "Access: " + (hasPermission ? 
            ChatColor.GREEN + "Granted" : ChatColor.RED + "Denied"));
        
        // Show usage examples
        sender.sendMessage(ChatColor.WHITE + "Usage:");
        sender.sendMessage(ChatColor.GREEN + "  /cast " + spell.getName());
        
        if (!parameters.isEmpty()) {
            StringBuilder example = new StringBuilder();
            example.append("  /cast ").append(spell.getName());
            
            for (String param : parameters.keySet()) {
                example.append(" ").append(param).append(" <value>");
            }
            
            sender.sendMessage(ChatColor.GREEN + example.toString());
        }
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Use /cast " + spell.getName() + " to cast this spell");
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Spells Command Help ===");
        sender.sendMessage(ChatColor.YELLOW + "/spells " + ChatColor.WHITE + "- List all available spells");
        sender.sendMessage(ChatColor.YELLOW + "/spells category <category> " + ChatColor.WHITE + "- List spells in category");
        sender.sendMessage(ChatColor.YELLOW + "/spells search <query> " + ChatColor.WHITE + "- Search for spells");
        sender.sendMessage(ChatColor.YELLOW + "/spells info <spell> " + ChatColor.WHITE + "- Show detailed spell information");
        sender.sendMessage(ChatColor.YELLOW + "/spells help " + ChatColor.WHITE + "- Show this help message");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.WHITE + "You can also use /spells <spellname> to get info about a specific spell.");
    }
} 