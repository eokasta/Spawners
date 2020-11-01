package com.github.eokasta.spawners.dao.impl;

import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.dao.Dao;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CacheDao implements Dao<Spawner> {

    private final HashMap<Location, Spawner> spawners = new HashMap<>();

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

}
