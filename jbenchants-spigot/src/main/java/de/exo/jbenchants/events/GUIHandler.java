package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class GUIHandler implements Listener {

    API api = Main.instance.api;
    JBEnchantItems items = Main.instance.items;
    JBEnchantNBT nbt = Main.instance.nbt;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item != null) {
            switch (event.getView().getOriginalTitle()) {
                case "§8Enchants":
                    event.setCancelled(true);
                    switch (item.getType()) {
                        case WHITE_DYE -> player.openInventory(getEnchantsInventory("Common"));
                        case ORANGE_DYE -> player.openInventory(getEnchantsInventory("Rare"));
                        case LIGHT_BLUE_DYE -> player.openInventory(getEnchantsInventory("Epic"));
                        case PURPLE_DYE -> player.openInventory(getEnchantsInventory("Legendary"));
                        case IRON_PICKAXE -> player.openInventory(getEnchantsInventory("Tool"));
                        case IRON_CHESTPLATE -> player.openInventory(getEnchantsInventory("Armor"));
                        case IRON_SWORD -> player.openInventory(getEnchantsInventory("Weapon"));
                    }
                        break;
                case "§8Common Enchants", "§8Rare Enchants", "§8Epic Enchants", "§8Legendary Enchants", "§8Tool Enchants", "§8Armor Enchants", "§8Weapon Enchants":
                    event.setCancelled(true);
                    if (event.getCurrentItem().getType().equals(Material.RED_BED)) {
                        player.openInventory(getEnchantsInventory());
                    }
                    break;
            }
        }
    }

    public Inventory getEnchantsInventory() {
        Inventory inv = Bukkit.createInventory(null, 36, "§8Enchants");
        ItemStack common = new ItemStack(Material.WHITE_DYE);
        ItemMeta commonMeta = common.getItemMeta();
        commonMeta.setDisplayName("§7✦ "+api.getColor("common")+"§nCommon Enchants§7 ✦");
        common.setItemMeta(commonMeta);
        inv.setItem(10, common);
        ItemStack rare = new ItemStack(Material.ORANGE_DYE);
        ItemMeta rareMeta = rare.getItemMeta();
        rareMeta.setDisplayName("§7✦ "+api.getColor("rare")+"§nRare Enchants§7 ✦");
        rare.setItemMeta(rareMeta);
        inv.setItem(12, rare);
        ItemStack epic = new ItemStack(Material.LIGHT_BLUE_DYE);
        ItemMeta epicMeta = epic.getItemMeta();
        epicMeta.setDisplayName("§7✦ "+api.getColor("epic")+"§nEpic Enchants§7 ✦");
        epic.setItemMeta(epicMeta);
        inv.setItem(14, epic);
        ItemStack legendary = new ItemStack(Material.PURPLE_DYE);
        ItemMeta legendaryMeta = legendary.getItemMeta();
        legendaryMeta.setDisplayName("§7✦ "+api.getColor("legendary")+"§nLegendary Enchants§7 ✦");
        legendary.setItemMeta(legendaryMeta);
        inv.setItem(16, legendary);
        ItemStack tool = new ItemStack(Material.IRON_PICKAXE);
        tool.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta toolMeta = tool.getItemMeta();
        toolMeta.setDisplayName("§5§nTool Enchants");
        tool.setItemMeta(toolMeta);
        inv.setItem(20, tool);
        ItemStack armor = new ItemStack(Material.IRON_CHESTPLATE);
        armor.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta armorMeta = armor.getItemMeta();
        armorMeta.setDisplayName("§5§nArmor Enchants");
        armor.setItemMeta(armorMeta);
        inv.setItem(22, armor);
        ItemStack weapon = new ItemStack(Material.IRON_SWORD);
        weapon.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.setDisplayName("§5§nWeapon Enchants");
        weapon.setItemMeta(weaponMeta);
        inv.setItem(24, weapon);

        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName("");
        spacer.setItemMeta(spacerMeta);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, spacer);
            }
        }
        return inv;
    }

    public Inventory getEnchantsInventory(String type) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8"+type+" Enchants");
        int[] edgeSlots = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,46,47,48,49,50,51,52,53};
        ItemStack common = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta commonMeta = common.getItemMeta();
        commonMeta.setDisplayName(api.getColor("common")+"§nCommon Enchants");
        common.setItemMeta(commonMeta);
        ItemStack rare = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta rareMeta = rare.getItemMeta();
        rareMeta.setDisplayName(api.getColor("rare")+"§nRare Enchants");
        rare.setItemMeta(rareMeta);
        ItemStack epic = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta epicMeta = epic.getItemMeta();
        epicMeta.setDisplayName(api.getColor("epic")+"§nEpic Enchants");
        epic.setItemMeta(epicMeta);
        ItemStack legendary = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta legendaryMeta = legendary.getItemMeta();
        legendaryMeta.setDisplayName(api.getColor("legendary")+"§nLegendary Enchants");
        legendary.setItemMeta(legendaryMeta);
        ItemStack category = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack returnItem = new ItemStack(Material.RED_BED);
        ItemMeta returnItemMeta = returnItem.getItemMeta();
        returnItemMeta.setDisplayName("§e§l<- §fGo Back");
        returnItem.setItemMeta(returnItemMeta);

        for (int i = 0; i < edgeSlots.length; i++) {
            switch (type) {
                case "Common":
                    inv.setItem(edgeSlots[i], common);
                    break;
                case "Rare":
                    inv.setItem(edgeSlots[i], rare);
                    break;
                case "Epic":
                    inv.setItem(edgeSlots[i], epic);
                    break;
                case "Legendary":
                    inv.setItem(edgeSlots[i], category);
                    break;
                case "Tool":
                    ItemMeta categoryMeta = category.getItemMeta();
                    categoryMeta.setDisplayName("§c§nTool Enchants");
                    category.setItemMeta(categoryMeta);
                    inv.setItem(edgeSlots[i], category);
                    break;
                case "Armor":
                    categoryMeta = category.getItemMeta();
                    categoryMeta.setDisplayName("§c§nArmor Enchants");
                    category.setItemMeta(categoryMeta);
                    inv.setItem(edgeSlots[i], category);
                    break;
                case "Weapon":
                    categoryMeta = category.getItemMeta();
                    categoryMeta.setDisplayName("§c§nWeapon Enchants");
                    category.setItemMeta(categoryMeta);
                    inv.setItem(edgeSlots[i], category);
                    break;
            }
        }
        inv.setItem(45, returnItem);
        for (int i = 0; i < api.getEnchantments(type.toLowerCase()).size(); i++) {
            inv.addItem(items.getEnchantInformation(api.getEnchantments(type.toLowerCase()).get(i)));
        }

        return inv;
    }

    private List<String> getPossibleEnchants(Player player, ItemStack item, String rarity) {
        int level = Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "level_level"));
        int maxEnchants;
        List<String> possibleEnchants = api.getEnchantments(rarity);
        List<String> typeSpecificEnchants = api.getEnchantments(nbt.getCategory(item));
        possibleEnchants.retainAll(typeSpecificEnchants);
        List<String> itemEnchants = nbt.getEnchants(item);
        if (level >= 40) {
            maxEnchants = 8;
        } else if (level >= 35) {
            maxEnchants = 7;
        } else if (level >= 25) {
            maxEnchants = 6;
        } else if (level >= 15) {
            maxEnchants = 5;
        } else if (level >= 10) {
            maxEnchants = 4;
        } else if (level >= 5) {
            maxEnchants = 3;
        } else {
            maxEnchants = 2;
        }
        if (itemEnchants.size() >= maxEnchants) {  // max enchantment amount - only upgrades
            for (String enchants : nbt.getEnchants(item)) {
                if (nbt.getEnchantmentLevel(item, enchants) == api.getLevelCap(enchants)) {
                    itemEnchants.remove(enchants);
                }
            }
            if (!itemEnchants.isEmpty()) {
                return itemEnchants;
            } else
                return null;
        } else {  // new enchants can be added (- maxed enchants)
            for (String enchants : nbt.getEnchants(item)) {
                if (nbt.getEnchantmentLevel(item, enchants) == api.getLevelCap(enchants)) {
                    possibleEnchants.remove(enchants);
                }
            }
            return possibleEnchants;
        }
    }

    public void unlockCrystal(Player player, ItemStack item, List<String> enchants) {
        Inventory inv = Bukkit.createInventory(null, 27, "§7Decrypting");
        ItemStack decryptItem = new ItemStack(Material.WHITE_DYE);
        ItemMeta decryptItemMeta = decryptItem.getItemMeta();
        decryptItemMeta.setDisplayName("§8Decrypting...");
        decryptItem.setItemMeta(decryptItemMeta);
        BukkitScheduler scheduler = Main.instance.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                // Do something
            }
        }, 0L, 5L);
    }
}