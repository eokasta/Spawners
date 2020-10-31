package com.github.eokasta.spawners.listeners;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.EntityStack;
import com.github.eokasta.spawners.entities.Spawner;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class SpawnerEntityDeathListeners implements Listener {

    private final SpawnerPlugin plugin;
    private final EntityStack entityStack;

    public SpawnerEntityDeathListeners(SpawnerPlugin plugin) {
        this.plugin = plugin;
        this.entityStack = plugin.getEntityStack();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final Player killer = event.getEntity().getKiller();
        if (entity instanceof Player)
            return;

        event.getDrops().clear();

        final double stacks = entityStack.getStacks(entity);
        if (stacks == -1)
            return;

        final Spawner spawner = entityStack.getSpawner(entity);
        if (spawner == null)
            return;

        /*
        TODO: add modificated drop
         */
        spawner.getDrops().put(Material.STONE, spawner.getDrops().getOrDefault(Material.STONE, 0D) + stacks);

        event.getDrops().clear();
    }

}
