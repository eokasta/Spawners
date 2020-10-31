package com.github.eokasta.spawners.utils;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Data
public class Settings {

    private final YamlConfig config;

    public ConfigurationSection getSQLSettings() {
        return config.getConfig().getConfigurationSection("mysql");
    }

    public List<String> getAllowedWorlds() {
        return config.getConfig().getStringList("allowed-worlds");
    }

    public int getSaveTimerDelay() {
        return config.getConfig().getInt("save-timer-delay", 5);
    }

    public int getStackEntitiesRadius() {
        return config.getConfig().getInt("stack-entities-radius", 10);
    }

}
