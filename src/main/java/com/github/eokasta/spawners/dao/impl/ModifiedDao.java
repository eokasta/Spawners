package com.github.eokasta.spawners.dao.impl;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.dao.Dao;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@RequiredArgsConstructor
public class ModifiedDao implements Dao<Spawner> {

    private final SpawnerPlugin plugin;

    private final Map<Location, Spawner> spawners = new HashMap<>();

    @Getter
    private BukkitTask task;

    public void initTask() {
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                final long before = System.currentTimeMillis();
                plugin.getLogger().info("Saving spawners...");

                new ArrayList<>(getAll()).forEach(ModifiedDao.this::execute);

                plugin.getLogger().info("Saved spawners in " + (System.currentTimeMillis() - before) + "ms.");
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20*60*plugin.getSettings().getSaveTimerDelay());
    }

    @Override
    public Optional<Spawner> get(int id) {
        Spawner spawner = null;
        for (Spawner s : spawners.values())
            if (id == s.getId()) {
                spawner = s;
            }

        return Optional.ofNullable(spawner);
    }

    @Override
    public Optional<Spawner> get(Location location) {
        return Optional.ofNullable(spawners.get(location));
    }

    @Override
    public boolean has(Location location) {
        return spawners.containsKey(location);
    }

    @Override
    public List<Spawner> getAll() {
        return new ArrayList<>(spawners.values());
    }

    @Override
    public void save(Spawner spawner) {
        spawners.put(spawner.getLocation(), spawner);
    }

    @Override
    public void delete(Spawner spawner) {
        spawners.remove(spawner.getLocation());
    }

    public void execute(Spawner spawner) {
        plugin.getManager().getSpawnerDao().save(spawner);
        delete(spawner);
    }

}
