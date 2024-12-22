package de.exo.jbenchants.items.crystal;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Crystal {
    private static Crystal INSTANCE;

    private Crystal() {
    }

    public static Crystal getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Crystal();
        return INSTANCE;
    }

    API api = Main.getAPI();
    CrystalMeta meta = CrystalMeta.getInstance();
    CrystalNBT nbt = CrystalNBT.getInstance();

    public ItemStack getCrystal(String rarity) {
        return getCrystal(rarity, (int) Math.floor(Math.random() * 100));
    }

    public ItemStack getCrystal(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.getMaterial(api.getItemMaterial("crystal")));
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
        nbt.setCrystalNBT(item, rarity, chance);
        meta.setCrystalMeta(item, rarity, chance);
        return item;
    }

}
