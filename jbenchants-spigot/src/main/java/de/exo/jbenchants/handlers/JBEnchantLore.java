package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JBEnchantLore implements JBEnchantData.Lore {

    API api = Main.instance.api;
    Configuration config = Main.instance.getConfig();
    JBEnchantNBT nbt = Main.instance.nbt;

    @Override
    public List<Integer> getEnchantmentLoreSlots(ItemStack item) {
        List<Integer> enchants = new ArrayList<>();
        List<String> lore = item.getLore();
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                if (config.getBoolean("use_prefix")) {
                    if (api.check(api.getName(lore.get(i).split(" ")[0]+" "+lore.get(i).split(" ")[1]))) {
                        enchants.add(i);
                    }
                } else {
                    if (api.check(api.getName(lore.get(i).split(" ")[0]))) {
                        enchants.add(i);
                    }
                }
            }
            return enchants;
        }
        return null;
    }

    @Override
    public void deleteUnusedEnchants(ItemStack item) {
        List<String> lore = item.getLore();
        if (lore != null) {
            String displayName;
            for (int i = 0; i < lore.size(); i++) {
                if (config.getBoolean("use_prefix")) {
                    displayName = lore.get(i).split(" ")[1];
                } else {
                    displayName = lore.get(i).split(" ")[0];
                }
                if (api.check(api.getName(displayName)) && !nbt.getEnchants(item).contains(api.getName(displayName))) {
                    lore.remove(i);
                }
            }
            if (lore.isEmpty()) {
                item.setLore(null);
            } else
                item.setLore(lore);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void deleteAllEnchants(ItemStack item) {
        List<String> lore = item.getLore();
        if (lore != null) {
            for (int i = lore.size()-1; i >= 0; i--) {
                List<String> string = List.of(lore.get(i).split(" "));
                try {
                    int nullCheck = Integer.parseInt(string.get(string.size()-1));
                    lore.remove(i);
                    continue;
                } catch (NumberFormatException ignored) {
                }
                if (config.getBoolean("use_prefix")) {
                    if (string.size() == 2) {
                        if (api.check(api.getName(string.get(1)))) {
                            lore.remove(i);
                        }
                    }
                } else {
                    if (string.size() == 1) {
                        if (api.check(api.getName(string.get(0)))) {
                            lore.remove(i);
                        }
                    }
                }
            }
            if (lore.isEmpty()) {
                item.setLore(null);
            } else
                item.setLore(lore);
            item.setItemMeta(item.getItemMeta());
        }
    }

    @Override
    public void updateLore(ItemStack item) {
        deleteAllEnchants(item);
        List<String> enchants = nbt.getEnchants(item);
        List<Integer> loreSlots = getEnchantmentLoreSlots(item);
        List<String> lore = item.getLore();
        if (lore != null) {
            lore.addAll(0, sortEnchantsWithLevel(enchants, item));
            item.setLore(lore);
        } else {
            item.setLore(sortEnchantsWithLevel(enchants, item));
        }
        item.setItemMeta(item.getItemMeta());
    }

    @Override
    public List<String> sortEnchants(List<String> list) {
        List<String> legendary = new ArrayList<>();
        List<String> epic = new ArrayList<>();
        List<String> rare = new ArrayList<>();
        List<String> common = new ArrayList<>();
        List<String> special = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            switch (api.getRarity(list.get(i))) {
                case "legendary":
                    legendary.add(api.getEnchantmentColor("legendary")+api.getDisplayName(list.get(i)));
                    break;
                case "epic":
                    epic.add(api.getEnchantmentColor("epic")+api.getDisplayName(list.get(i)));
                    break;
                case "rare":
                    rare.add(api.getEnchantmentColor("rare")+api.getDisplayName(list.get(i)));
                    break;
                case "common":
                    common.add(api.getEnchantmentColor("common")+api.getDisplayName(list.get(i)));
                    break;
                case "special":
                    special.add(api.getEnchantmentColor("special")+api.getDisplayName(list.get(i)));
                    break;
            }
        }
        Collections.sort(legendary); Collections.sort(epic); Collections.sort(rare); Collections.sort(common); Collections.sort(special);
        list.clear();
        list.addAll(legendary);
        list.addAll(epic);
        list.addAll(rare);
        list.addAll(common);
        list.addAll(special);
        return list;
    }

    @Override
    public List<String> sortEnchantsWithLevel(List<String> list, ItemStack item) {
        List<String> legendary = new ArrayList<>();
        List<String> epic = new ArrayList<>();
        List<String> rare = new ArrayList<>();
        List<String> common = new ArrayList<>();
        List<String> special = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            switch (api.getRarity(list.get(i))) {
                case "legendary":
                    if (config.getBoolean("use_prefix")) {
                        legendary.add(api.getEnchantmentPrefix("legendary")+" "+api.getEnchantmentColor("legendary")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        legendary.add(api.getEnchantmentColor("legendary")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "epic":
                    if (config.getBoolean("use_prefix")) {
                        epic.add(api.getEnchantmentPrefix("epic")+" "+api.getEnchantmentColor("epic")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        epic.add(api.getEnchantmentColor("epic")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "rare":
                    if (config.getBoolean("use_prefix")) {
                        rare.add(api.getEnchantmentPrefix("rare")+" "+api.getEnchantmentColor("rare")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        rare.add(api.getEnchantmentColor("rare")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "common":
                    if (config.getBoolean("use_prefix")) {
                        common.add(api.getEnchantmentPrefix("common")+" "+api.getEnchantmentColor("common")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        common.add(api.getEnchantmentColor("common")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "special":
                    if (config.getBoolean("use_prefix")) {
                        special.add(api.getEnchantmentPrefix("special")+" "+api.getEnchantmentColor("special")+api.getDisplayName(list.get(i))+" ");
                    } else
                        special.add(api.getEnchantmentColor("special")+api.getDisplayName(list.get(i)));
                    break;
            }
        }
        Collections.sort(legendary); Collections.sort(epic); Collections.sort(rare); Collections.sort(common); Collections.sort(special);
        list.clear();
        list.addAll(legendary);
        list.addAll(epic);
        list.addAll(rare);
        list.addAll(common);
        list.addAll(special);
        return list;
    }
}
