package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class JBEnchantItems implements JBEnchantData.Items {

    private static JBEnchantItems INSTANCE;
    private JBEnchantItems() {
    }
    public static JBEnchantItems getInstance() {
        if (INSTANCE == null) INSTANCE = new JBEnchantItems();
        return INSTANCE;
    }

    API api = Main.getAPI();
    JBEnchantLore lore = JBEnchantLore.getInstance();

    // Information

    @Override
    public ItemStack getEnchantInformation(String name) {
        ItemStack enchInfo = new ItemStack(Material.getMaterial(api.getEnchantmentMaterial(name)));
        if (name == "regenerate") {
            PotionMeta enchMeta = (PotionMeta) enchInfo.getItemMeta();
            enchMeta.setBasePotionType(PotionType.LONG_REGENERATION);
            enchInfo.setItemMeta(enchMeta);
        }
        lore.setEnchantmentInfoMeta(enchInfo, name);
        enchInfo.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
        enchInfo.setItemMeta(enchInfo.getItemMeta());
        return enchInfo;
    }

    @Override
    public ItemStack getCleanserEnchantInformation(ItemStack item, String name) {
        ItemStack enchInfo = new ItemStack(Material.getMaterial(api.getEnchantmentMaterial(name)));
        lore.setCleanserEnchantmentInfoMeta(enchInfo, item, name);
        enchInfo.setItemMeta(enchInfo.getItemMeta());
        enchInfo.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);
        return enchInfo;
    }

    // Dust

    @Override
    public ItemStack getDust(String rarity) {
        int chance = (int) Math.floor(Math.random()*100);
        switch (rarity) {
            case "random":
                if (Math.random() <= 0.22)
                    return getDust("legendary", chance);
                if (Math.random() <= 0.3)
                    return getDust("epic", chance);
                if (Math.random() <= 0.45)
                    return getDust("rare", chance);
                return getDust("common", chance);
            default:
                return getDust(rarity, chance);
        }
    }

    @Override
    public ItemStack getDust(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.GUNPOWDER);
        NBTItem nbti = new NBTItem(item);
        switch (rarity) {
            case "legendary", "epic", "rare", "common":
                nbti.setString("dust", rarity);
                nbti.applyNBT(item);
                break;
            default:
                if (Math.random() <= 0.22) {
                    nbti.setString("dust", "legendary");
                } else if (Math.random() <= 0.3) {
                    nbti.setString("dust", "epic");
                } else if (Math.random() <= 0.45) {
                    nbti.setString("dust", "rare");
                } else
                    nbti.setString("dust", "common");
        }
        nbti.setInteger("chance", chance);
        nbti.applyNBT(item);
        lore.setDustMeta(item, rarity, chance);
        return item;
    }

    // Cleanser

    @Override
    public ItemStack getCleanser() {
        int chance = (int) Math.floor(Math.random()*100);
        return getCleanser(chance);
    }

    @Override
    public ItemStack getCleanser(int chance) {
        ItemStack item = new ItemStack(Material.SUGAR);
        item.getItemMeta().addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(item.getItemMeta());
        item.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
        NBTItem nbti = new NBTItem(item);
        nbti.setInteger("cleanser", chance);
        nbti.applyNBT(item);
        lore.setCleanserMeta(item, chance);
        return item;
    }

    // Scroll

    @Override
    public ItemStack getScroll(String rarity) {
        int chance = (int) Math.floor(Math.random()*100);
        switch (rarity) {
            case "random":
                if (Math.random() <= 0.22)
                    return getScroll("legendary", chance);
                if (Math.random() <= 0.3)
                    return getScroll("epic", chance);
                if (Math.random() <= 0.45)
                    return getScroll("rare", chance);
                return getScroll("common", chance);
            default:
                return getScroll(rarity, chance);
        }
    }

    @Override
    public ItemStack getScroll(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.PAPER);
        NBTItem nbti = new NBTItem(item);
        switch (rarity) {
            case "legendary", "epic", "rare", "common":
                nbti.setString("scroll", rarity);
                nbti.applyNBT(item);
                break;
            default:
                if (Math.random() <= 0.22) {
                    nbti.setString("scroll", "legendary");
                } else if (Math.random() <= 0.3) {
                    nbti.setString("scroll", "epic");
                } else if (Math.random() <= 0.45) {
                    nbti.setString("scroll", "rare");
                } else
                    nbti.setString("scroll", "common");
        }
        nbti.setInteger("chance", chance);
        nbti.applyNBT(item);
        lore.setScrollMeta(item, rarity, chance);
        return item;
    }
}
