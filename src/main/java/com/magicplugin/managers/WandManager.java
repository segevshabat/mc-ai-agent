package com.magicplugin.managers;

import com.magicplugin.objects.Wand;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;

public class WandManager {
    private final ConfigManager configManager;
    private final Logger logger;
    private final Map<String, Wand> wandTemplates;
    private final Map<UUID, Set<String>> lostWands;
    
    public WandManager(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
        this.wandTemplates = new HashMap<>();
        this.lostWands = new HashMap<>();
        loadWands();
    }
    
    public void loadWands() {
        wandTemplates.clear();
        FileConfiguration wandsConfig = configManager.getWands();
        
        if (wandsConfig.getConfigurationSection("wands") != null) {
            for (String wandKey : wandsConfig.getConfigurationSection("wands").getKeys(false)) {
                ConfigurationSection wandSection = wandsConfig.getConfigurationSection("wands." + wandKey);
                if (wandSection != null) {
                    Wand wand = loadWandFromConfig(wandKey, wandSection);
                    wandTemplates.put(wandKey, wand);
                }
            }
        }
        
        logger.info("Loaded " + wandTemplates.size() + " wand templates");
    }
    
    private Wand loadWandFromConfig(String key, ConfigurationSection section) {
        String name = section.getString("name", key);
        String description = section.getString("description", "A magical wand");
        String materialName = section.getString("material", "STICK");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.STICK;
            logger.warning("Invalid material '" + materialName + "' for wand '" + key + "', using STICK");
        }
        
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(description));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemStack.setItemMeta(meta);
        }
        
        Wand wand = new Wand(key, name, description, itemStack);
        
        // Load spells
        if (section.isList("spells")) {
            for (String spell : section.getStringList("spells")) {
                wand.addSpell(spell);
            }
        }
        
        // Load materials
        if (section.isList("materials")) {
            for (String mat : section.getStringList("materials")) {
                wand.addMaterial(mat);
            }
        }
        
        // Load properties
        wand.setLocked(section.getBoolean("locked", false));
        wand.setMaxUses(section.getInt("max_uses", -1));
        wand.setXpRegeneration(section.getDouble("xp_regeneration", 0.0));
        wand.setDamageReduction(section.getDouble("damage_reduction", 0.0));
        wand.setCostReduction(section.getDouble("cost_reduction", 0.0));
        
        return wand;
    }
    
    public Wand getWandTemplate(String key) {
        return wandTemplates.get(key);
    }
    
    public Wand createWand(String key) {
        Wand template = getWandTemplate(key);
        if (template == null) {
            return null;
        }
        
        // Create a copy of the template
        ItemStack itemStack = template.getItemStack();
        Wand wand = new Wand(template.getKey(), template.getName(), template.getDescription(), itemStack);
        
        // Copy all properties
        for (String spell : template.getSpells()) {
            wand.addSpell(spell);
        }
        for (String material : template.getMaterials()) {
            wand.addMaterial(material);
        }
        
        wand.setLocked(template.isLocked());
        wand.setMaxUses(template.getMaxUses());
        wand.setXpRegeneration(template.getXpRegeneration());
        wand.setDamageReduction(template.getDamageReduction());
        wand.setCostReduction(template.getCostReduction());
        
        return wand;
    }
    
    public Wand createEmptyWand() {
        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("Empty Wand");
            meta.setLore(Arrays.asList("A magical wand"));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemStack.setItemMeta(meta);
        }
        
        return new Wand("empty", "Empty Wand", "A magical wand", itemStack);
    }
    
    public boolean isWand(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        
        return meta.hasEnchant(Enchantment.UNBREAKING) && 
               (meta.getDisplayName().contains("Wand") || meta.getDisplayName().contains("wand"));
    }
    
    public Set<String> getWandTemplateKeys() {
        return new HashSet<>(wandTemplates.keySet());
    }
    
    public Collection<Wand> getAllWandTemplates() {
        return wandTemplates.values();
    }
    
    public void addLostWand(Player player, String wandKey) {
        UUID playerId = player.getUniqueId();
        lostWands.computeIfAbsent(playerId, k -> new HashSet<>()).add(wandKey);
    }
    
    public Set<String> getLostWands(Player player) {
        return lostWands.getOrDefault(player.getUniqueId(), new HashSet<>());
    }
    
    public void clearLostWands(Player player) {
        lostWands.remove(player.getUniqueId());
    }
    
    public void clearAllLostWands() {
        lostWands.clear();
    }
    
    public boolean wandTemplateExists(String key) {
        return wandTemplates.containsKey(key);
    }
} 