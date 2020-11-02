package com.github.eokasta.spawners.manager;

import com.github.eokasta.spawners.SpawnerPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomDrops {

    private final SpawnerPlugin plugin;

    @Getter
    private final Map<EntityType, List<Material>> mobDrops = new HashMap<>();
    @Getter
    private final Map<Material, Double> materialPrices = new HashMap<>();

    public void loadMobDrops() {
        final ConfigurationSection section = plugin.getSettings().getConfig().getConfig().getConfigurationSection("mobs");
        if (section == null)
            return;

        for (String mob : section.getKeys(false)) {
            final List<Material> materials = new ArrayList<>();
            for (String material : section.getStringList(mob + ".drops"))
                materials.add(Material.valueOf(material));

            if (!materials.isEmpty())
                mobDrops.put(EntityType.valueOf(mob), materials);
        }

    }

    public void loadPriceDrops() {
        final ConfigurationSection section = plugin.getSettings().getConfig().getConfig().getConfigurationSection("drops");
        if (section == null)
            return;

        for (String material : section.getKeys(false))
            materialPrices.put(Material.valueOf(material), section.getDouble(material));
    }

    public List<Material> getDrops(EntityType entityType) {
        return mobDrops.get(entityType);
    }

    public double getPrice(Material material) {
        return materialPrices.getOrDefault(material, 0D);
    }

}
