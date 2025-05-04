package de.exo.jbenchants.items.scroll;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.items.Lore;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairScrollMeta {
    private static RepairScrollMeta INSTANCE;

    protected static RepairScrollMeta getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepairScrollMeta();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final RepairScrollHandler handler = RepairScrollHandler.getInstance();

    public List<String> getScrollLore(String rarity, int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("scroll");
        for (int i = 0; i < newLore.length; i++)
            lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                    .replace(":chance:", Lore.getChanceGradient(chance)+"%")
                    .replace(":durability:", ""+handler.getScrollDurability(rarity)));
        return lore;
    }

    public void setScrollMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getScrollLore(rarity, chance));
        meta.setDisplayName(api.getColor(rarity) + api.getItemName("scroll"));
        item.setItemMeta(meta);
    }
}
