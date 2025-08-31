package com.magicplugin.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration spells;
    private FileConfiguration wands;
    private FileConfiguration messages;
    
    private File configFile;
    private File spellsFile;
    private File wandsFile;
    private File messagesFile;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createConfigs();
        loadConfigs();
    }
    
    private void createConfigs() {
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Create config files
        configFile = new File(plugin.getDataFolder(), "config.yml");
        spellsFile = new File(plugin.getDataFolder(), "spells.yml");
        wandsFile = new File(plugin.getDataFolder(), "wands.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        // Create default files if they don't exist
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        if (!spellsFile.exists()) {
            plugin.saveResource("spells.yml", false);
        }
        if (!wandsFile.exists()) {
            plugin.saveResource("wands.yml", false);
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }
    
    public void loadConfigs() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            spells = YamlConfiguration.loadConfiguration(spellsFile);
            wands = YamlConfiguration.loadConfiguration(wandsFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading configuration files", e);
        }
    }
    
    public void reloadConfigs() {
        loadConfigs();
    }
    
    public void saveConfigs() {
        try {
            config.save(configFile);
            spells.save(spellsFile);
            wands.save(wandsFile);
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error saving configuration files", e);
        }
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public FileConfiguration getSpells() {
        return spells;
    }
    
    public FileConfiguration getWands() {
        return wands;
    }
    
    public FileConfiguration getMessages() {
        return messages;
    }
    
    public String getMessage(String key) {
        return messages.getString(key, key);
    }
    
    public String getMessage(String key, String defaultValue) {
        return messages.getString(key, defaultValue);
    }
} 