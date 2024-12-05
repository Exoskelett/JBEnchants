package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.commands.Crystals;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.exo.jbenchants.handlers.JBEnchantRegions;
import de.exo.jbenchants.items.Crystal;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTBlock;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class GUIHandler implements Listener {

    private static GUIHandler INSTANCE;
    private GUIHandler() {
    }
    public static GUIHandler getInstance() {
        if (INSTANCE == null) INSTANCE = new GUIHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    JBEnchantItems items = JBEnchantItems.getInstance();
    JBEnchantLore lore = JBEnchantLore.getInstance();
    JBEnchantNBT nbt = JBEnchantNBT.getInstance();
    JBEnchantRegions regions = JBEnchantRegions.getInstance();
    Crystal crystal = Crystal.getInstance();

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
                case "§8Decrypting...":
                    event.setCancelled(true);
                    break;
                case "§8Cleanser §7§o(Click to remove)":
                    event.setCancelled(true);
                    if (event.getClickedInventory() == player.getInventory()) return;
                    ItemStack enchItem = event.getClickedInventory().getItem(17);
                    List<String> enchants = lore.sortEnchants(nbt.getEnchants(enchItem));
                    if (event.getSlot() < enchants.size()) {
                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            ItemStack count = player.getInventory().getItem(i);
                            if (count != null && count.equals(enchItem)) {
                                nbt.removeEnchantment(enchItem, enchants.get(event.getSlot()).substring(2));
                                lore.updateLore(enchItem);
                                nbt.setUsedItemChance(player, "cleanser", -1);
                                player.getInventory().setItem(i, enchItem);
                                player.getOpenInventory().close();
                                return;
                            }
                        }
                        player.getOpenInventory().close();
                        player.sendMessage("§cSomething went wrong, please report this incident to staff!");
                    }
                case "§8Crystals":
                    event.setCancelled(true);
                    if (event.getClickedInventory() == player.getInventory() || event.getCurrentItem().getType() != Material.NETHER_STAR) return;
                    String rarity = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[0].substring(2).toLowerCase();
                    player = (Player) event.getWhoClicked();
                    if (nbt.getPlayerCrystals(player, rarity) > 0) {
                        if (player.getInventory().firstEmpty() != -1) {
                            nbt.addPlayerCrystals(player, rarity, -1);
                            player.getInventory().addItem(crystal.getCrystal(rarity));
                            player.openInventory(new Crystals().getCrystalsInventory(player));
                        } else
                            player.sendMessage("§cYou don't have enough space in your inventory.");
                    }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        switch (event.getView().getOriginalTitle()) {
            case "§8Cleanser §7§o(Click to remove)":
                if (nbt.getUsedItemChance(player, "cleanser") != "-1") {
                    player.getInventory().addItem(items.getCleanser(Integer.parseInt(nbt.getUsedItemChance(player, "cleanser"))));
                    nbt.setUsedItemChance(player, "cleanser", -1);
                }
                break;
            case "§8Treasure Hunter":
                InventoryHolder holder = event.getInventory().getHolder();
                if (holder instanceof Chest && regions.checkBlock(((Chest) holder).getBlock(), "mine")) {
                    ((Chest) holder).getBlock().setType(Material.AIR);
                }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest && regions.checkBlock(((Chest) holder).getBlock(), "mine") && !nbt.checkTreasureHunter(((Chest) holder).getBlock(), player)) {
            event.setCancelled(true);
        } else if (holder instanceof Chest && regions.checkBlock(((Chest) holder).getBlock(), "mine")) {
            Inventory chestInventory = event.getInventory();
            player.getWorld().getBlockAt(event.getInventory().getLocation()).setType(Material.AIR);
            event.setCancelled(true);
            player.openInventory(chestInventory);
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
        spacerMeta.setDisplayName(" ");
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
                    inv.setItem(edgeSlots[i], legendary);
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

    public Inventory getCleanserInventory(ItemStack item) {
        Inventory inventory = Bukkit.createInventory(null, 18, "§8Cleanser §7§o(Click to remove)");
        List<String> enchants = lore.sortEnchants(nbt.getEnchants(item));
        for (int i = 0; i < enchants.size() && i < 17; i++) {
            inventory.setItem(i, items.getCleanserEnchantInformation(item, enchants.get(i).substring(2)));
        }
        inventory.setItem(17, item);
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(" ");
        spacer.setItemMeta(spacerMeta);
        for (int k = 9; k < inventory.getSize(); k++) {
            if (inventory.getItem(k) == null) inventory.setItem(k, spacer);
        }
        return inventory;
    }
}
