package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JBEnchantNBT implements JBEnchantData.NBT {

    API api = Main.instance.api;

    public void addEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (getEnchantmentLevel(item, name)+level <= 0) {
            removeEnchantment(item, name);
        } else if (!nbti.hasTag(name)) {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants")+1);
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
        } else  {
            setEnchantmentLevel(item, name, nbti.getInteger(name)+level);
        }
    }
    public void setEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (level <= 0) {
            removeEnchantment(item, name);
        } else if (nbti.hasTag(name)) {
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
        } else {
            addEnchantmentLevel(item, name, level);
        }
    }
    public void removeEnchantment(ItemStack item, String name) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name)) {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants")-1);
            nbti.removeKey(name);
            nbti.applyNBT(item);
        }
    }
    public int getEnchantmentLevel(ItemStack item, String name) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name)) {
            return nbti.getInteger(name);
        }
        return 0;
    }
    public List<String> getEnchants(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> enchants = new ArrayList<>();
        if (nbti.hasTag("jbenchants")) {
            int i = 0;
            for (int k = 0; k < api.getEnchantments().size(); k++) {
                if (nbti.hasTag(api.getEnchantments().get(k))) {
                    enchants.add(api.getEnchantments().get(k));
                    i++;
                }
                if (i == nbti.getInteger("jbenchants")) break;
            }
            return enchants;
        } else
            return null;
    }
    public boolean checkLevelCap(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        int cap = api.getLevelCap(name);
        if (nbti.hasTag(name)) {
            return cap - nbti.getInteger(name) >= level;
        } else return level <= cap;
    }
    public String getCategory(ItemStack item) {
        String material = item.getType().toString();
        if (material.endsWith("_PICKAXE") || material.endsWith("_SHOVEL")) {
            return "tool";
        } else if (material.endsWith("_AXE")) {
            return "axe";
        }else if (material.endsWith("_SWORD")) {
            return "weapon";
        } else if (material.endsWith("_HELMET")) {
            return "helmet";
        } else if (material.endsWith("_CHESTPLATE")) {
            return "chestplate";
        } else if (material.endsWith("_LEGGINGS")) {
            return "leggings";
        } else if (material.endsWith("_BOOTS")) {
            return "boots";
        }else if (material.equals("FISHING_ROD")) {
            return "fishing";
        } else if (material.equals("BOW")) {
            return "bow";
        } else
            return null;
    }
}
