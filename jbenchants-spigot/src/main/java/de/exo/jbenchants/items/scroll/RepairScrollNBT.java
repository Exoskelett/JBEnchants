package de.exo.jbenchants.items.scroll;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class RepairScrollNBT {
    private static RepairScrollNBT INSTANCE;

    protected static RepairScrollNBT getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepairScrollNBT();
        return INSTANCE;
    }

    public void setScrollNBT(ItemStack item, String rarity, int chance) {
        NBTItem nbti = new NBTItem(item);
        nbti.setString("scroll", rarity);
        nbti.setInteger("chance", Integer.valueOf(chance));
        nbti.applyNBT(item);
    }

    public void repairTool(ItemStack tool, int repairValue) {
        NBTItem nbti = new NBTItem(tool);
        if (!nbti.hasTag("Damage"))
            return;
        int newDamage = nbti.getInteger("Damage").intValue() - repairValue;
        if (newDamage <= 0) {
            nbti.removeKey("Damage");
        } else {
            nbti.setInteger("Damage", Integer.valueOf(newDamage));
        }
        nbti.applyNBT(tool);
    }
}
