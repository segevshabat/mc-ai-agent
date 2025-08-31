package com.magicplugin.objects;

public class Spell {
    private final String key;
    private final String name;
    private final String description;
    private final String category;
    private final String icon;
    private final int cooldown;
    private final int range;
    private final boolean enabled;
    
    public Spell(String key, String name, String description, String category, 
                 String icon, int cooldown, int range, boolean enabled) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.category = category;
        this.icon = icon;
        this.cooldown = cooldown;
        this.range = range;
        this.enabled = enabled;
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
    
    public String getCategory() {
        return category;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public int getRange() {
        return range;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public String toString() {
        return "Spell{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", enabled=" + enabled +
                '}';
    }
} 