package com.github.eokasta.spawners.storage.impl;

import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.storage.DatabaseManager;
import com.github.eokasta.spawners.storage.dao.Dao;
import com.github.eokasta.spawners.utils.Helper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SpawnerDao implements Dao<Spawner> {

    private final DatabaseManager database;
    private final Gson gson;

    public SpawnerDao(DatabaseManager database) {
        this.database = database;
        this.gson = database.getPlugin().getGson();
    }

    @Override
    public Optional<Spawner> get(int id) {
        Spawner spawner = null;

        try (final Connection connection = database.getDataSource().getConnection();
             final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + database.getSpawnersTable() + " WHERE id = '" + id + "'");
             final ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                final Type type = new TypeToken<Map<Material, Double>>() {}.getType();

                spawner = new Spawner(
                        resultSet.getInt("id"),
                        Helper.deserializeLocation(resultSet.getString("location")),
                        EntityType.valueOf(resultSet.getString("entitytype")),
                        resultSet.getString("owner"),
                        resultSet.getDouble("amount")
                );

                final Map<Material, Double> drops = gson.fromJson(resultSet.getString("drops"), type);
                spawner.setDrops(drops);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(spawner);
    }

    @Override
    public Optional<Spawner> get(Location location) {
        Spawner spawner = null;

        try (final Connection connection = database.getDataSource().getConnection();
             final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + database.getSpawnersTable() + " WHERE location = '" + Helper.serializeLocation(location) + "'");
             final ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                final Type type = new TypeToken<Map<Material, Double>>() {}.getType();

                spawner = new Spawner(
                        resultSet.getInt("id"),
                        Helper.deserializeLocation(resultSet.getString("location")),
                        EntityType.valueOf(resultSet.getString("entitytype")),
                        resultSet.getString("owner"),
                        resultSet.getDouble("amount")
                );

                final Map<Material, Double> drops = gson.fromJson(resultSet.getString("drops"), type);
                spawner.setDrops(drops);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(spawner);
    }

    @Override
    public boolean has(Location location) {
        try (
                final Connection connection = database.getDataSource().getConnection();
                final PreparedStatement statement = connection.prepareStatement("SELECT location FROM " + database.getSpawnersTable() + " WHERE location = ?")
        ) {
            statement.setString(1, Helper.serializeLocation(location));
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Spawner> getAll() {
        final List<Spawner> spawners = new ArrayList<>();

        try (
                final Connection connection = database.getDataSource().getConnection();
                final PreparedStatement statement = connection.prepareStatement(String.format("SELECT * FROM %s;", database.getSpawnersTable()));
                final ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                final Type type = new TypeToken<Map<Material, Double>>() {}.getType();

                final Spawner spawner = new Spawner(
                        resultSet.getInt("id"),
                        Helper.deserializeLocation(resultSet.getString("location")),
                        EntityType.valueOf(resultSet.getString("entitytype")),
                        resultSet.getString("owner"),
                        resultSet.getDouble("amount")
                );

                final Map<Material, Double> drops = gson.fromJson(resultSet.getString("drops"), type);
                spawner.setDrops(drops);

                spawners.add(spawner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return spawners;
    }

    @Override
    public void save(Spawner spawner) {
        if (has(spawner.getLocation()))
            update(spawner);
        else
            insert(spawner);
    }

    @Override
    public void delete(Spawner spawner) {
        try (
                final Connection connection = database.getDataSource().getConnection();
                final PreparedStatement statement = connection.prepareStatement("DELETE FROM " + database.getSpawnersTable() + " WHERE location = ?")) {
            statement.setString(1, Helper.serializeLocation(spawner.getLocation()));

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert(Spawner spawner) {
        try (
                final Connection connection = database.getDataSource().getConnection();
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO " + database.getSpawnersTable() + " (location, entitytype, owner, amount, drops) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, Helper.serializeLocation(spawner.getLocation()));
            statement.setString(2, spawner.getEntityType().name());
            statement.setString(3, spawner.getOwner());
            statement.setDouble(4, spawner.getAmount());
            statement.setString(5, gson.toJson(spawner.getDrops()));

            final int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating user failed, no rows affected.");

            try (final ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next())
                    spawner.setId(resultSet.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Spawner spawner) {
        try (
                final Connection connection = database.getDataSource().getConnection();
                final PreparedStatement statement = connection.prepareStatement(
                        "UPDATE " + database.getSpawnersTable() + " " +
                                "SET location = ?, " +
                                "entitytype = ?, " +
                                "owner = ?, " +
                                "amount = ?, " +
                                "drops = ? " +
                                "WHERE id = ?")
        ) {
            statement.setString(1, Helper.serializeLocation(spawner.getLocation()));
            statement.setString(2, spawner.getEntityType().name());
            statement.setString(3, spawner.getOwner());
            statement.setDouble(4, spawner.getAmount());
            statement.setString(5, gson.toJson(spawner.getDrops()));
            statement.setInt(6, spawner.getId());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
