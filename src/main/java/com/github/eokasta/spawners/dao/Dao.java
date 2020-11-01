package com.github.eokasta.spawners.dao;

import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(int id);

    Optional<T> get(Location location);

    boolean has(Location location);

    List<T> getAll();

    void save(T t);

    void delete(T t);
}