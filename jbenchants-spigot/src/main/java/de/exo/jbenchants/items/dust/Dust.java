package de.exo.jbenchants.items.dust;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Dust {
    private static Dust INSTANCE;

    private Dust() {
    }

    public static Dust getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Dust();
        return INSTANCE;
    }

    API api = Main.getAPI();
    private final DustMeta lore = DustMeta.getInstance();
    private final DustNBT nbt = DustNBT.getInstance();

    public ItemStack getDust(String rarity) {
        return getDust(rarity, (int) Math.floor(Math.random()*100));
    }

    public ItemStack getDust(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.getMaterial(api.getItemMaterial("dust")));
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
        nbt.setDustNBT(item, rarity, chance);
        lore.setDustMeta(item, rarity, chance);
        return item;
    }
}
