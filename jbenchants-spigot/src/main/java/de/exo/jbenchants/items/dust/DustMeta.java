package de.exo.jbenchants.items.dust;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.items.Lore;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DustMeta {
    private static DustMeta INSTANCE;

    protected static DustMeta getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DustMeta();
        return INSTANCE;
    }

    API api = Main.getAPI();

    public List<String> getDustLore(String rarity, int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("dust");
        for (int i = 0; i < newLore.length; i++)
            lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                    .replace(":chance:", Lore.getChanceGradient(chance)+"%"));
        return lore;
    }

    public void setDustMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getDustLore(rarity, chance));
        meta.setDisplayName(api.getColor(rarity) + api.getItemName("dust"));
        item.setItemMeta(meta);
    }
}
