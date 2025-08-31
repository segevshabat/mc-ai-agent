package com.magicplugin.commands;

import com.magicplugin.MagicPlugin;
import com.magicplugin.objects.Spell;
import com.magicplugin.managers.SpellManager;
import com.magicplugin.managers.MagicManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class CastCommand implements CommandExecutor {
    
    private final MagicPlugin plugin;
    private final SpellManager spellManager;
    private final MagicManager magicManager;
    
    public CastCommand(MagicPlugin plugin) {
        this.plugin = plugin;
        this.spellManager = plugin.getSpellManager();
        this.magicManager = plugin.getMagicManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("magic.commands.cast")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String spellName = args[0];
        
        // Check if spell exists
        if (!spellManager.spellExists(spellName)) {
            player.sendMessage(ChatColor.RED + "Spell '" + spellName + "' not found!");
            player.sendMessage(ChatColor.YELLOW + "Use /spells to see available spells.");
            return true;
        }
        
        // Check if player has permission to cast this spell
        if (!player.hasPermission("magic.cast." + spellName)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to cast '" + spellName + "'!");
            return true;
        }
        
        // Parse parameters
        Map<String, String> parameters = parseParameters(args);
        
        // Cast the spell
        castSpell(player, spellName, parameters);
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Cast Command Help ===");
        player.sendMessage(ChatColor.YELLOW + "Usage: /cast <spell> [param value] [param value]...");
        player.sendMessage(ChatColor.WHITE + "Examples:");
        player.sendMessage(ChatColor.GREEN + "• /cast boom size 20 fire true");
        player.sendMessage(ChatColor.GREEN + "• /cast arrow fire true count 50");
        player.sendMessage(ChatColor.GREEN + "• /cast familiar type chicken count 30");
        player.sendMessage(ChatColor.GREEN + "• /cast paint material gold_block");
        player.sendMessage(ChatColor.GREEN + "• /cast recurse material water");
        player.sendMessage(ChatColor.WHITE + "Use /spells to see available spells and their parameters.");
    }
    
    private Map<String, String> parseParameters(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        
        // Skip first argument (spell name)
        for (int i = 1; i < args.length - 1; i += 2) {
            String paramName = args[i];
            String paramValue = args[i + 1];
            
            // Handle special cases
            if (paramValue.equalsIgnoreCase("true") || paramValue.equalsIgnoreCase("false")) {
                parameters.put(paramName, paramValue.toLowerCase());
            } else {
                // Try to parse as number
                try {
                    // Check if it's an integer
                    Integer.parseInt(paramValue);
                    parameters.put(paramName, paramValue);
                } catch (NumberFormatException e) {
                    try {
                        // Check if it's a double
                        Double.parseDouble(paramValue);
                        parameters.put(paramName, paramValue);
                    } catch (NumberFormatException e2) {
                        // Treat as string
                        parameters.put(paramName, paramValue);
                    }
                }
            }
        }
        
        return parameters;
    }
    
    private void castSpell(Player player, String spellName, Map<String, String> parameters) {
        try {
            // Get spell object
            Spell spell = spellManager.getSpell(spellName);
            
            if (spell == null) {
                player.sendMessage(ChatColor.RED + "Error: Could not load spell '" + spellName + "'!");
                return;
            }
            
            // Validate parameters
            if (!validateSpellParameters(spell, parameters)) {
                player.sendMessage(ChatColor.RED + "Invalid parameters for spell '" + spellName + "'!");
                player.sendMessage(ChatColor.YELLOW + "Use /spells to see valid parameters.");
                return;
            }
            
            // Check cooldown
            if (spell.isOnCooldown(player)) {
                long remaining = spell.getRemainingCooldown(player);
                player.sendMessage(ChatColor.RED + "Spell is on cooldown! Wait " + remaining + " seconds.");
                return;
            }
            
            // Check costs
            if (!spell.canAfford(player)) {
                player.sendMessage(ChatColor.RED + "You can't afford to cast this spell!");
                return;
            }
            
            // Cast the spell
            boolean success = magicManager.castSpell(player, spell, parameters);
            
            if (success) {
                // Apply costs
                spell.applyCosts(player);
                
                // Set cooldown
                spell.setCooldown(player);
                
                // Send success message
                String paramText = parameters.isEmpty() ? "" : " with parameters: " + formatParameters(parameters);
                player.sendMessage(ChatColor.GREEN + "Successfully cast '" + spellName + "'" + paramText + "!");
                
                // Show spell effects
                showSpellEffects(player, spell, parameters);
                
            } else {
                player.sendMessage(ChatColor.RED + "Failed to cast spell '" + spellName + "'!");
                player.sendMessage(ChatColor.YELLOW + "Check if you have the required materials and permissions.");
            }
            
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error casting spell: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean validateSpellParameters(Spell spell, Map<String, String> parameters) {
        // Get valid parameters for this spell
        Map<String, String> validParams = spell.getValidParameters();
        
        // Check if all provided parameters are valid
        for (String paramName : parameters.keySet()) {
            if (!validParams.containsKey(paramName)) {
                return false;
            }
        }
        
        return true;
    }
    
    private String formatParameters(Map<String, String> parameters) {
        if (parameters.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        
        return sb.toString();
    }
    
    private void showSpellEffects(Player player, Spell spell, Map<String, String> parameters) {
        // Show visual effects based on spell type and parameters
        String spellType = spell.getType();
        
        switch (spellType.toLowerCase()) {
            case "boom":
            case "explosion":
                showExplosionEffects(player, parameters);
                break;
            case "arrow":
            case "projectile":
                showProjectileEffects(player, parameters);
                break;
            case "construction":
            case "build":
                showConstructionEffects(player, parameters);
                break;
            case "heal":
            case "buff":
                showBuffEffects(player, parameters);
                break;
            default:
                showGenericEffects(player, spell, parameters);
                break;
        }
    }
    
    private void showExplosionEffects(Player player, Map<String, String> parameters) {
        String size = parameters.getOrDefault("size", "1");
        String fire = parameters.getOrDefault("fire", "false");
        
        player.sendMessage(ChatColor.ORANGE + "💥 Explosion size: " + size);
        if (fire.equals("true")) {
            player.sendMessage(ChatColor.RED + "🔥 Fire enabled!");
        }
    }
    
    private void showProjectileEffects(Player player, Map<String, String> parameters) {
        String count = parameters.getOrDefault("count", "1");
        String fire = parameters.getOrDefault("fire", "false");
        
        player.sendMessage(ChatColor.BLUE + "🏹 Projectile count: " + count);
        if (fire.equals("true")) {
            player.sendMessage(ChatColor.RED + "🔥 Fire arrows!");
        }
    }
    
    private void showConstructionEffects(Player player, Map<String, String> parameters) {
        String material = parameters.getOrDefault("material", "stone");
        String size = parameters.getOrDefault("size", "1");
        
        player.sendMessage(ChatColor.GREEN + "🏗️ Building with: " + material);
        player.sendMessage(ChatColor.GREEN + "📏 Size: " + size);
    }
    
    private void showBuffEffects(Player player, Map<String, String> parameters) {
        String duration = parameters.getOrDefault("duration", "30");
        String level = parameters.getOrDefault("level", "1");
        
        player.sendMessage(ChatColor.LIGHT_PURPLE + "✨ Buff duration: " + duration + "s");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "⭐ Level: " + level);
    }
    
    private void showGenericEffects(Player player, Spell spell, Map<String, String> parameters) {
        player.sendMessage(ChatColor.AQUA + "✨ Casting " + spell.getName());
        
        if (!parameters.isEmpty()) {
            player.sendMessage(ChatColor.AQUA + "🔧 Parameters applied: " + formatParameters(parameters));
        }
    }
} 