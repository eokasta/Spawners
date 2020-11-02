package com.github.eokasta.spawners.manager;

import com.github.eokasta.nbtapi.nbt.NBTCompound;
import com.github.eokasta.nbtapi.nbt.NBTItem;
import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.dao.impl.CacheDao;
import com.github.eokasta.spawners.dao.impl.SpawnerDao;
import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.utils.Helper;
import com.github.eokasta.spawners.utils.MakeItem;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
public class SpawnerManager {

    private final SpawnerPlugin plugin;
    private final SpawnerDao spawnerDao;
    private final CacheDao cacheDao;
    private final CustomDrops customDrops;

    private BukkitTask saveTask;

    public SpawnerManager(SpawnerPlugin plugin) {
        this.plugin = plugin;
        this.spawnerDao = new SpawnerDao(plugin.getDatabaseManager());
        this.cacheDao = new CacheDao();
        this.customDrops = new CustomDrops(plugin);
        customDrops.loadMobDrops();
        customDrops.loadPriceDrops();

        initTask();
    }

    public void save(Spawner spawner) {
        CompletableFuture.runAsync(() -> spawnerDao.save(spawner)).whenComplete((v, t) -> cacheDao.save(spawner));
    }

    public void delete(Spawner spawner) {
        CompletableFuture.runAsync(() -> spawnerDao.delete(spawner)).whenComplete((v, t) -> cacheDao.delete(spawner));
    }

    public Spawner get(Location location) {
        Spawner spawner = cacheDao.get(location).orElse(null);
        if (spawner == null)
            try {
                final Spawner spawner1 = CompletableFuture.supplyAsync(() -> spawnerDao.get(location).orElse(null)).get();
                if (spawner1 != null) {
                    spawner = spawner1;
                    cacheDao.save(spawner1);
                }

            } catch (InterruptedException | ExecutionException ignored) {
            }

        return spawner;
    }

    public ItemStack getSpawnerItem(EntityType entityType, double amount) {
        /*
        TODO: add modificated spawner item
         */

        final MakeItem makeItem = new MakeItem(Material.MOB_SPAWNER);
        makeItem.setName("&aSpawner");
        makeItem.addLore("&7Entity type: &f" + entityType.getName());
        makeItem.addLore("&7Amount: &f" + Helper.formatBalance(amount));

        final NBTItem nbtItem = new NBTItem(makeItem.build());
        final NBTCompound compound = nbtItem.addCompound("spawner");
        compound.setString("entityType", entityType.name());
        compound.setDouble("amount", amount);

        return nbtItem.getItem();
    }

    public Spawner getSpawnerFromItemStack(ItemStack itemStack, boolean multiplyItemAmount) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return null;

        final NBTItem nbtItem = new NBTItem(itemStack);
        final NBTCompound compound = nbtItem.getCompound("spawner");
        if (compound == null)
            return null;

        final EntityType entityType = EntityType.valueOf(compound.getString("entityType"));
        final double amount = compound.getDouble("amount");

        return new Spawner(0, null, entityType, null, amount * (multiplyItemAmount ? itemStack.getAmount() : 1));
    }

    public Spawner getNearbySpawner(Location location, int radius, EntityType entityType, String owner) {
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    final Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.MOB_SPAWNER)
                        continue;

                    final Optional<Spawner> optionalSpawner = cacheDao.get(block.getLocation());
                    if (!optionalSpawner.isPresent())
                        continue;

                    final Spawner spawner = optionalSpawner.get();

                    if (!spawner.getOwner().equals(owner) || spawner.getEntityType() != entityType)
                        continue;

                    return spawner;
                }
            }
        }

        return null;
    }

    public void initTask() {
        this.saveTask = new BukkitRunnable() {
            @Override
            public void run() {
                final long before = System.currentTimeMillis();
                plugin.getLogger().info("Saving spawners...");

                cacheDao.getAll().forEach(spawner -> {
                    if (spawner.isModified()) {
                        spawnerDao.save(spawner);
                        spawner.setModified(false);
                    }
                });

                plugin.getLogger().info("Saved spawners in " + (System.currentTimeMillis() - before) + "ms.");
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20 * 60 * plugin.getSettings().getSaveTimerDelay());
    }

}
