package com.github.eokasta.spawners.listeners;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.utils.EntityStack;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerSpawnListeners implements Listener {

    private final SpawnerPlugin plugin;
    private final EntityStack entityStack;

    public SpawnerSpawnListeners(SpawnerPlugin plugin) {
        this.plugin = plugin;
        this.entityStack = plugin.getEntityStack();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(SpawnerSpawnEvent event) {
        final Entity entity = event.getEntity();
        final CreatureSpawner creatureSpawner = event.getSpawner();
        if (creatureSpawner == null)
            return;

        final Block block = creatureSpawner.getBlock();

        final Spawner spawner = plugin.getManager().get(block.getLocation());
        if (spawner == null) {
            event.setCancelled(true);
            return;
        }

        final int radius = plugin.getSettings().getStackEntitiesRadius();
        for (Entity nearbyEntity : spawner.getLocation().getWorld().getNearbyEntities(spawner.getLocation(), radius, radius, radius)) {
            if (nearbyEntity.getType() == spawner.getEntityType() && entityStack.getStacks(nearbyEntity) != -1) {
                entityStack.setStacks(nearbyEntity, entityStack.getStacks(nearbyEntity) + spawner.getAmount(), spawner);

                event.setCancelled(true);
                return;
            }
        }

        entityStack.setStacks(entity, spawner.getAmount(), spawner);
    }

}
