package com.magicplugin;

import com.magicplugin.commands.*;
import com.magicplugin.listeners.MagicListener;
import com.magicplugin.managers.*;
import com.magicplugin.objects.Wand;
import com.magicplugin.objects.Spell;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class MagicPlugin extends JavaPlugin {
    
    private static MagicPlugin instance;
    private Logger logger;
    
    // Managers
    private WandManager wandManager;
    private SpellManager spellManager;
    private MagicManager magicManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        logger.info("=== Magic Plugin Starting ===");
        
        // Initialize managers
        initializeManagers();
        
        // Load configurations
        loadConfigurations();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Load data
        loadData();
        
        logger.info("=== Magic Plugin Started Successfully ===");
    }
    
    @Override
    public void onDisable() {
        logger.info("=== Magic Plugin Shutting Down ===");
        
        // Save all data
        if (wandManager != null) {
            wandManager.saveAllWands();
        }
        
        if (spellManager != null) {
            spellManager.saveAllSpells();
        }
        
        logger.info("=== Magic Plugin Shut Down ===");
    }
    
    private void initializeManagers() {
        logger.info("Initializing managers...");
        
        configManager = new ConfigManager(this);
        spellManager = new SpellManager(this);
        wandManager = new WandManager(this);
        magicManager = new MagicManager(this);
        
        logger.info("Managers initialized successfully!");
    }
    
    private void loadConfigurations() {
        logger.info("Loading configurations...");
        
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Load all configuration files
        configManager.loadConfigurations();
        
        logger.info("Configurations loaded successfully!");
    }
    
    private void registerCommands() {
        logger.info("Registering commands...");
        
        // Wand commands
        getCommand("wand").setExecutor(new WandCommand(this));
        getCommand("wandp").setExecutor(new WandPlayerCommand(this));
        
        // Magic commands
        getCommand("magic").setExecutor(new MagicCommand(this));
        
        // Spell commands
        getCommand("spells").setExecutor(new SpellsCommand(this));
        getCommand("cast").setExecutor(new CastCommand(this));
        
        // Give commands
        getCommand("mgive").setExecutor(new MagicGiveCommand(this));
        
        logger.info("Commands registered successfully!");
    }
    
    private void registerListeners() {
        logger.info("Registering listeners...");
        
        getServer().getPluginManager().registerEvents(new MagicListener(this), this);
        
        logger.info("Listeners registered successfully!");
    }
    
    private void loadData() {
        logger.info("Loading data...");
        
        // Load spells
        spellManager.loadSpells();
        
        // Load wands
        wandManager.loadWands();
        
        logger.info("Data loaded successfully!");
    }
    
    // Getters for managers
    public static MagicPlugin getInstance() {
        return instance;
    }
    
    public WandManager getWandManager() {
        return wandManager;
    }
    
    public SpellManager getSpellManager() {
        return spellManager;
    }
    
    public MagicManager getMagicManager() {
        return magicManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public FileConfiguration getMainConfig() {
        return getConfig();
    }
} 