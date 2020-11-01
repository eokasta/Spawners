package com.github.eokasta.spawners.entities;

import com.github.eokasta.spawners.SpawnerPlugin;
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

    private final SpawnerPlugin plugin;
    private int id;
    private Location location;
    private EntityType entityType;
    private String owner;
    private double amount;
    private Map<Material, Double> drops;

    private InventoryGUI gui;

    public Spawner(SpawnerPlugin plugin, int id, Location location, EntityType entityType, String owner, double amount) {
        this.plugin = plugin;
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
                        " ")
                .build())
        );

        final MakeItem makeItem = new MakeItem(Material.EMERALD);
        makeItem.setName("&aDrops");
        if (drops.isEmpty())
            makeItem.addLore("&cNo drops.");
        else
            drops.forEach((material, amount) -> makeItem.addLore("&a" + material.name() + ": &f" + Helper.formatBalance(amount)));

        gui.setButton(15, new ItemButton(makeItem.build()).setDefaultAction(event -> {
            if (drops.isEmpty())
                return;

            new HashMap<>(drops).forEach((material, amount) -> {
                event.getWhoClicked().sendMessage(Helper.format("&aSell x" + Helper.formatBalance(amount) + " " +
                        material.name() + ": &f" + Helper.formatBalance(plugin.getManager().getCustomDrops().getPrice(material))));
                drops.remove(material);
                updateInventory();
                plugin.getManager().getModifiedDao().save(this);
            });
        }));

        return gui;
    }

    public void showInventory(Player player) {
        updateInventory().show(player);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
    }

}
