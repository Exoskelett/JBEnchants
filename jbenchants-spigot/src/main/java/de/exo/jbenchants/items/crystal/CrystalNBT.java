package de.exo.jbenchants.items.crystal;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrystalNBT {
    private static CrystalNBT INSTANCE;

    public static CrystalNBT getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrystalNBT();
        return INSTANCE;
    }

    public void setCrystalNBT(ItemStack item, String rarity, int chance) {
        NBTItem nbti = new NBTItem(item);
        nbti.setString("crystal", rarity);
        nbti.setString("chance", "" + chance);
        nbti.applyNBT(item);
    }

    public void setUsedCrystalChance(Player player, String rarity, int chance) {
        NBTEntity entity = new NBTEntity((Entity)player);
        if (chance > 0) {
            entity.getPersistentDataContainer().setInteger("used_" + rarity.toLowerCase(), Integer.valueOf(chance));
        } else {
            entity.getPersistentDataContainer().removeKey("used_" + rarity.toLowerCase());
        }
    }

    public String getUsedCrystalChance(Player player) {
        NBTCompound entity = (new NBTEntity((Entity)player)).getPersistentDataContainer();
        for (String rarity : new String[] { "common", "rare", "epic", "legendary" }) {
            if (entity.hasTag("used_" + rarity))
                return rarity + "-" + rarity;
        }
        return null;
    }
}
