package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    legendary.add(api.getColor("legendary")+api.getDisplayName(list.get(i)));
                    break;
                case "epic":
                    epic.add(api.getColor("epic")+api.getDisplayName(list.get(i)));
                    break;
                case "rare":
                    rare.add(api.getColor("rare")+api.getDisplayName(list.get(i)));
                    break;
                case "common":
                    common.add(api.getColor("common")+api.getDisplayName(list.get(i)));
                    break;
                case "special":
                    special.add(api.getColor("special")+api.getDisplayName(list.get(i)));
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
                        legendary.add(api.getPrefix("legendary")+" "+api.getColor("legendary")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        legendary.add(api.getColor("legendary")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "epic":
                    if (config.getBoolean("use_prefix")) {
                        epic.add(api.getPrefix("epic")+" "+api.getColor("epic")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        epic.add(api.getColor("epic")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "rare":
                    if (config.getBoolean("use_prefix")) {
                        rare.add(api.getPrefix("rare")+" "+api.getColor("rare")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        rare.add(api.getColor("rare")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "common":
                    if (config.getBoolean("use_prefix")) {
                        common.add(api.getPrefix("common")+" "+api.getColor("common")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    } else
                        common.add(api.getColor("common")+api.getDisplayName(list.get(i))+" "+nbt.getEnchantmentLevel(item, list.get(i)));
                    break;
                case "special":
                    if (config.getBoolean("use_prefix")) {
                        special.add(api.getPrefix("special")+" "+api.getColor("special")+api.getDisplayName(list.get(i))+" ");
                    } else
                        special.add(api.getColor("special")+api.getDisplayName(list.get(i)));
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
    public void setEnchantmentInfoMeta(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§7Rarity: "+api.getColor(api.getRarity(name))+api.getRarity(name).substring(0, 1).toUpperCase()+api.getRarity(name).substring(1));
        lore.add("§7Max Level: §e"+api.getLevelCap(name));
        lore.add("");
        for (int i = 0; i < api.getEnchantmentLore(name).split(":nl:").length; i++) {
            lore.add("§7"+api.getEnchantmentLore(name).split(":nl:")[i]);
        }
        meta.setLore(lore);
        meta.setDisplayName(api.getColor(api.getRarity(name))+"§n"+api.getDisplayName(name));
        item.setItemMeta(meta);
    }

    @Override
    public void setCleanserEnchantmentInfoMeta(ItemStack item, ItemStack reference, String name) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§7Rarity: "+api.getColor(api.getRarity(name))+api.getRarity(name).substring(0, 1).toUpperCase()+api.getRarity(name).substring(1));
        lore.add("§7Level: §e"+nbt.getEnchantmentLevel(reference, name)+" §8(§e"+api.getLevelCap(name)+"§8)");
        lore.add("");
        for (int i = 0; i < api.getEnchantmentLore(name).split(":nl:").length; i++) {
            lore.add("§7"+api.getEnchantmentLore(name).split(":nl:")[i]);
        }
        meta.setLore(lore);
        meta.setDisplayName(api.getColor(api.getRarity(name))+"§n"+api.getDisplayName(name));
        item.setItemMeta(meta);
    }

    // Crystals

    @Override
    public List<String> getCrystalLore(String rarity, int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("crystal");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                    .replace(":chance:", getChanceGradient(chance)+"%"));
        }
        return lore;
    }

    @Override
    public void updateCrystalLore(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> newLore = getCrystalLore(nbti.getString("crystal"), nbti.getInteger("chance"));
        item.getItemMeta().setLore(newLore);
        item.setItemMeta(item.getItemMeta());
    }

    @Override
    public void setCrystalMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getCrystalLore(rarity, chance));
        meta.setDisplayName(api.getItemName("crystal").replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1)));
        item.setItemMeta(meta);
    }

    @Override
    public List<String> getMysteryCrystalLore(String rarity, int low, int high) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("mysteryCrystal");
        for (int i = 0; i < newLore.length; i++) {
            if (low == 0 && high == 100) {
                lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                        .replace(":chance:", "§arandom"));
            } else {
                lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                        .replace(":chance:", "§a"+low+"-"+high+"%"));
            }
        }
        return lore;
    }

    @Override
    public void setMysteryCrystalMeta(ItemStack item, String rarity, int low, int high) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getMysteryCrystalLore(rarity, low, high));
        meta.setDisplayName(api.getItemName("mysteryCrystal").replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1)));
        item.setItemMeta(meta);
    }

    @Override
    public String getChanceGradient(int chance) {
        if (chance >= 80) {
            return "§2"+chance;
        } else if (chance >= 60) {
            return "§a"+chance;
        } else if (chance >= 40) {
            return "§e"+chance;
        } else if (chance >= 20) {
            return "§c"+chance;
        } else
            return "§4"+chance;
    }
    
    // Dust
    
    @Override
    public List<String> getDustLore(String rarity, int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("dust");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                    .replace(":chance:", getChanceGradient(chance)+"%"));
        }
        return lore;
    }

    @Override
    public void setDustMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getDustLore(rarity, chance));
        meta.setDisplayName(api.getColor(rarity)+api.getItemName("dust"));
        item.setItemMeta(meta);
    }

    // Cleanser

    @Override
    public List<String> getCleanserLore(int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("cleanser");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(i, newLore[i].replace(":chance:", getChanceGradient(chance)+"%"));
        }
        return lore;
    }

    @Override
    public void setCleanserMeta(ItemStack item, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getCleanserLore(chance));
        meta.setDisplayName("§d"+api.getItemName("cleanser"));
        item.setItemMeta(meta);
    }

    // Scroll

    @Override
    public List<String> getScrollLore(String rarity, int chance) {
        List<String> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("scroll");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(i, newLore[i].replace(":rarity:", api.getColor(rarity)+rarity.substring(0, 1).toUpperCase()+rarity.substring(1))
                    .replace(":chance:", getChanceGradient(chance)+"%")
                    .replace(":durability:", ""+getScrollDurability(rarity)));
        }
        return lore;
    }

    @Override
    public void setScrollMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getScrollLore(rarity, chance));
        meta.setDisplayName(api.getColor(rarity)+api.getItemName("scroll"));
        item.setItemMeta(meta);
    }

    @Override
    public int getScrollDurability(String rarity) {
        switch (rarity) {
            case "legendary": return 300;
            case "epic": return 150;
            case "rare": return 100;
            case "common": return 50;
        }
        return 0;
    }
}
