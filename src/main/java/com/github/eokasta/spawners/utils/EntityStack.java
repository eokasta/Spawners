package com.github.eokasta.spawners.utils;

import com.github.eokasta.nbtapi.nbt.NBTEntity;
import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

@RequiredArgsConstructor
public class EntityStack {

    private final SpawnerPlugin plugin;

    public double getStacks(Entity entity) {
        if ((!entity.hasMetadata("stacks") || entity.getMetadata("stacks").isEmpty())
                || (!entity.hasMetadata("spawner-location") || entity.getMetadata("spawner-location").isEmpty()))
            return -1;

        return entity.getMetadata("stacks").get(0).asDouble();
    }

    public Spawner getSpawner(Entity entity) {
        if ((!entity.hasMetadata("stacks") || entity.getMetadata("stacks").isEmpty())
                || (!entity.hasMetadata("spawner-location") || entity.getMetadata("spawner-location").isEmpty()))
            return null;

        final Location location = Helper.deserializeLocation(entity.getMetadata("spawner-location").get(0).asString());
        final Spawner spawner = plugin.getManager().get(location);

        if (spawner == null || spawner.getEntityType() != entity.getType())
            return null;

        return spawner;
    }

    public void setStacks(Entity entity, double stacks, Spawner spawner) {
        final NBTEntity nbtEntity = new NBTEntity(entity);
        nbtEntity.setBoolean("NoAI", true);

        entity.setMetadata("stacks", new FixedMetadataValue(plugin, stacks));
        entity.setMetadata("spawner-location", new FixedMetadataValue(plugin, Helper.serializeLocation(spawner.getLocation())));

        entity.setCustomName(Helper.format("&a" + Helper.formatBalance(getStacks(entity)) + " " + entity.getType().getName()));
        entity.setCustomNameVisible(true);
    }

}
