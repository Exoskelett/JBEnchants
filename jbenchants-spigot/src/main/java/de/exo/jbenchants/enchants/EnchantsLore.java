package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EnchantsLore {

    private static EnchantsLore INSTANCE;

    private EnchantsLore() {
    }

    public static EnchantsLore getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EnchantsLore();
        return INSTANCE;
    }

    API api = Main.getAPI();
    Configuration config = Main.instance.getConfig();
    EnchantsNBT nbt = EnchantsNBT.getInstance();

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

    public void deleteAllEnchants(ItemStack item) {
        List<String> lore = item.getLore();
        if (lore != null) {
            for (int i = lore.size()-1; i >= 0; i--) {
                List<String> string = List.of(lore.get(i).split(" "));
                try {
                    int nullCheck = Integer.parseInt(string.get(string.size()-1));  // checks whether the last arg is an integer value
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
        Collections.sort(legendary);
        Collections.sort(epic);
        Collections.sort(rare);
        Collections.sort(common);
        Collections.sort(special);
        list.clear();
        list.addAll(legendary);
        list.addAll(epic);
        list.addAll(rare);
        list.addAll(common);
        list.addAll(special);
        return list;
    }

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
        Collections.sort(legendary);
        Collections.sort(epic);
        Collections.sort(rare);
        Collections.sort(common);
        Collections.sort(special);
        list.clear();
        list.addAll(legendary);
        list.addAll(epic);
        list.addAll(rare);
        list.addAll(common);
        list.addAll(special);
        return list;
    }

    public void setEnchantmentInfoMeta(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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
        if (name.equals("regenerate")) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionData(new PotionData(PotionType.REGEN, false, false));
            item.setItemMeta(potionMeta);
            item.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        }
    }
}
