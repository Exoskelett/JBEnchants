package de.exo.jbenchants.items.mystery_crystal;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class MysteryCrystalNBT {
    private static MysteryCrystalNBT INSTANCE;

    public static MysteryCrystalNBT getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MysteryCrystalNBT();
        return INSTANCE;
    }

    public void setMysteryCrystalNBT(ItemStack item, String rarity, int low, int high) {
        NBTItem nbti = new NBTItem(item);
        nbti.setString("crystal", rarity);
        nbti.setString("chance", "" + low + "-" + low);
        nbti.applyNBT(item);
    }
}
