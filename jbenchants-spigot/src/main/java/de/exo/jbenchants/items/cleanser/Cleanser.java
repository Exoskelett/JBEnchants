package de.exo.jbenchants.items.cleanser;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Cleanser {
    private static Cleanser INSTANCE;

    public static Cleanser getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Cleanser();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final CleanserMeta meta = CleanserMeta.getInstance();

    private final CleanserNBT nbt = CleanserNBT.getInstance();

    public ItemStack getCleanser() {
        return getCleanser((int)Math.floor(Math.random() * 100.0D));
    }

    public ItemStack getCleanser(int chance) {
        ItemStack item = new ItemStack(Material.getMaterial(this.api.getItemMaterial("cleanser")));
        this.nbt.setCleanserNBT(item, chance);
        this.meta.setCleanserMeta(item, chance);
        return item;
    }

    public ItemStack getCleanserEnchantInformation(ItemStack item, String name) {
        ItemStack enchInfo = new ItemStack(Material.getMaterial(this.api.getEnchantmentMaterial(name)));
        this.meta.setCleanserEnchantmentInfoMeta(enchInfo, item, name);
        return enchInfo;
    }
}
