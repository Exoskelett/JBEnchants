package de.exo.jbenchants.items.mystery_crystal;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MysteryCrystal {
    private static MysteryCrystal INSTANCE;

    private MysteryCrystal() {
    }

    public static MysteryCrystal getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MysteryCrystal();
        return INSTANCE;
    }

    API api = Main.getAPI();
    private final MysteryCrystalNBT nbt = MysteryCrystalNBT.getInstance();
    private final MysteryCrystalMeta meta = MysteryCrystalMeta.getInstance();

    public ItemStack getMysteryCrystal(String rarity, int low, int high) {
        ItemStack item = new ItemStack(Material.getMaterial(api.getItemMaterial("mysteryCrystal")));
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
        nbt.setMysteryCrystalNBT(item, rarity, low, high);
        meta.setMysteryCrystalMeta(item, rarity, low, high);
        return item;
    }
}
