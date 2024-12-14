package de.exo.jbenchants.items.mystery_crystal;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MysteryCrystalMeta {
    private static MysteryCrystalMeta INSTANCE;

    private MysteryCrystalMeta() {
    }

    public static MysteryCrystalMeta getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MysteryCrystalMeta();
        return INSTANCE;
    }

    API api = Main.getAPI();

    public List<String> getMysteryCrystalLore(String rarity, int low, int high) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("mysteryCrystal");
        for (int i = 0; i < newLore.length; i++) {
            if (low == 0 && high == 100) {
                lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                        .replace(":chance:", "§arandom"));
            } else {
                lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                        .replace(":chance:", "§a"+low+"-"+high+"%"));
            }
        }
        return lore;
    }

    public void setMysteryCrystalMeta(ItemStack item, String rarity, int low, int high) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.setLore(getMysteryCrystalLore(rarity, low, high));
        meta.setDisplayName(api.getItemName("mysteryCrystal").replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1)));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }
}
