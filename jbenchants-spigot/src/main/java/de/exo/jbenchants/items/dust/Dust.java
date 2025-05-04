package de.exo.jbenchants.items.dust;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Dust {
    private static Dust INSTANCE;

    public static Dust getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Dust();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final DustMeta lore = DustMeta.getInstance();

    private final DustNBT nbt = DustNBT.getInstance();

    public ItemStack getDust(String rarity) {
        return getDust(rarity, (int)Math.floor(Math.random() * 100.0D));
    }

    public ItemStack getDust(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.getMaterial(this.api.getItemMaterial("dust")));
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
        this.nbt.setDustNBT(item, rarity, chance);
        this.lore.setDustMeta(item, rarity, chance);
        return item;
    }
}
