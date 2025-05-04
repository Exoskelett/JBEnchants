package de.exo.jbenchants.items.dust;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class DustNBT {
    private static DustNBT INSTANCE;

    protected static DustNBT getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DustNBT();
        return INSTANCE;
    }

    API api = Main.getAPI();

    public void setDustNBT(ItemStack item, String rarity, int chance) {
        NBTItem nbti = new NBTItem(item);
        nbti.setString("dust", rarity);
        nbti.setInteger("chance", Integer.valueOf(chance));
        nbti.applyNBT(item);
    }
}
