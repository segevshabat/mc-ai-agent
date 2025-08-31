package com.magicplugin.commands;

import com.magicplugin.MagicPlugin;
import com.magicplugin.objects.Wand;
import com.magicplugin.objects.Spell;
import com.magicplugin.managers.WandManager;
import com.magicplugin.managers.SpellManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.meta.ItemMeta;

public class WandPlayerCommand implements CommandExecutor {
    
    private final MagicPlugin plugin;
    private final WandManager wandManager;
    private final SpellManager spellManager;
    
    public WandPlayerCommand(MagicPlugin plugin) {
        this.plugin = plugin;
        this.wandManager = plugin.getWandManager();
        this.spellManager = plugin.getSpellManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("magic.commands.wandp")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 2) {
            sendHelpMessage(sender);
            return true;
        }
        
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return true;
        }
        
        String subCommand = args[1].toLowerCase();
        
        switch (subCommand) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wandp <player> add <spell>");
                    return true;
                }
                addSpellToPlayerWand(sender, targetPlayer, args[2]);
                break;
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wandp <player> remove <spell>");
                    return true;
                }
                removeSpellFromPlayerWand(sender, targetPlayer, args[2]);
                break;
            case "name":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wandp <player> name <name>");
                    return true;
                }
                namePlayerWand(sender, targetPlayer, args[2]);
                break;
            case "list":
                listPlayerWands(sender, targetPlayer);
                break;
            case "fill":
                fillPlayerWand(sender, targetPlayer);
                break;
            case "configure":
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wandp <player> configure <property> <value>");
                    return true;
                }
                configurePlayerWand(sender, targetPlayer, args[2], args[3]);
                break;
            case "add":
                if (args.length < 4 || !args[2].equalsIgnoreCase("material")) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wandp <player> add material <material>");
                    return true;
                }
                addMaterialToPlayerWand(sender, targetPlayer, args[3]);
                break;
            case "remove":
                if (args.length < 4 || !args[2].equalsIgnoreCase("material")) {
                    sender.sendMessage(ChatColor.RED + "Usage: /wandp <player> remove material <material>");
                    return true;
                }
                removeMaterialFromPlayerWand(sender, targetPlayer, args[3]);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Wand Player Command Help ===");
        sender.sendMessage(ChatColor.YELLOW + "Usage: /wandp <player> <command>");
        sender.sendMessage(ChatColor.WHITE + "Commands:");
        sender.sendMessage(ChatColor.GREEN + "• add <spell> " + ChatColor.WHITE + "- Add spell to player's wand");
        sender.sendMessage(ChatColor.GREEN + "• remove <spell> " + ChatColor.WHITE + "- Remove spell from player's wand");
        sender.sendMessage(ChatColor.GREEN + "• name <name> " + ChatColor.WHITE + "- Name player's wand");
        sender.sendMessage(ChatColor.GREEN + "• list " + ChatColor.WHITE + "- List player's wands");
        sender.sendMessage(ChatColor.GREEN + "• fill " + ChatColor.WHITE + "- Fill player's wand with all spells");
        sender.sendMessage(ChatColor.GREEN + "• configure <property> <value> " + ChatColor.WHITE + "- Configure wand property");
        sender.sendMessage(ChatColor.GREEN + "• add material <material> " + ChatColor.WHITE + "- Add material to wand");
        sender.sendMessage(ChatColor.GREEN + "• remove material <material> " + ChatColor.WHITE + "- Remove material from wand");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.WHITE + "Example: /wandp PlayerName add fireball");
    }
    
    private void addSpellToPlayerWand(CommandSender sender, Player targetPlayer, String spellName) {
        if (!sender.hasPermission("magic.commands.wandp.add")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to add spells!");
            return true;
        }
        
        if (!spellManager.spellExists(spellName)) {
            sender.sendMessage(ChatColor.RED + "Spell '" + spellName + "' not found!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Spell spell = spellManager.getSpell(spellName);
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        // Check if player has permission to use this spell
        if (!targetPlayer.hasPermission("magic.cast." + spellName)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " doesn't have permission to use '" + spellName + "'!");
            return true;
        }
        
        wand.addSpell(spell);
        wandManager.saveWand(wand);
        
        // Update item
        updateWandItem(targetPlayer, wand);
        
        sender.sendMessage(ChatColor.GREEN + "Added spell '" + spellName + "' to " + targetPlayer.getName() + "'s wand!");
        targetPlayer.sendMessage(ChatColor.GREEN + "A spell was added to your wand by an administrator!");
    }
    
    private void removeSpellFromPlayerWand(CommandSender sender, Player targetPlayer, String spellName) {
        if (!sender.hasPermission("magic.commands.wandp.remove")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to remove spells!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        if (wand.removeSpell(spellName)) {
            wandManager.saveWand(wand);
            updateWandItem(targetPlayer, wand);
            
            sender.sendMessage(ChatColor.GREEN + "Removed spell '" + spellName + "' from " + targetPlayer.getName() + "'s wand!");
            targetPlayer.sendMessage(ChatColor.YELLOW + "A spell was removed from your wand by an administrator!");
        } else {
            sender.sendMessage(ChatColor.RED + "Spell '" + spellName + "' not found on " + targetPlayer.getName() + "'s wand!");
        }
    }
    
    private void namePlayerWand(CommandSender sender, Player targetPlayer, String name) {
        if (!sender.hasPermission("magic.commands.wandp.name")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to name wands!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        wand.setName(name);
        wandManager.saveWand(wand);
        updateWandItem(targetPlayer, wand);
        
        sender.sendMessage(ChatColor.GREEN + "Named " + targetPlayer.getName() + "'s wand: " + ChatColor.GOLD + name);
        targetPlayer.sendMessage(ChatColor.GREEN + "Your wand was named by an administrator: " + ChatColor.GOLD + name);
    }
    
    private void listPlayerWands(CommandSender sender, Player targetPlayer) {
        if (!sender.hasPermission("magic.commands.wandp.list")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to list wands!");
            return true;
        }
        
        List<UUID> playerWands = wandManager.getPlayerWands(targetPlayer.getUniqueId());
        
        if (playerWands.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + targetPlayer.getName() + " has no wands.");
            return;
        }
        
        sender.sendMessage(ChatColor.GOLD + "=== " + targetPlayer.getName() + "'s Wands ===");
        sender.sendMessage(ChatColor.GREEN + "Total wands: " + playerWands.size());
        
        for (UUID wandId : playerWands) {
            Wand wand = wandManager.getWand(wandId);
            if (wand != null) {
                sender.sendMessage(ChatColor.GREEN + "• " + wand.getName() + " (" + wandId.toString() + ")");
                sender.sendMessage(ChatColor.GRAY + "  Spells: " + wand.getSpells().size());
                sender.sendMessage(ChatColor.GRAY + "  Materials: " + wand.getMaterials().size());
            }
        }
    }
    
    private void fillPlayerWand(CommandSender sender, Player targetPlayer) {
        if (!sender.hasPermission("magic.commands.wandp.fill")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to fill wands!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        // Add all available spells that the player has permission for
        List<Spell> allSpells = spellManager.getAllSpells();
        int added = 0;
        
        for (Spell spell : allSpells) {
            if (targetPlayer.hasPermission("magic.cast." + spell.getName())) {
                wand.addSpell(spell);
                added++;
            }
        }
        
        wandManager.saveWand(wand);
        updateWandItem(targetPlayer, wand);
        
        sender.sendMessage(ChatColor.GREEN + "Filled " + targetPlayer.getName() + "'s wand with " + added + " spells!");
        targetPlayer.sendMessage(ChatColor.GREEN + "Your wand was filled with all available spells by an administrator!");
    }
    
    private void configurePlayerWand(CommandSender sender, Player targetPlayer, String property, String value) {
        if (!sender.hasPermission("magic.commands.wandp.configure")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to configure wands!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        try {
            wand.setProperty(property, value);
            wandManager.saveWand(wand);
            updateWandItem(targetPlayer, wand);
            
            sender.sendMessage(ChatColor.GREEN + "Set wand property '" + property + "' to '" + value + "' for " + targetPlayer.getName());
            targetPlayer.sendMessage(ChatColor.GREEN + "Your wand was configured by an administrator!");
            
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid property or value: " + e.getMessage());
        }
    }
    
    private void addMaterialToPlayerWand(CommandSender sender, Player targetPlayer, String materialName) {
        if (!sender.hasPermission("magic.commands.wandp.add")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to add materials!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid material: " + materialName);
            return true;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        wand.addMaterial(material);
        wandManager.saveWand(wand);
        updateWandItem(targetPlayer, wand);
        
        sender.sendMessage(ChatColor.GREEN + "Added material '" + materialName + "' to " + targetPlayer.getName() + "'s wand!");
        targetPlayer.sendMessage(ChatColor.GREEN + "A material was added to your wand by an administrator!");
    }
    
    private void removeMaterialFromPlayerWand(CommandSender sender, Player targetPlayer, String materialName) {
        if (!sender.hasPermission("magic.commands.wandp.remove")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to remove materials!");
            return true;
        }
        
        // Check if player has a wand in hand
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        if (!isWand(heldItem)) {
            sender.sendMessage(ChatColor.RED + targetPlayer.getName() + " must be holding a wand!");
            return true;
        }
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid material: " + materialName);
            return true;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            sender.sendMessage(ChatColor.RED + "Could not find wand data!");
            return true;
        }
        
        if (wand.removeMaterial(material)) {
            wandManager.saveWand(wand);
            updateWandItem(targetPlayer, wand);
            
            sender.sendMessage(ChatColor.GREEN + "Removed material '" + materialName + "' from " + targetPlayer.getName() + "'s wand!");
            targetPlayer.sendMessage(ChatColor.YELLOW + "A material was removed from your wand by an administrator!");
        } else {
            sender.sendMessage(ChatColor.RED + "Material '" + materialName + "' not found on " + targetPlayer.getName() + "'s wand!");
        }
    }
    
    private boolean isWand(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        
        return meta.getDisplayName().contains("Wand");
    }
    
    private void updateWandItem(Player player, Wand wand) {
        // Update the held item with new wand data
        ItemStack newItem = wand.getItem();
        player.getInventory().setItemInMainHand(newItem);
    }
} 