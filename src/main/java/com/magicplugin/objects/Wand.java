package com.magicplugin.objects;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Wand {
    private final String key;
    private final String name;
    private final String description;
    private final List<String> spells;
    private final List<String> materials;
    private final ItemStack itemStack;
    private boolean locked;
    private int uses;
    private int maxUses;
    private double xpRegeneration;
    private double damageReduction;
    private double costReduction;
    
    public Wand(String key, String name, String description, ItemStack itemStack) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.itemStack = itemStack;
        this.spells = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.locked = false;
        this.uses = 0;
        this.maxUses = -1; // Unlimited by default
        this.xpRegeneration = 0.0;
        this.damageReduction = 0.0;
        this.costReduction = 0.0;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<String> getSpells() {
        return new ArrayList<>(spells);
    }
    
    public void addSpell(String spell) {
        if (!spells.contains(spell)) {
            spells.add(spell);
        }
    }
    
    public void removeSpell(String spell) {
        spells.remove(spell);
    }
    
    public boolean hasSpell(String spell) {
        return spells.contains(spell);
    }
    
    public List<String> getMaterials() {
        return new ArrayList<>(materials);
    }
    
    public void addMaterial(String material) {
        if (!materials.contains(material)) {
            materials.add(material);
        }
    }
    
    public void removeMaterial(String material) {
        materials.remove(material);
    }
    
    public boolean hasMaterial(String material) {
        return materials.contains(material);
    }
    
    public ItemStack getItemStack() {
        return itemStack.clone();
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public int getUses() {
        return uses;
    }
    
    public void setUses(int uses) {
        this.uses = uses;
    }
    
    public void incrementUses() {
        this.uses++;
    }
    
    public int getMaxUses() {
        return maxUses;
    }
    
    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }
    
    public boolean hasUnlimitedUses() {
        return maxUses <= 0;
    }
    
    public boolean canUse() {
        return hasUnlimitedUses() || uses < maxUses;
    }
    
    public double getXpRegeneration() {
        return xpRegeneration;
    }
    
    public void setXpRegeneration(double xpRegeneration) {
        this.xpRegeneration = xpRegeneration;
    }
    
    public double getDamageReduction() {
        return damageReduction;
    }
    
    public void setDamageReduction(double damageReduction) {
        this.damageReduction = Math.max(0.0, Math.min(1.0, damageReduction));
    }
    
    public double getCostReduction() {
        return costReduction;
    }
    
    public void setCostReduction(double costReduction) {
        this.costReduction = Math.max(0.0, Math.min(1.0, costReduction));
    }
    
    @Override
    public String toString() {
        return "Wand{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", spells=" + spells.size() +
                ", materials=" + materials.size() +
                ", locked=" + locked +
                '}';
    }
} 