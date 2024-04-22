package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JBEnchantLore implements JBEnchantData.Lore {
    API api = Main.instance.api;
    JBEnchantData.NBT nbt = Main.instance.nbt;

    @Override
    public List<Integer> getEnchantmentLoreSlots(ItemStack item) {
        List<Integer> enchants = new ArrayList<>();
        List<String> lore = item.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++) {
            String possibleEnch = lore.get(i).split(" ")[0];
            if (api.exists(possibleEnch)) {
                enchants.add(i);
            }
        }
        return enchants;
    }

    @Override
    public void updateLore(ItemStack item) {
        List<String> enchants = nbt.getEnchants(item);
        List<Integer> loreSlots = getEnchantmentLoreSlots(item);
        List<String> lore = item.getItemMeta().getLore();
        for (int i = 0; i < loreSlots.size(); i++) {
            lore.remove(loreSlots.get(i));
        }
        for (int i = 0; i < enchants.size(); i++) {
            lore.addFirst(api.getDisplayName(enchants.get(enchants.size()-i))
                    +" - "
                    +nbt.getEnchantmentLevel(item, enchants.get(enchants.size()-i)));
        }
        item.getItemMeta().setLore(lore);
        item.setItemMeta(item.getItemMeta());
    }
}
