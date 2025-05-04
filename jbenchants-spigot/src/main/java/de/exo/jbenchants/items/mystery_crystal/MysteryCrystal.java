package de.exo.jbenchants.items.mystery_crystal;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MysteryCrystal {
    private static MysteryCrystal INSTANCE;

    public static MysteryCrystal getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MysteryCrystal();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final MysteryCrystalNBT nbt = MysteryCrystalNBT.getInstance();

    private final MysteryCrystalMeta meta = MysteryCrystalMeta.getInstance();

    public ItemStack getMysteryCrystal(String rarity, int low, int high) {
        ItemStack item = new ItemStack(Material.getMaterial(this.api.getItemMaterial("mysteryCrystal")));
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
        this.nbt.setMysteryCrystalNBT(item, rarity, low, high);
        this.meta.setMysteryCrystalMeta(item, rarity, low, high);
        return item;
    }
}
