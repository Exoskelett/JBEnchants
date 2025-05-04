package de.exo.jbenchants.items.cleanser;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CleanserNBT {
    private static CleanserNBT INSTANCE;

    public static CleanserNBT getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CleanserNBT();
        return INSTANCE;
    }

    public void setCleanserNBT(ItemStack item, int chance) {
        NBTItem nbti = new NBTItem(item);
        nbti.setInteger("cleanser", Integer.valueOf(chance));
        nbti.applyNBT(item);
    }

    public void setUsedCleanserChance(Player player, int chance) {
        NBTEntity entity = new NBTEntity((Entity)player);
        if (chance >= 0) {
            entity.getPersistentDataContainer().setInteger("used_cleanser", Integer.valueOf(chance));
        } else {
            entity.getPersistentDataContainer().removeKey("used_cleanser");
        }
    }

    public int getUsedCleanserChance(Player player) {
        NBTCompound entity = (new NBTEntity((Entity)player)).getPersistentDataContainer();
        if (entity.hasTag("used_cleanser"))
            return entity.getInteger("used_cleanser").intValue();
        return -1;
    }
}
