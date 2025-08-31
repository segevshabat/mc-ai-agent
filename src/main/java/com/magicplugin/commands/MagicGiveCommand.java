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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MagicGiveCommand implements CommandExecutor {
    
    private final MagicPlugin plugin;
    private final WandManager wandManager;
    private final SpellManager spellManager;
    
    public MagicGiveCommand(MagicPlugin plugin) {
        this.plugin = plugin;
        this.wandManager = plugin.getWandManager();
        this.spellManager = plugin.getSpellManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("magic.commands.mgive")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }
        
        Player targetPlayer;
        String itemName;
        int amount = 1;
        
        if (args.length == 1) {
            // /mgive <item> - Give to self
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to give items to yourself!");
                return true;
            }
            targetPlayer = (Player) sender;
            itemName = args[0];
        } else if (args.length == 2) {
            // /mgive <player> <item> or /mgive <item> <amount>
            if (isPlayerName(args[0])) {
                // /mgive <player> <item>
                targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
                    return true;
                }
                itemName = args[1];
            } else {
                // /mgive <item> <amount>
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You must be a player to give items to yourself!");
                    return true;
                }
                targetPlayer = (Player) sender;
                itemName = args[0];
                try {
                    amount = Integer.parseInt(args[1]);
                    if (amount <= 0 || amount > 64) {
                        sender.sendMessage(ChatColor.RED + "Amount must be between 1 and 64!");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[1]);
                    return true;
                }
            }
        } else if (args.length == 3) {
            // /mgive <player> <item> <amount>
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
                return true;
            }
            itemName = args[1];
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0 || amount > 64) {
                    sender.sendMessage(ChatColor.RED + "Amount must be between 1 and 64!");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return true;
            }
        } else {
            sendHelpMessage(sender);
            return true;
        }
        
        // Give the item
        giveItem(sender, targetPlayer, itemName, amount);
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Magic Give Command Help ===");
        sender.sendMessage(ChatColor.YELLOW + "Usage:");
        sender.sendMessage(ChatColor.GREEN + "• /mgive <item> " + ChatColor.WHITE + "- Give item to yourself");
        sender.sendMessage(ChatColor.GREEN + "• /mgive <item> <amount> " + ChatColor.WHITE + "- Give amount to yourself");
        sender.sendMessage(ChatColor.GREEN + "• /mgive <player> <item> " + ChatColor.WHITE + "- Give item to player");
        sender.sendMessage(ChatColor.GREEN + "• /mgive <player> <item> <amount> " + ChatColor.WHITE + "- Give amount to player");
        sender.sendMessage(ChatColor.WHITE + "Examples:");
        sender.sendMessage(ChatColor.GREEN + "• /mgive elder " + ChatColor.WHITE + "- Give yourself an Elder Wand");
        sender.sendMessage(ChatColor.GREEN + "• /mgive PlayerName fireball 5 " + ChatColor.WHITE + "- Give player 5 fireball spells");
        sender.sendMessage(ChatColor.GREEN + "• /mgive PlayerName xp 200 " + ChatColor.WHITE + "- Give player 200 XP");
        sender.sendMessage(ChatColor.GREEN + "• /mgive spell:wolf " + ChatColor.WHITE + "- Give wolf spell");
        sender.sendMessage(ChatColor.GREEN + "• /mgive book:engineering " + ChatColor.WHITE + "- Give engineering spellbook");
    }
    
    private boolean isPlayerName(String name) {
        return Bukkit.getPlayer(name) != null;
    }
    
    private void giveItem(CommandSender sender, Player targetPlayer, String itemName, int amount) {
        try {
            ItemStack item = createItem(itemName, amount);
            
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Unknown item: " + itemName);
                return;
            }
            
            // Give item to player
            targetPlayer.getInventory().addItem(item);
            
            // Send messages
            if (sender.equals(targetPlayer)) {
                sender.sendMessage(ChatColor.GREEN + "Gave yourself " + amount + "x " + getItemDisplayName(itemName) + "!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Gave " + targetPlayer.getName() + " " + amount + "x " + getItemDisplayName(itemName) + "!");
                targetPlayer.sendMessage(ChatColor.GREEN + "You received " + amount + "x " + getItemDisplayName(itemName) + "!");
            }
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error giving item: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private ItemStack createItem(String itemName, int amount) {
        // Check for special prefixes
        if (itemName.startsWith("wand:")) {
            return createWandItem(itemName.substring(5), amount);
        } else if (itemName.startsWith("spell:")) {
            return createSpellItem(itemName.substring(6), amount);
        } else if (itemName.startsWith("book:")) {
            return createSpellBook(itemName.substring(5), amount);
        } else if (itemName.equalsIgnoreCase("xp")) {
            return createXPItem(amount);
        } else {
            // Try to create as regular item
            return createRegularItem(itemName, amount);
        }
    }
    
    private ItemStack createWandItem(String wandName, int amount) {
        // Check if wand template exists
        if (!wandManager.templateExists(wandName)) {
            return null;
        }
        
        Wand template = wandManager.getTemplate(wandName);
        ItemStack item = template.getItem().clone();
        item.setAmount(amount);
        
        return item;
    }
    
    private ItemStack createSpellItem(String spellName, int amount) {
        // Check if spell exists
        if (!spellManager.spellExists(spellName)) {
            return null;
        }
        
        Spell spell = spellManager.getSpell(spellName);
        ItemStack item = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + spell.getName() + " Spell");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Spell: " + spell.getName());
        lore.add(ChatColor.GRAY + "Type: " + spell.getType());
        lore.add(ChatColor.GRAY + "Right-click to learn");
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createSpellBook(String category, int amount) {
        ItemStack item = new ItemStack(Material.BOOK, amount);
        ItemMeta meta = item.getItemMeta();
        
        if (category.equalsIgnoreCase("all")) {
            meta.setDisplayName(ChatColor.GOLD + "Master Spellbook");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Contains all available spells");
            lore.add(ChatColor.GRAY + "Right-click to browse");
            meta.setLore(lore);
        } else {
            meta.setDisplayName(ChatColor.GOLD + category.substring(0, 1).toUpperCase() + category.substring(1) + " Spellbook");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Contains " + category + " spells");
            lore.add(ChatColor.GRAY + "Right-click to browse");
            meta.setLore(lore);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createXPItem(int amount) {
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE, amount);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GREEN + "XP Bottle");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Contains " + amount + " XP levels");
        lore.add(ChatColor.GRAY + "Right-click to consume");
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createRegularItem(String itemName, int amount) {
        try {
            Material material = Material.valueOf(itemName.toUpperCase());
            return new ItemStack(material, amount);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    private String getItemDisplayName(String itemName) {
        if (itemName.startsWith("wand:")) {
            return "Wand (" + itemName.substring(5) + ")";
        } else if (itemName.startsWith("spell:")) {
            return "Spell (" + itemName.substring(6) + ")";
        } else if (itemName.startsWith("book:")) {
            String category = itemName.substring(5);
            if (category.equalsIgnoreCase("all")) {
                return "Master Spellbook";
            } else {
                return category.substring(0, 1).toUpperCase() + category.substring(1) + " Spellbook";
            }
        } else if (itemName.equalsIgnoreCase("xp")) {
            return "XP";
        } else {
            try {
                Material material = Material.valueOf(itemName.toUpperCase());
                return material.name().toLowerCase().replace("_", " ");
            } catch (IllegalArgumentException e) {
                return itemName;
            }
        }
    }
} 