package com.github.eokasta.spawners.entities;

import com.github.eokasta.spawners.SpawnerPlugin;
import com.github.eokasta.spawners.utils.Helper;
import com.github.eokasta.spawners.utils.MakeItem;
import dev.arantes.inventorymenulib.PaginatedGUIBuilder;
import dev.arantes.inventorymenulib.buttons.ItemButton;
import dev.arantes.inventorymenulib.menus.InventoryGUI;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class SpawnerDropsInventory {

    private static InventoryGUI emptyDrops = null;

    private final SpawnerPlugin plugin;
    private final Spawner spawner;

    private PaginatedGUIBuilder builder;

    public void updateInventory() {
        if (emptyDrops == null) {
            emptyDrops = new InventoryGUI("", 9*3);
            emptyDrops.setDefaultAllCancell(true);
            emptyDrops.setButton(13, new ItemButton(new MakeItem(Material.WEB).setName("&cIt's so empty...").build()));
        }

        if (spawner.getDrops().isEmpty())
            return;

        if (builder == null) {
            this.builder = new PaginatedGUIBuilder("", "xxxxxxxxx<#######>");
            builder.setDefaultAllCancell(true);
            builder.setPreviousPageItem(Material.ARROW, 1, Helper.format("&aPrevious page"));
            builder.setNextPageItem(Material.ARROW, 1, Helper.format("&aNext page"));
        }

        final List<ItemButton> content = new ArrayList<>();

        new HashMap<>(spawner.getDrops()).forEach((material, amount) -> {

            final double unityPrice = plugin.getManager().getCustomDrops().getPrice(material);
            final double totalPrice = unityPrice * amount;

            final MakeItem makeItem = new MakeItem(material);
            makeItem.setName("&a" + material.name());
            makeItem.addLoreList(
                    "",
                    " &7Amount: &f" + Helper.formatBalance(amount),
                    " &7Price per unity: &2$ &f" + Helper.formatBalance(unityPrice),
                    " &7Total price: &2$ &f" + Helper.formatBalance(totalPrice),
                    "",
                    "&aClick to sell."
            );

            content.add(new ItemButton(makeItem.build()).setDefaultAction(event -> {
                final Player player = (Player) event.getWhoClicked();
                if (plugin.getManager().get(spawner.getLocation()) == null) {
                    player.sendMessage(Helper.format("&cThis spawner is unavailable")); /* TODO: change message */
                    player.closeInventory();
                    return;
                }

                if (!spawner.getOwner().equals(event.getWhoClicked().getName())) {
                    player.sendMessage(Helper.format("&cNo permission to interact with this spawner.")); /* TODO: change message */
                    return;
                }

                if (!spawner.getDrops().containsKey(material)) {
                    updateInventory();
                    show(player);
                    return;
                }

                plugin.getEconomy().depositPlayer(player, totalPrice);
                spawner.getDrops().remove(material);
                player.sendMessage(Helper.format("&aYou sold x%s &f%s &afrom your spawner for &f%s&a coins.", Helper.formatBalance(amount), material.name(), Helper.formatBalance(totalPrice))); /* TODO: change message */
                plugin.getManager().getModifiedDao().save(spawner);

                updateInventory();
                show(player);
            }));
        });

        builder.setContent(content);
    }

    public void show(Player player) {
        updateInventory();
        if (spawner.getDrops().isEmpty())
            emptyDrops.show(player);
        else
            builder.build().show(player);

    }

}
