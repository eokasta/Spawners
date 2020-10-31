package com.github.eokasta.spawners.storage;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.Spawner;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager {

    @Getter
    private final SpawnerPlugin plugin;
    @Getter
    private final HikariDataSource dataSource;
    @Getter
    private final String spawnersTable;
    @Getter
    private final Set<Spawner> changedSpawners = new HashSet<>();
    @Getter
    private BukkitTask bukkitTask;

    public DatabaseManager(SpawnerPlugin plugin) throws SQLException {
        this.plugin = plugin;

        final ConfigurationSection sqlSettings = plugin.getSettings().getSQLSettings();
        this.spawnersTable = sqlSettings.getString("table");

        final HikariConfig config = new HikariConfig();

        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", sqlSettings.getString("host"));
        config.addDataSourceProperty("port", sqlSettings.getString("port"));
        config.addDataSourceProperty("databaseName", sqlSettings.getString("database"));
        config.addDataSourceProperty("user", sqlSettings.getString("user"));
        config.addDataSourceProperty("password", sqlSettings.getString("password"));
        config.addDataSourceProperty("autoReconnect", true);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);

        config.setMaximumPoolSize(16);
        config.setConnectionTimeout(30000);
        this.dataSource = new HikariDataSource(config);

        createTable(spawnersTable,
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "location TEXT NOT NULL UNIQUE, " +
                        "entitytype VARCHAR(24) NOT NULL, " +
                        "owner VARCHAR(16) NOT NULL, " +
                        "amount BIGINT NOT NULL, " +
                        "drops TEXT NOT NULL, " +
                        "INDEX(location)"
        );
    }

    private void createTable(String table, String values) throws SQLException {
        try (
                final Connection connection = dataSource.getConnection();
                final PreparedStatement statement = connection.prepareStatement(String.format("CREATE TABLE IF NOT EXISTS %s (%s);", table, values))) {
            statement.execute();
        }
    }
}
