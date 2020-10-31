package com.github.eokasta.spawners;

import com.github.eokasta.spawners.commands.SpawnerCommand;
import com.github.eokasta.spawners.entities.EntityStack;
import com.github.eokasta.spawners.listeners.SpawnerBlockListeners;
import com.github.eokasta.spawners.listeners.SpawnerEntityDeathListeners;
import com.github.eokasta.spawners.listeners.SpawnerInteractListeners;
import com.github.eokasta.spawners.listeners.SpawnerSpawnListeners;
import com.github.eokasta.spawners.storage.DatabaseManager;
import com.github.eokasta.spawners.utils.Settings;
import com.github.eokasta.spawners.utils.YamlConfig;
import com.google.gson.Gson;
import dev.arantes.inventorymenulib.listeners.InventoryListener;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerPlugin extends JavaPlugin {

    @Getter
    private Settings settings;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private SpawnerManager manager;
    @Getter
    private EntityStack entityStack;

    @Getter
    public Gson gson;

    @SneakyThrows
    @Override
    public void onEnable() {
        this.gson = new Gson();

        InventoryListener.register(this);

        this.settings = new Settings(new YamlConfig("config.yml", this, true));
        this.databaseManager = new DatabaseManager(this);
        this.manager = new SpawnerManager(this);

        this.entityStack = new EntityStack(this);

        new SpawnerCommand(this);
        new SpawnerBlockListeners(this);
        new SpawnerInteractListeners(this);
        new SpawnerSpawnListeners(this);
        new SpawnerEntityDeathListeners(this);
    }

    @Override
    public void onDisable() {
        databaseManager.getDataSource().close();
    }

}
