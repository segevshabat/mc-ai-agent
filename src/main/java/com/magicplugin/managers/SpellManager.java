package com.magicplugin.managers;

import com.magicplugin.objects.Spell;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class SpellManager {
    private final ConfigManager configManager;
    private final Logger logger;
    private final Map<String, Spell> spells;
    private final Map<UUID, Long> cooldowns;
    
    public SpellManager(ConfigManager configManager, Logger logger) {
        this.configManager = configManager;
        this.logger = logger;
        this.spells = new HashMap<>();
        this.cooldowns = new HashMap<>();
        loadSpells();
    }
    
    public void loadSpells() {
        spells.clear();
        FileConfiguration spellsConfig = configManager.getSpells();
        
        if (spellsConfig.getConfigurationSection("spells") != null) {
            for (String spellKey : spellsConfig.getConfigurationSection("spells").getKeys(false)) {
                ConfigurationSection spellSection = spellsConfig.getConfigurationSection("spells." + spellKey);
                if (spellSection != null) {
                    Spell spell = loadSpellFromConfig(spellKey, spellSection);
                    spells.put(spellKey, spell);
                }
            }
        }
        
        logger.info("Loaded " + spells.size() + " spells");
    }
    
    private Spell loadSpellFromConfig(String key, ConfigurationSection section) {
        String name = section.getString("name", key);
        String description = section.getString("description", "A magical spell");
        String category = section.getString("category", "general");
        String icon = section.getString("icon", "STICK");
        int cooldown = section.getInt("cooldown", 0);
        int range = section.getInt("range", 10);
        boolean enabled = section.getBoolean("enabled", true);
        
        return new Spell(key, name, description, category, icon, cooldown, range, enabled);
    }
    
    public Spell getSpell(String key) {
        return spells.get(key);
    }
    
    public Collection<Spell> getAllSpells() {
        return spells.values();
    }
    
    public List<Spell> getSpellsByCategory(String category) {
        return spells.values().stream()
                .filter(spell -> spell.getCategory().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(Spell::getName))
                .toList();
    }
    
    public List<Spell> searchSpells(String query) {
        return spells.values().stream()
                .filter(spell -> spell.getName().toLowerCase().contains(query.toLowerCase()) ||
                               spell.getDescription().toLowerCase().contains(query.toLowerCase()))
                .sorted(Comparator.comparing(Spell::getName))
                .toList();
    }
    
    public boolean canCast(Player player, String spellKey) {
        Spell spell = getSpell(spellKey);
        if (spell == null || !spell.isEnabled()) {
            return false;
        }
        
        // Check permissions
        if (!player.hasPermission("Magic.cast." + spellKey)) {
            return false;
        }
        
        // Check cooldown
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(playerId)) {
            long lastCast = cooldowns.get(playerId);
            long timeSince = (currentTime - lastCast) / 1000;
            if (timeSince < spell.getCooldown()) {
                return false;
            }
        }
        
        return true;
    }
    
    public void setCooldown(Player player, String spellKey) {
        Spell spell = getSpell(spellKey);
        if (spell != null && spell.getCooldown() > 0) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
    
    public long getRemainingCooldown(Player player, String spellKey) {
        Spell spell = getSpell(spellKey);
        if (spell == null || spell.getCooldown() == 0) {
            return 0;
        }
        
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }
        
        long lastCast = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long timeSince = (currentTime - lastCast) / 1000;
        long remaining = spell.getCooldown() - timeSince;
        
        return Math.max(0, remaining);
    }
    
    public Set<String> getCategories() {
        Set<String> categories = new HashSet<>();
        for (Spell spell : spells.values()) {
            categories.add(spell.getCategory());
        }
        return categories;
    }
    
    public boolean spellExists(String spellKey) {
        return spells.containsKey(spellKey);
    }
} 