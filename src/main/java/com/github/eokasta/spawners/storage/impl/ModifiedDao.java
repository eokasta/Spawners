package com.github.eokasta.spawners.storage.impl;

import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.storage.dao.Dao;
import org.bukkit.Location;

import java.util.*;

public class ModifiedDao implements Dao<Spawner> {

    private final Map<Location, Spawner> spawners = new HashMap<>();

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
        return (List<Spawner>) spawners.values();
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
