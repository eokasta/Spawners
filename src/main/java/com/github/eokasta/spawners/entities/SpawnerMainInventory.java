package com.github.eokasta.spawners.entities;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.utils.Helper;
import com.github.eokasta.spawners.utils.MakeItem;
import dev.arantes.inventorymenulib.buttons.ItemButton;
import dev.arantes.inventorymenulib.menus.InventoryGUI;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Data
public class SpawnerMainInventory {
    
    private final SpawnerPlugin plugin;
    private final Spawner spawner;
    
    private InventoryGUI inventory;
    
    public void updateInventory() {
        if (inventory == null) {
            inventory = new InventoryGUI("", 9 * 3);
            inventory.setDefaultAllCancell(true);
        }

        inventory.setButton(13, new ItemButton(new MakeItem(Material.MOB_SPAWNER)
                .setName("&aSpawner information")
                .addLoreList("",
                        " &7Entity type: &f" + spawner.getEntityType().getName(),
                        " &7Owner: &f" + spawner.getOwner(),
                        " &7Amount: &f" + Helper.formatBalance(spawner.getAmount()),
                        " ")
                .build())
        );

        final MakeItem makeItem = new MakeItem(Material.EMERALD);
        makeItem.setName("&aDrops");

        inventory.setButton(15, new ItemButton(makeItem.build()).setDefaultAction(event -> new SpawnerDropsInventory(plugin, spawner).show((Player) event.getWhoClicked())));
    }

    public void showInventory(Player player) {
        updateInventory();
        inventory.show(player);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1F, 1F);
    }
    
}
