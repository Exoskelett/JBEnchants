package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JBEnchantItems implements JBEnchantData.Items {

    API api = Main.instance.api;
    JBEnchantLore lore = Main.instance.lore;

    // Information

    @Override
    public ItemStack getEnchantInformation(String name) {
        ItemStack enchInfo = new ItemStack(Material.getMaterial(api.getEnchantmentMaterial(name)));
        lore.setEnchantmentInfoMeta(enchInfo, name);
        enchInfo.setItemMeta(enchInfo.getItemMeta());
        return enchInfo;
    }

    // Crystals

    @Override
    public ItemStack getCrystal(String rarity) {
        int chance = (int) Math.floor(Math.random()*100);
        switch (rarity) {
            case "random":
                if (Math.random() <= 0.22)
                    return getCrystal("legendary", chance);
                if (Math.random() <= 0.3)
                    return getCrystal("epic", chance);
                if (Math.random() <= 0.45)
                    return getCrystal("rare", chance);
                return getCrystal("common", chance);
            default:
                return getCrystal(rarity, chance);
        }
    }
    @Override
    public ItemStack getCrystal(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        item.getItemMeta().addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(item.getItemMeta());
        item.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
        NBTItem nbti = new NBTItem(item);
        switch (rarity) {
            case "legendary", "epic", "rare", "common":
                nbti.setString("crystal", rarity);
                nbti.applyNBT(item);
                break;
            default:
                if (Math.random() <= 0.22) {
                    nbti.setString("crystal", "legendary");
                } else if (Math.random() <= 0.3) {
                    nbti.setString("crystal", "epic");
                } else if (Math.random() <= 0.45) {
                    nbti.setString("crystal", "rare");
                } else
                    nbti.setString("crystal", "common");
        }
        nbti.setString("chance", ""+chance);
        nbti.applyNBT(item);
        lore.setCrystalMeta(item, rarity, chance);
        return item;
    }
    @Override
    public ItemStack getMysteryCrystal(String rarity, int low, int high) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        item.getItemMeta().addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(item.getItemMeta());
        item.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
        NBTItem nbti = new NBTItem(item);
        switch (rarity) {
            case "legendary", "epic", "rare", "common":
                nbti.setString("crystal", rarity);
                nbti.setString("chance", low+"-"+high);
                break;
            default:
                if (Math.random() <= 0.22)
                    return getMysteryCrystal("legendary", low, high);
                if (Math.random() <= 0.3)
                    return getMysteryCrystal("epic", low, high);
                if (Math.random() <= 0.45)
                    return getMysteryCrystal("rare", low, high);
                return getMysteryCrystal("common", low, high);
        }
        nbti.applyNBT(item);
        lore.setMysteryCrystalMeta(item, rarity, low, high);
        return item;
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
