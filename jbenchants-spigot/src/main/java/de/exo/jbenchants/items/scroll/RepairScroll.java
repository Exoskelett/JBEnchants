package de.exo.jbenchants.items.scroll;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RepairScroll {
    private static RepairScroll INSTANCE;

    public static RepairScroll getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepairScroll();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final RepairScrollMeta meta = RepairScrollMeta.getInstance();

    private final RepairScrollNBT nbt = RepairScrollNBT.getInstance();

    public ItemStack getScroll(String rarity) {
        return getScroll(rarity, (int)Math.floor(Math.random() * 100.0D));
    }

    public ItemStack getScroll(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.getMaterial(this.api.getItemMaterial("scroll")));
        if (rarity.equals("random"))
            if (Math.random() <= 0.22D) {
                rarity = "legendary";
            } else if (Math.random() <= 0.3D) {
                rarity = "epic";
            } else if (Math.random() <= 0.45D) {
                rarity = "rare";
            } else {
                rarity = "common";
            }
        this.nbt.setScrollNBT(item, rarity, chance);
        this.meta.setScrollMeta(item, rarity, chance);
        return item;
    }
}
