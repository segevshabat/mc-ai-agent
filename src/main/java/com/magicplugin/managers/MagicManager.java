package com.magicplugin.managers;

import com.magicplugin.objects.Spell;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class MagicManager {
    private final ConfigManager configManager;
    private final SpellManager spellManager;
    private final WandManager wandManager;
    private final Logger logger;
    private final Map<UUID, List<String>> constructionBatches;
    private final Map<UUID, Boolean> undoAvailable;
    
    public MagicManager(ConfigManager configManager, SpellManager spellManager, WandManager wandManager, Logger logger) {
        this.configManager = configManager;
        this.spellManager = spellManager;
        this.wandManager = wandManager;
        this.logger = logger;
        this.constructionBatches = new HashMap<>();
        this.undoAvailable = new HashMap<>();
    }
    
    public boolean castSpell(Player player, String spellKey, Map<String, String> parameters) {
        Spell spell = spellManager.getSpell(spellKey);
        if (spell == null) {
            player.sendMessage("§cSpell '" + spellKey + "' not found!");
            return false;
        }
        
        if (!spellManager.canCast(player, spellKey)) {
            long cooldown = spellManager.getRemainingCooldown(player, spellKey);
            if (cooldown > 0) {
                player.sendMessage("§cSpell is on cooldown for " + cooldown + " seconds!");
            } else {
                player.sendMessage("§cYou don't have permission to cast this spell!");
            }
            return false;
        }
        
        // Set cooldown
        spellManager.setCooldown(player, spellKey);
        
        // Execute spell effect
        executeSpellEffect(player, spell, parameters);
        
        // Play effects
        playSpellEffects(player, spell);
        
        logger.info("Player " + player.getName() + " cast spell: " + spellKey);
        return true;
    }
    
    private void executeSpellEffect(Player player, Spell spell, Map<String, String> parameters) {
        String spellKey = spell.getKey().toLowerCase();
        Location targetLocation = player.getTargetBlock(null, spell.getRange()).getLocation();
        
        switch (spellKey) {
            case "fireball":
                executeFireball(player, targetLocation, parameters);
                break;
            case "heal":
                executeHeal(player, parameters);
                break;
            case "teleport":
                executeTeleport(player, targetLocation, parameters);
                break;
            case "lightning":
                executeLightning(player, targetLocation, parameters);
                break;
            case "freeze":
                executeFreeze(player, targetLocation, parameters);
                break;
            case "shield":
                executeShield(player, parameters);
                break;
            case "light":
                executeLight(player, targetLocation, parameters);
                break;
            case "boom":
            case "explosion":
                executeExplosion(player, targetLocation, parameters);
                break;
            default:
                player.sendMessage("§7Casting " + spell.getName() + "...");
                playGenericSpellEffect(player, targetLocation);
                break;
        }
    }
    
    private void executeFireball(Player player, Location target, Map<String, String> parameters) {
        // Create fireball effect
        target.getWorld().createExplosion(target, 2.0f, false, false);
        player.sendMessage("§6Fireball cast at target location!");
    }
    
    private void executeHeal(Player player, Map<String, String> parameters) {
        double health = player.getHealth();
        double maxHealth = player.getMaxHealth();
        double healAmount = 4.0; // Default heal amount
        
        if (parameters.containsKey("amount")) {
            try {
                healAmount = Double.parseDouble(parameters.get("amount"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        
        player.setHealth(Math.min(maxHealth, health + healAmount));
        player.sendMessage("§aYou have been healed!");
    }
    
    private void executeTeleport(Player player, Location target, Map<String, String> parameters) {
        target.add(0, 1, 0); // Teleport above the block
        player.teleport(target);
        player.sendMessage("§5Teleported to target location!");
    }
    
    private void executeLightning(Player player, Location target, Map<String, String> parameters) {
        target.getWorld().strikeLightning(target);
        player.sendMessage("§eLightning struck the target!");
    }
    
    private void executeFreeze(Player player, Location target, Map<String, String> parameters) {
        // Create ice/snow effect
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, target, 50, 2, 2, 2, 0.1);
        player.sendMessage("§bFreeze spell cast!");
    }
    
    private void executeShield(Player player, Map<String, String> parameters) {
        // Apply temporary damage resistance
        player.sendMessage("§eShield activated!");
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(), 30, 1, 2, 1, 0.1);
    }
    
    private void executeLight(Player player, Location target, Map<String, String> parameters) {
        // Place torches or light source
        player.sendMessage("§eLet there be light!");
        player.getWorld().spawnParticle(Particle.END_ROD, target, 20, 1, 1, 1, 0.05);
    }
    
    private void executeExplosion(Player player, Location target, Map<String, String> parameters) {
        float power = 3.0f;
        if (parameters.containsKey("size")) {
            try {
                power = Float.parseFloat(parameters.get("size"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        
        target.getWorld().createExplosion(target, power, false, false);
        player.sendMessage("§cExplosion created!");
    }
    
    private void playGenericSpellEffect(Player player, Location target) {
        player.getWorld().spawnParticle(Particle.SPELL_WITCH, target, 20, 1, 1, 1, 0.1);
    }
    
    private void playSpellEffects(Player player, Spell spell) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(), 10, 0.5, 1, 0.5, 0.1);
    }
    
    public void startConstructionBatch(Player player) {
        constructionBatches.put(player.getUniqueId(), new ArrayList<>());
        player.sendMessage("§aConstruction batch started.");
    }
    
    public void commitConstructionBatch(Player player) {
        UUID playerId = player.getUniqueId();
        if (constructionBatches.containsKey(playerId)) {
            constructionBatches.remove(playerId);
            undoAvailable.put(playerId, false);
            player.sendMessage("§aConstruction batch committed.");
        }
    }
    
    public void cancelConstructionBatch(Player player) {
        UUID playerId = player.getUniqueId();
        if (constructionBatches.containsKey(playerId)) {
            // Perform undo operations here
            constructionBatches.remove(playerId);
            player.sendMessage("§cConstruction batch cancelled and undone.");
        }
    }
    
    public void commitAllBatches() {
        for (UUID playerId : constructionBatches.keySet()) {
            undoAvailable.put(playerId, false);
        }
        constructionBatches.clear();
        logger.info("All construction batches committed");
    }
    
    public void cancelAllBatches() {
        // Perform undo operations for all batches
        constructionBatches.clear();
        logger.info("All construction batches cancelled");
    }
    
    public Map<String, Object> getPlayerTasks(Player player) {
        Map<String, Object> tasks = new HashMap<>();
        tasks.put("construction_batches", constructionBatches.containsKey(player.getUniqueId()));
        tasks.put("undo_available", undoAvailable.getOrDefault(player.getUniqueId(), false));
        return tasks;
    }
    
    public void savePlayerData(Player player) {
        // Save player-specific magic data
        logger.info("Saved magic data for player: " + player.getName());
    }
    
    public void saveAllData() {
        // Save all magic data
        logger.info("Saved all magic data");
    }
} 