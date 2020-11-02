package com.github.eokasta.spawners.entities;

import com.github.eokasta.spawners.SpawnerPlugin;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Data
public class Spawner {

    private int id;
    private Location location;
    private EntityType entityType;
    private String owner;
    private double amount;
    private Map<Material, Double> drops;
    private boolean isModified;

    public Spawner(int id, Location location, EntityType entityType, String owner, double amount) {
        this.id = id;
        this.location = location;
        this.entityType = entityType;
        this.owner = owner;
        this.amount = amount;
        this.drops = new HashMap<>();
    }

}
