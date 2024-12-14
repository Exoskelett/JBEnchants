package de.exo.jbenchants.items.cleanser;

import de.exo.jbenchants.API;
import de.exo.jbenchants.items.Lore;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CleanserMeta {
    private static CleanserMeta INSTANCE;

    private CleanserMeta() {
    }

    protected static CleanserMeta getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CleanserMeta();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();

    public List<String> getCleanserLore(int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("cleanser");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(i, newLore[i].replace(":chance:", Lore.getChanceGradient(chance)+"%"));
        }
        return lore;
    }

    public void setCleanserMeta(ItemStack item, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(getCleanserLore(chance));
        meta.setDisplayName("§d" + api.getItemName("cleanser"));
        item.setItemMeta(meta);
    }

    public void setCleanserEnchantmentInfoMeta(ItemStack item, ItemStack reference, String name) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§7Rarity: "+api.getColor(api.getRarity(name))+api.getRarity(name).substring(0, 1).toUpperCase()+api.getRarity(name).substring(1));
        lore.add("§7Level: §e"+enchantsNBT.getEnchantmentLevel(reference, name)+" §8(§e"+api.getLevelCap(name)+"§8)");
        lore.add("");
        for (int i = 0; i < api.getEnchantmentLore(name).split(":nl:").length; i++) {
            lore.add("§7"+api.getEnchantmentLore(name).split(":nl:")[i]);
        }
        meta.setLore(lore);
        meta.setDisplayName(api.getColor(api.getRarity(name))+"§n"+api.getDisplayName(name));
        item.setItemMeta(meta);
    }
}
