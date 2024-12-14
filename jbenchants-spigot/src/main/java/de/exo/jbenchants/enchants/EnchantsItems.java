package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class EnchantsItems {

    private static EnchantsItems INSTANCE;
    private EnchantsItems() {
    }
    public static EnchantsItems getInstance() {
        if (INSTANCE == null) INSTANCE = new EnchantsItems();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsLore lore = EnchantsLore.getInstance();

    public ItemStack getEnchantInformation(String name) {
        ItemStack enchInfo = new ItemStack(Material.getMaterial(api.getEnchantmentMaterial(name)));
        lore.setEnchantmentInfoMeta(enchInfo, name);
        return enchInfo;
    }
}
