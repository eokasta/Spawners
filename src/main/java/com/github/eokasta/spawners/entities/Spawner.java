package com.github.eokasta.spawners.entities;

import com.github.eokasta.spawners.SpawnerPlugin;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

@Data
public class Spawner {

    private final SpawnerPlugin plugin;
    private int id;
    private Location location;
    private EntityType entityType;
    private String owner;
    private double amount;
    private Map<Material, Double> drops;

    private final SpawnerMainInventory spawnerInventory;
    private final SpawnerDropsInventory dropsInventory;

    public Spawner(SpawnerPlugin plugin, int id, Location location, EntityType entityType, String owner, double amount) {
        this.plugin = plugin;
        this.id = id;
        this.location = location;
        this.entityType = entityType;
        this.owner = owner;
        this.amount = amount;
        this.drops = new HashMap<>();
        this.spawnerInventory = new SpawnerMainInventory(plugin, this);
        this.dropsInventory = new SpawnerDropsInventory(plugin, this);
    }

}
