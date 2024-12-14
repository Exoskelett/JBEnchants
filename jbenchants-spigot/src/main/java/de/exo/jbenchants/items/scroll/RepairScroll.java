package de.exo.jbenchants.items.scroll;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RepairScroll {
    private static RepairScroll INSTANCE;

    private RepairScroll() {
    }

    public static RepairScroll getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepairScroll();
        return INSTANCE;
    }

    API api = Main.getAPI();
    private final RepairScrollMeta meta = RepairScrollMeta.getInstance();
    private final RepairScrollNBT nbt = RepairScrollNBT.getInstance();

    public ItemStack getScroll(String rarity) {
        return getScroll(rarity, (int) Math.floor(Math.random()*100));
    }

    public ItemStack getScroll(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.getMaterial(api.getItemMaterial("scroll")));
        if (rarity.equals("random")) {
            if (Math.random() <= 0.22) {
                rarity = "legendary";
            } else if (Math.random() <= 0.3) {
                rarity = "epic";
            } else if (Math.random() <= 0.45) {
                rarity = "rare";
            } else
                rarity = "common";
        }
        nbt.setScrollNBT(item, rarity, chance);
        meta.setScrollMeta(item, rarity, chance);
        return item;
    }
}
