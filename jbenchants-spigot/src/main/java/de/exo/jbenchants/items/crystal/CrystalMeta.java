package de.exo.jbenchants.items.crystal;

import de.exo.jbenchants.API;
import de.exo.jbenchants.items.Lore;
import de.exo.jbenchants.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CrystalMeta {
    private static CrystalMeta INSTANCE;

    private CrystalMeta() {
    }

    public static CrystalMeta getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrystalMeta();
        return INSTANCE;
    }

    API api = Main.getAPI();

    public List<String> getCrystalLore(String rarity, int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("crystal");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                    .replace(":chance:", Lore.getChanceGradient(chance)+"%"));
        }
        return lore;
    }

    public void setCrystalMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getCrystalLore(rarity, chance));
        meta.setDisplayName(api.getItemName("crystal").replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1)));
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }
}
