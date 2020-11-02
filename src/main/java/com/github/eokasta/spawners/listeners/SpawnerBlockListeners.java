package com.github.eokasta.spawners.listeners;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.utils.Helper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SpawnerBlockListeners implements Listener {

    private final SpawnerPlugin plugin;

    public SpawnerBlockListeners(SpawnerPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block blockPlaced = event.getBlockPlaced();

        final ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getType() == Material.AIR)
            return;

        if (!plugin.getSettings().getAllowedWorlds().contains(blockPlaced.getWorld().getName()))
            return;

        final Spawner spawner = plugin.getManager().getSpawnerFromItemStack(itemInHand, true);
        if (spawner == null)
            return;

        final Spawner nearbySpawner = plugin.getManager().getNearbySpawner(blockPlaced.getLocation(), 10, spawner.getEntityType(), player.getName());

        if (nearbySpawner != null
                && nearbySpawner.getOwner().equals(player.getName())
                && nearbySpawner.getEntityType() == spawner.getEntityType()) {

            nearbySpawner.setAmount(nearbySpawner.getAmount() + spawner.getAmount());
            spawner.setModified(true);

            player.sendMessage(Helper.format("&aStacked +%s spawners. Total: %s",
                    Helper.formatBalance(spawner.getAmount()),
                    Helper.formatBalance(nearbySpawner.getAmount()))
            ); /* TODO: change message */

            blockPlaced.setType(Material.AIR);
            blockPlaced.getState().update();
            player.setItemInHand(null);
            return;
        }

        spawner.setLocation(blockPlaced.getLocation());
        spawner.setOwner(player.getName());
        spawner.setDrops(new HashMap<>());

        plugin.getManager().save(spawner);

        player.sendMessage(Helper.format("&aPlaced %s spawners.", Helper.formatBalance(spawner.getAmount()))); /* TODO: change message */
        player.playSound(spawner.getLocation(), Sound.SUCCESSFUL_HIT, 1F, 1F);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            final CreatureSpawner state = (CreatureSpawner) spawner.getLocation().getBlock().getState();
            state.setSpawnedType(spawner.getEntityType());
            state.update();
        }, 2);

        player.setItemInHand(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!plugin.getSettings().getAllowedWorlds().contains(block.getWorld().getName()))
            return;

        final Spawner spawner = plugin.getManager().get(block.getLocation());
        if (spawner == null)
            return;

        if (spawner.getOwner().equals(player.getName()) || (player.hasPermission("spawners.bypass.break") && player.isSneaking())) {
            player.sendMessage(Helper.format("&cRemoved %s spawners.", Helper.formatBalance(spawner.getAmount()))); /* TODO: change message */

            final ItemStack spawnerItem = plugin.getManager().getSpawnerItem(spawner.getEntityType(), spawner.getAmount());
            player.getInventory().addItem(spawnerItem);

            plugin.getManager().delete(spawner);
            return;
        }

        player.sendMessage(Helper.format("&cNo permission to remove this spawner.")); /* TODO: change message */
        event.setCancelled(true);
    }

}
