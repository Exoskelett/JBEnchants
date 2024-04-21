package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantsNBT {
    API api = Main.instance.api;
    public void addEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name)) {
            if (level == 0) {
                removeEnchantment(item, name);
                return;
            } else
                setEnchantmentLevel(item, name, nbti.getInteger(name)+level);
        } else {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants")+1);
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
        }
        System.out.println("addEnchantment");
    }
    public void setEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (level == 0) {
            removeEnchantment(item, name);
            return;
        } else if (nbti.hasTag(name)) {
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
        } else {
            addEnchantmentLevel(item, name, level);
        }
        System.out.println("setEnchantmentLevel");
    }
    public void removeEnchantment(ItemStack item, String name) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name)) {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants")-1);
            nbti.removeKey(name);
            nbti.applyNBT(item);
        }
        System.out.println("removeEnchantment");
    }
    public void removeEnchantmentLevel(ItemStack item, String name, int level) {
        NBT.modify(item, JBEnchantData.class, (proxy) -> {
            for (int i = 0; i < proxy.getJBEnchants(item).size(); i++) {
                if (proxy.getJBEnchants(item).get(i).getName().equals(name)) {
                    proxy.getJBEnchants(item).get(i).removeLevel(level);
                    if (proxy.getJBEnchants(item).get(i).getLevel() < 1) {
                        removeEnchantment(item, name);
                    }
                }
            }
        });
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
}
