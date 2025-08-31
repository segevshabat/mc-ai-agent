package com.magicplugin.commands;

import com.magicplugin.MagicPlugin;
import com.magicplugin.objects.Wand;
import com.magicplugin.objects.Spell;
import com.magicplugin.managers.WandManager;
import com.magicplugin.managers.SpellManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WandCommand implements CommandExecutor {
    
    private final MagicPlugin plugin;
    private final WandManager wandManager;
    private final SpellManager spellManager;
    
    public WandCommand(MagicPlugin plugin) {
        this.plugin = plugin;
        this.wandManager = plugin.getWandManager();
        this.spellManager = plugin.getSpellManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("magic.commands.wand")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            // /wand - Create empty wand
            createEmptyWand(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listWandTemplates(player);
                break;
            case "add":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand add <spell>");
                    return true;
                }
                addSpellToWand(player, args[1]);
                break;
            case "fill":
                fillWandWithSpells(player);
                break;
            case "remove":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand remove <spell>");
                    return true;
                }
                removeSpellFromWand(player, args[1]);
                break;
            case "configure":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand configure <property> <value>");
                    return true;
                }
                configureWandProperty(player, args[1], args[2]);
                break;
            case "upgrade":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand upgrade <property> <value>");
                    return true;
                }
                upgradeWandProperty(player, args[1], args[2]);
                break;
            case "combine":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand combine <wand>");
                    return true;
                }
                combineWands(player, args[1]);
                break;
            case "add":
                if (args.length < 3 || !args[1].equalsIgnoreCase("material")) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand add material <material>");
                    return true;
                }
                addMaterialToWand(player, args[2]);
                break;
            case "remove":
                if (args.length < 3 || !args[1].equalsIgnoreCase("material")) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand remove material <material>");
                    return true;
                }
                removeMaterialFromWand(player, args[2]);
                break;
            case "name":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /wand name <name>");
                    return true;
                }
                nameWand(player, args[1]);
                break;
            case "describe":
                describeWand(player);
                break;
            case "unlock":
                unlockWand(player);
                break;
            case "enchant":
                if (args.length > 1) {
                    try {
                        int xp = Integer.parseInt(args[1]);
                        enchantWandWithXP(player, xp);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid XP amount: " + args[1]);
                    }
                } else {
                    enchantHeldItem(player);
                }
                break;
            case "unenchant":
                unenchantWand(player);
                break;
            default:
                // Try to create wand from template
                createWandFromTemplate(player, subCommand);
                break;
        }
        
        return true;
    }
    
    private void createEmptyWand(Player player) {
        ItemStack wandItem = new ItemStack(Material.STICK);
        ItemMeta meta = wandItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Magic Wand");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Empty wand");
        lore.add(ChatColor.GRAY + "Right-click to open inventory");
        meta.setLore(lore);
        
        wandItem.setItemMeta(meta);
        
        // Create wand object
        Wand wand = new Wand(player.getUniqueId());
        wand.setItem(wandItem);
        wandManager.saveWand(wand);
        
        player.getInventory().addItem(wandItem);
        player.sendMessage(ChatColor.GREEN + "Created empty magic wand!");
    }
    
    private void createWandFromTemplate(Player player, String templateName) {
        // Check if template exists
        if (!wandManager.templateExists(templateName)) {
            player.sendMessage(ChatColor.RED + "Wand template '" + templateName + "' not found!");
            player.sendMessage(ChatColor.YELLOW + "Use /wand list to see available templates.");
            return true;
        }
        
        Wand template = wandManager.getTemplate(templateName);
        Wand newWand = template.clone();
        newWand.setOwner(player.getUniqueId());
        
        ItemStack wandItem = newWand.getItem();
        player.getInventory().addItem(wandItem);
        
        wandManager.saveWand(newWand);
        player.sendMessage(ChatColor.GREEN + "Created wand from template: " + ChatColor.GOLD + templateName);
    }
    
    private void listWandTemplates(Player player) {
        List<String> templates = wandManager.getAvailableTemplates();
        
        if (templates.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No wand templates available.");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== Available Wand Templates ===");
        for (String template : templates) {
            player.sendMessage(ChatColor.GREEN + "• " + template);
        }
    }
    
    private void addSpellToWand(Player player, String spellName) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        if (!spellManager.spellExists(spellName)) {
            player.sendMessage(ChatColor.RED + "Spell '" + spellName + "' not found!");
            return;
        }
        
        Spell spell = spellManager.getSpell(spellName);
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        wand.addSpell(spell);
        wandManager.saveWand(wand);
        
        // Update item
        updateWandItem(player, wand);
        player.sendMessage(ChatColor.GREEN + "Added spell '" + spellName + "' to your wand!");
    }
    
    private void fillWandWithSpells(Player player) {
        if (!player.hasPermission("magic.wand.fill")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to fill wands!");
            return;
        }
        
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        // Add all available spells
        List<Spell> allSpells = spellManager.getAllSpells();
        for (Spell spell : allSpells) {
            if (player.hasPermission("magic.cast." + spell.getName())) {
                wand.addSpell(spell);
            }
        }
        
        wandManager.saveWand(wand);
        updateWandItem(player, wand);
        player.sendMessage(ChatColor.GREEN + "Filled wand with all available spells!");
    }
    
    private void removeSpellFromWand(Player player, String spellName) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        if (wand.removeSpell(spellName)) {
            wandManager.saveWand(wand);
            updateWandItem(player, wand);
            player.sendMessage(ChatColor.GREEN + "Removed spell '" + spellName + "' from your wand!");
        } else {
            player.sendMessage(ChatColor.RED + "Spell '" + spellName + "' not found on your wand!");
        }
    }
    
    private void configureWandProperty(Player player, String property, String value) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        try {
            wand.setProperty(property, value);
            wandManager.saveWand(wand);
            updateWandItem(player, wand);
            player.sendMessage(ChatColor.GREEN + "Set wand property '" + property + "' to '" + value + "'");
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid property or value: " + e.getMessage());
        }
    }
    
    private void upgradeWandProperty(Player player, String property, String value) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        try {
            wand.upgradeProperty(property, value);
            wandManager.saveWand(wand);
            updateWandItem(player, wand);
            player.sendMessage(ChatColor.GREEN + "Upgraded wand property '" + property + "' to '" + value + "'");
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid property or value: " + e.getMessage());
        }
    }
    
    private void combineWands(Player player, String wandName) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand targetWand = wandManager.getWandFromItem(heldItem);
        
        if (targetWand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        // Find source wand in inventory
        ItemStack sourceWandItem = null;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isWand(item) && !item.equals(heldItem)) {
                sourceWandItem = item;
                break;
            }
        }
        
        if (sourceWandItem == null) {
            player.sendMessage(ChatColor.RED + "You need another wand in your inventory to combine!");
            return;
        }
        
        Wand sourceWand = wandManager.getWandFromItem(sourceWandItem);
        
        if (sourceWand == null) {
            player.sendMessage(ChatColor.RED + "Could not find source wand data!");
            return;
        }
        
        // Combine wands
        targetWand.combineWith(sourceWand);
        wandManager.saveWand(targetWand);
        
        // Remove source wand
        player.getInventory().removeItem(sourceWandItem);
        
        updateWandItem(player, targetWand);
        player.sendMessage(ChatColor.GREEN + "Successfully combined wands!");
    }
    
    private void addMaterialToWand(Player player, String materialName) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid material: " + materialName);
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        wand.addMaterial(material);
        wandManager.saveWand(wand);
        updateWandItem(player, wand);
        player.sendMessage(ChatColor.GREEN + "Added material '" + materialName + "' to your wand!");
    }
    
    private void removeMaterialFromWand(Player player, String materialName) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid material: " + materialName);
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        if (wand.removeMaterial(material)) {
            wandManager.saveWand(wand);
            updateWandItem(player, wand);
            player.sendMessage(ChatColor.GREEN + "Removed material '" + materialName + "' from your wand!");
        } else {
            player.sendMessage(ChatColor.RED + "Material '" + materialName + "' not found on your wand!");
        }
    }
    
    private void nameWand(Player player, String name) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        wand.setName(name);
        wandManager.saveWand(wand);
        updateWandItem(player, wand);
        player.sendMessage(ChatColor.GREEN + "Named your wand: " + ChatColor.GOLD + name);
    }
    
    private void describeWand(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== Wand Properties ===");
        player.sendMessage(ChatColor.GREEN + "Name: " + ChatColor.WHITE + wand.getName());
        player.sendMessage(ChatColor.GREEN + "Owner: " + ChatColor.WHITE + wand.getOwnerName());
        player.sendMessage(ChatColor.GREEN + "Spells: " + ChatColor.WHITE + wand.getSpells().size());
        player.sendMessage(ChatColor.GREEN + "Materials: " + ChatColor.WHITE + wand.getMaterials().size());
        
        // Show properties
        Map<String, Object> properties = wand.getProperties();
        if (!properties.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "Properties:");
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                player.sendMessage(ChatColor.YELLOW + "  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
    
    private void unlockWand(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        wand.setLocked(false);
        wandManager.saveWand(wand);
        updateWandItem(player, wand);
        player.sendMessage(ChatColor.GREEN + "Unlocked your wand!");
    }
    
    private void enchantHeldItem(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be holding an item!");
            return;
        }
        
        // Create wand from held item
        ItemStack wandItem = heldItem.clone();
        ItemMeta meta = wandItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Magic Wand");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Wand created from " + heldItem.getType().name().toLowerCase());
        lore.add(ChatColor.GRAY + "Right-click to open inventory");
        meta.setLore(lore);
        
        wandItem.setItemMeta(meta);
        
        // Create wand object
        Wand wand = new Wand(player.getUniqueId());
        wand.setItem(wandItem);
        wandManager.saveWand(wand);
        
        // Replace held item
        player.getInventory().setItemInMainHand(wandItem);
        player.sendMessage(ChatColor.GREEN + "Created wand from " + heldItem.getType().name().toLowerCase() + "!");
    }
    
    private void enchantWandWithXP(Player player, int xp) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        wand.addXP(xp);
        wandManager.saveWand(wand);
        updateWandItem(player, wand);
        player.sendMessage(ChatColor.GREEN + "Added " + xp + " XP levels to your wand!");
    }
    
    private void unenchantWand(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (!isWand(heldItem)) {
            player.sendMessage(ChatColor.RED + "You must be holding a wand!");
            return;
        }
        
        Wand wand = wandManager.getWandFromItem(heldItem);
        
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Could not find wand data!");
            return;
        }
        
        // Remove wand from database
        wandManager.deleteWand(wand);
        
        // Remove from inventory
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        player.sendMessage(ChatColor.GREEN + "Destroyed your wand!");
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