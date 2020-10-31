package com.github.eokasta.spawners.entities;

import com.github.eokasta.spawners.utils.Helper;
import com.github.eokasta.spawners.utils.MakeItem;
import dev.arantes.inventorymenulib.buttons.ItemButton;
import dev.arantes.inventorymenulib.menus.InventoryGUI;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Data
public class Spawner {

    private int id;
    private Location location;
    private EntityType entityType;
    private String owner;
    private double amount;
    private Map<Material, Double> drops;

    private InventoryGUI gui;

    public Spawner(int id, Location location, EntityType entityType, String owner, double amount) {
        this.id = id;
        this.location = location;
        this.entityType = entityType;
        this.owner = owner;
        this.amount = amount;
        this.drops = new HashMap<>();
    }

    public InventoryGUI updateInventory() {
        if (gui == null) {
            gui = new InventoryGUI("", 9 * 3);
            gui.setDefaultAllCancell(true);
        }

        gui.setButton(13, new ItemButton(new MakeItem(Material.MOB_SPAWNER)
                .setName("&aSpawner information")
                .addLoreList("",
                        " &7Entity type: &f" + entityType.getName(),
                        " &7Owner: &f" + owner,
                        " &7Amount: &f" + Helper.formatBalance(amount),
                        " &7Drops: &f" + (drops.isEmpty() ? "&cNo drops" : drops.get(Material.STONE)),
                        " ")
                .build())
        );

        return gui;
    }

    public void showInventory(Player player) {
        updateInventory().show(player);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
    }

}
