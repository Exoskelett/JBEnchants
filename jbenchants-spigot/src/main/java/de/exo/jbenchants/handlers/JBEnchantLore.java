package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JBEnchantLore implements JBEnchantData.Lore {

    private static JBEnchantLore INSTANCE;

    private JBEnchantLore() {
    }

    public static JBEnchantLore getInstance() {
        if (INSTANCE == null)
            INSTANCE = new JBEnchantLore();
        return INSTANCE;
    }

    API api = Main.getAPI();
    Configuration config = Main.instance.getConfig();
    JBEnchantNBT nbt = JBEnchantNBT.getInstance();

    @Override
    public List<Integer> getEnchantmentLoreSlots(ItemStack item) {
        List<Integer> enchants = new ArrayList<>();
        List<Component> loreComponents = item.lore();
        if (loreComponents == null)
            return null;
        for (int i = 0; i < loreComponents.size(); i++) {
            String loreText = loreComponents.get(i).toString();
            if (config.getBoolean("use_prefix")) {
                if (api.check(api.getName(loreText.split(" ")[0] + " " + loreText.split(" ")[1]))) {
                    enchants.add(i);
                }
            } else {
                if (api.check(api.getName(loreText.split(" ")[0]))) {
                    enchants.add(i);
                }
            }
        }
        return enchants;
    }

    @Override
    public void deleteUnusedEnchants(ItemStack item) {
        List<Component> loreComponents = item.lore();
        if (loreComponents != null) {
            ItemMeta meta = item.getItemMeta();
            List<Component> newLore = new ArrayList<>();
            String displayName;
            for (Component component : loreComponents) {
                String loreText = component.toString();
                if (config.getBoolean("use_prefix")) {
                    displayName = loreText.split(" ")[1];
                } else {
                    displayName = loreText.split(" ")[0];
                }
                if (!api.check(api.getName(displayName)) || nbt.getEnchants(item).contains(api.getName(displayName))) {
                    newLore.add(component);
                }
            }
            if (newLore.isEmpty()) {
                meta.lore(null);
            } else {
                meta.lore(newLore);
            }
            item.setItemMeta(meta);
        }
    }

    @Override
    public void deleteAllEnchants(ItemStack item) {
        List<Component> loreComponents = item.lore();
        if (loreComponents != null) {
            ItemMeta meta = item.getItemMeta();
            List<Component> newLore = new ArrayList<>();
            for (Component component : loreComponents) {
                String loreText = component.toString();
                List<String> string = List.of(loreText.split(" "));
                try {
                    Integer.parseInt(string.get(string.size() - 1)); // checks whether the last arg is an integer value
                    continue;
                } catch (NumberFormatException ignored) {
                }
                if (config.getBoolean("use_prefix")) {
                    if (string.size() == 2) {
                        if (!api.check(api.getName(string.get(1)))) {
                            newLore.add(component);
                        }
                    } else {
                        newLore.add(component);
                    }
                } else {
                    if (string.size() == 1) {
                        if (!api.check(api.getName(string.get(0)))) {
                            newLore.add(component);
                        }
                    } else {
                        newLore.add(component);
                    }
                }
            }
            if (newLore.isEmpty()) {
                meta.lore(null);
            } else {
                meta.lore(newLore);
            }
            item.setItemMeta(meta);
        }
    }

    @Override
    public void updateLore(ItemStack item) {
        deleteAllEnchants(item);
        List<String> enchants = nbt.getEnchants(item);
        List<Integer> loreSlots = getEnchantmentLoreSlots(item);
        List<Component> loreComponents = item.lore();
        List<Component> sortedEnchantComponents = sortEnchantsWithLevel(enchants, item);

        if (loreSlots != null && !loreSlots.isEmpty()) {
            // If existing enchantment slots are found, replace them
            for (int i = 0; i < Math.min(loreSlots.size(), sortedEnchantComponents.size()); i++) {
                loreComponents.set(loreSlots.get(i), sortedEnchantComponents.get(i));
            }

            // If more new enchantments than existing slots, add them to the beginning
            if (sortedEnchantComponents.size() > loreSlots.size()) {
                loreComponents.addAll(0,
                        sortedEnchantComponents.subList(loreSlots.size(), sortedEnchantComponents.size()));
            }
        } else {
            // If no existing enchantment slots, add to the beginning
            loreComponents.addAll(0, sortedEnchantComponents);
        }

        item.lore(loreComponents);
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
                    legendary.add(api.getColor("legendary") + api.getDisplayName(list.get(i)));
                    break;
                case "epic":
                    epic.add(api.getColor("epic") + api.getDisplayName(list.get(i)));
                    break;
                case "rare":
                    rare.add(api.getColor("rare") + api.getDisplayName(list.get(i)));
                    break;
                case "common":
                    common.add(api.getColor("common") + api.getDisplayName(list.get(i)));
                    break;
                case "special":
                    special.add(api.getColor("special") + api.getDisplayName(list.get(i)));
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

    @Override
    public List<Component> sortEnchantsWithLevel(List<String> list, ItemStack item) {
        List<Component> legendary = new ArrayList<>();
        List<Component> epic = new ArrayList<>();
        List<Component> rare = new ArrayList<>();
        List<Component> common = new ArrayList<>();
        List<Component> special = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            switch (api.getRarity(list.get(i))) {
                case "legendary":
                    if (config.getBoolean("use_prefix")) {
                        legendary.add(Component.text().color(TextColor.fromHexString(api.getColor("legendary")))
                                .content(api.getPrefix("legendary") + " " + api.getDisplayName(list.get(i)) + " "
                                        + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    } else
                        legendary.add(Component.text().color(TextColor.fromHexString(api.getColor("legendary")))
                                .content(api.getDisplayName(list.get(i)) + " "
                                        + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    break;
                case "epic":
                    if (config.getBoolean("use_prefix")) {
                        epic.add(Component.text().color(TextColor.fromHexString(api.getColor("epic")))
                                .content(api.getPrefix("epic") + " " + api.getDisplayName(list.get(i)) + " "
                                        + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    } else
                        epic.add(Component.text().color(TextColor.fromHexString(api.getColor("epic"))).content(
                                api.getDisplayName(list.get(i)) + " " + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    break;
                case "rare":
                    if (config.getBoolean("use_prefix")) {
                        rare.add(Component.text().color(TextColor.fromHexString(api.getColor("rare")))
                                .content(api.getPrefix("rare") + " " + api.getDisplayName(list.get(i)) + " "
                                        + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    } else
                        rare.add(Component.text().color(TextColor.fromHexString(api.getColor("rare"))).content(
                                api.getDisplayName(list.get(i)) + " " + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    break;
                case "common":
                    if (config.getBoolean("use_prefix")) {
                        common.add(Component.text().color(TextColor.fromHexString(api.getColor("common")))
                                .content(api.getPrefix("common") + " " + api.getDisplayName(list.get(i)) + " "
                                        + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    } else
                        common.add(Component.text().color(TextColor.fromHexString(api.getColor("common"))).content(
                                api.getDisplayName(list.get(i)) + " " + nbt.getEnchantmentLevel(item, list.get(i)))
                                .build());
                    break;
                case "special":
                    if (config.getBoolean("use_prefix")) {
                        special.add(Component.text().color(TextColor.fromHexString(api.getColor("special")))
                                .content(api.getPrefix("special") + " " + api.getDisplayName(list.get(i))).build());
                    } else
                        special.add(Component.text().color(TextColor.fromHexString(api.getColor("special")))
                                .content(api.getDisplayName(list.get(i))).build());
                    break;
            }
        }

        // Sort based on the content of the components
        legendary.sort((c1, c2) -> c1.toString().compareTo(c2.toString()));
        epic.sort((c1, c2) -> c1.toString().compareTo(c2.toString()));
        rare.sort((c1, c2) -> c1.toString().compareTo(c2.toString()));
        common.sort((c1, c2) -> c1.toString().compareTo(c2.toString()));
        special.sort((c1, c2) -> c1.toString().compareTo(c2.toString()));

        List<Component> result = new ArrayList<>();
        result.addAll(legendary);
        result.addAll(epic);
        result.addAll(rare);
        result.addAll(common);
        result.addAll(special);
        return result;
    }

    @Override
    public void setEnchantmentInfoMeta(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text()
                .color(NamedTextColor.GRAY)
                .content("Rarity: ")
                .append(Component.text()
                        .color(TextColor.fromHexString(api.getColor(api.getRarity(name))))
                        .content(api.getRarity(name).substring(0, 1).toUpperCase() + api.getRarity(name).substring(1))
                        .build())
                .build());
        lore.add(Component.text()
                .color(NamedTextColor.GRAY)
                .content("Max Level: ")
                .append(Component.text()
                        .color(NamedTextColor.YELLOW)
                        .content(String.valueOf(api.getLevelCap(name)))
                        .build())
                .build());
        lore.add(Component.text().content("").build());

        for (String loreLine : api.getEnchantmentLore(name).split(":nl:")) {
            lore.add(Component.text()
                    .color(NamedTextColor.GRAY)
                    .content(loreLine)
                    .build());
        }

        meta.lore(lore);
        meta.displayName(Component.text()
                .color(TextColor.fromHexString(api.getColor(api.getRarity(name))))
                .content(api.getDisplayName(name))
                .decoration(TextDecoration.UNDERLINED, true)
                .build());
        item.setItemMeta(meta);
    }

    @Override
    public void setCleanserEnchantmentInfoMeta(ItemStack item, ItemStack reference, String name) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text()
                .color(NamedTextColor.GRAY)
                .content("Rarity: ")
                .append(Component.text()
                        .color(TextColor.fromHexString(api.getColor(api.getRarity(name))))
                        .content(api.getRarity(name).substring(0, 1).toUpperCase() + api.getRarity(name).substring(1))
                        .build())
                .build());
        lore.add(Component.text()
                .color(NamedTextColor.GRAY)
                .content("Level: ")
                .append(Component.text()
                        .color(NamedTextColor.YELLOW)
                        .content(String.valueOf(nbt.getEnchantmentLevel(reference, name)) + " (" + api.getLevelCap(name)
                                + ")")
                        .build())
                .build());
        lore.add(Component.text().content("").build());

        for (String loreLine : api.getEnchantmentLore(name).split(":nl:")) {
            lore.add(Component.text()
                    .color(NamedTextColor.GRAY)
                    .content(loreLine)
                    .build());
        }

        meta.lore(lore);
        meta.displayName(Component.text()
                .color(TextColor.fromHexString(api.getColor(api.getRarity(name))))
                .content(api.getDisplayName(name))
                .decoration(TextDecoration.UNDERLINED, true)
                .build());
        item.setItemMeta(meta);
    }

    // Crystals

    @Override
    public List<Component> getCrystalLore(String rarity, int chance) {
        List<Component> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("crystal");
        for (int i = 0; i < newLore.length; i++) {
            Component rarityText = Component.text()
                    .color(TextColor.fromHexString(api.getColor(rarity)))
                    .content(rarity.substring(0, 1).toUpperCase() + rarity.substring(1))
                    .build();
            lore.add(Component.text()
                    .content(newLore[i].replace(":rarity:", "")
                            .replace(":chance:", getChanceGradient(chance) + "%"))
                    .append(rarityText)
                    .build());
        }
        return lore;
    }

    @Override
    public void updateCrystalLore(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<Component> newLore = getCrystalLore(nbti.getString("crystal"), nbti.getInteger("chance"));
        item.lore(newLore);
        item.setItemMeta(item.getItemMeta());
    }

    @Override
    public void setCrystalMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(getCrystalLore(rarity, chance));
        meta.displayName(Component.text()
                .color(TextColor.fromHexString(api.getColor(rarity)))
                .content(api.getItemName("crystal").replace(":rarity:",
                        rarity.substring(0, 1).toUpperCase() + rarity.substring(1)))
                .build());
        item.setItemMeta(meta);
    }

    @Override
    public List<Component> getMysteryCrystalLore(String rarity, int low, int high) {
        List<Component> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("mysteryCrystal");
        for (int i = 0; i < newLore.length; i++) {
            Component rarityText = Component.text()
                    .color(TextColor.fromHexString(api.getColor(rarity)))
                    .content(rarity.substring(0, 1).toUpperCase() + rarity.substring(1))
                    .build();
            Component chanceText = Component.text()
                    .color(NamedTextColor.GREEN)
                    .content(low == 0 && high == 100 ? "random" : low + "-" + high + "%")
                    .build();
            lore.add(Component.text()
                    .content(newLore[i].replace(":rarity:", "").replace(":chance:", ""))
                    .append(rarityText)
                    .append(chanceText)
                    .build());
        }
        return lore;
    }

    @Override
    public void setMysteryCrystalMeta(ItemStack item, String rarity, int low, int high) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(getMysteryCrystalLore(rarity, low, high));
        meta.displayName(Component.text()
                .color(TextColor.fromHexString(api.getColor(rarity)))
                .content(api.getItemName("mysteryCrystal").replace(":rarity:",
                        rarity.substring(0, 1).toUpperCase() + rarity.substring(1)))
                .build());
        item.setItemMeta(meta);
    }

    @Override
    public String getChanceGradient(int chance) {
        if (chance >= 80) {
            return "§2" + chance;
        } else if (chance >= 60) {
            return "§a" + chance;
        } else if (chance >= 40) {
            return "§e" + chance;
        } else if (chance >= 20) {
            return "§c" + chance;
        } else
            return "§4" + chance;
    }

    // Dust

    @Override
    public List<Component> getDustLore(String rarity, int chance) {
        List<Component> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("dust");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(Component.text()
                    .content(
                            newLore[i]
                                    .replace(":rarity:",
                                            api.getColor(rarity) + rarity.substring(0, 1).toUpperCase()
                                                    + rarity.substring(1))
                                    .replace(":chance:", getChanceGradient(chance) + "%"))
                    .build());
        }
        return lore;
    }

    @Override
    public void setDustMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(getDustLore(rarity, chance));
        meta.displayName(Component.text(api.getColor(rarity) + api.getItemName("dust")));
        item.setItemMeta(meta);
    }

    // Cleanser

    @Override
    public List<Component> getCleanserLore(int chance) {
        List<Component> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("cleanser");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(Component.text()
                    .content(newLore[i].replace(":chance:", getChanceGradient(chance) + "%"))
                    .build());
        }
        return lore;
    }

    @Override
    public void setCleanserMeta(ItemStack item, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(getCleanserLore(chance));
        meta.displayName(Component.text("§d" + api.getItemName("cleanser")));
        item.setItemMeta(meta);
    }

    // Scroll

    @Override
    public List<Component> getScrollLore(String rarity, int chance) {
        List<Component> lore = new ArrayList<>();
        String[] newLore = api.getItemLore("scroll");
        for (int i = 0; i < newLore.length; i++) {
            lore.add(Component.text()
                    .content(
                            newLore[i]
                                    .replace(":rarity:",
                                            api.getColor(rarity) + rarity.substring(0, 1).toUpperCase()
                                                    + rarity.substring(1))
                                    .replace(":chance:", getChanceGradient(chance) + "%")
                                    .replace(":durability:", "" + getScrollDurability(rarity)))
                    .build());
        }
        return lore;
    }

    @Override
    public void setScrollMeta(ItemStack item, String rarity, int chance) {
        ItemMeta meta = item.getItemMeta();
        meta.lore(getScrollLore(rarity, chance));
        meta.displayName(Component.text(api.getColor(rarity) + api.getItemName("scroll")));
        item.setItemMeta(meta);
    }

    @Override
    public int getScrollDurability(String rarity) {
        switch (rarity) {
            case "legendary":
                return 300;
            case "epic":
                return 150;
            case "rare":
                return 100;
            case "common":
                return 50;
        }
        return 0;
    }
}
