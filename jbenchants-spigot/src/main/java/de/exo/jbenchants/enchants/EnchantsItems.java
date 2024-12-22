package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EnchantsItems {

    private static EnchantsItems INSTANCE;
    private EnchantsItems() {
    }
    public static EnchantsItems getInstance() {
        if (INSTANCE == null) INSTANCE = new EnchantsItems();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsMeta lore = EnchantsMeta.getInstance();

    public ItemStack getEnchantInformation(String name) {
        ItemStack enchInfo = new ItemStack(Material.getMaterial(api.getEnchantmentMaterial(name)));
        lore.setEnchantmentInfoMeta(enchInfo, name);
        return enchInfo;
    }
}
