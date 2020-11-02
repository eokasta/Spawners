package com.github.eokasta.spawners.listeners;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.entities.Spawner;
import com.github.eokasta.spawners.entities.SpawnerMainInventory;
import com.github.eokasta.spawners.utils.Helper;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpawnerInteractListeners implements Listener {

    private final SpawnerPlugin plugin;

    public SpawnerInteractListeners(SpawnerPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractMenu(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final Block block = event.getClickedBlock();

        if (!plugin.getSettings().getAllowedWorlds().contains(block.getWorld().getName()))
            return;

        final Spawner spawner = plugin.getManager().get(block.getLocation());
        if (spawner == null)
            return;

        if (!spawner.getOwner().equals(player.getName())) {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1F, 1F);
            player.sendMessage(Helper.format("&cNo permission to interact with this spawner.")); /* TODO: change message */
            return;
        }

        new SpawnerMainInventory(plugin, spawner).showInventory(player);
    }
}
