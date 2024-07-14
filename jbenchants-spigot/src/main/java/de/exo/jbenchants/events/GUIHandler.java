package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.exo.jbenchants.handlers.JBEnchantRegions;
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

    API api = Main.instance.api;
    JBEnchantItems items = Main.instance.items;
    JBEnchantLore lore = Main.instance.lore;
    JBEnchantNBT nbt = Main.instance.nbt;
    JBEnchantRegions regions = Main.instance.regions;

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
                                nbt.setCleanserChance(player, -1);
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
                    if (nbt.getPlayerCrystal(player, rarity) > 0) {
                        nbt.addPlayerCrystal(player, rarity, -1);
                        player.getInventory().addItem(items.getCrystal(rarity));
                        player.openInventory(getCrystalsInventory(player));
                    }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        switch (event.getView().getOriginalTitle()) {
            case "§8Cleanser §7§o(Click to remove)":
                if (nbt.getCleanserChance(player) != -1) {
                    player.getInventory().addItem(items.getCleanser(nbt.getCleanserChance(player)));
                    nbt.setCleanserChance(player, -1);
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

    private int[] glassSlots = {0,1,2,3,5,6,7,8,9,17,18,19,20,21,23,24,25,26};
    public Inventory getCrystalOpeningInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "§8Decrypting...");
        ItemStack decryptItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta decryptItemMeta = decryptItem.getItemMeta();
        decryptItemMeta.setDisplayName("§7Decrypting");
        decryptItem.setItemMeta(decryptItemMeta);
        for (int i = 0; i < glassSlots.length; i++)
            inventory.setItem(glassSlots[i], getRandomGlass());
        inventory.setItem(4, decryptItem);
        inventory.setItem(22, decryptItem);
        return inventory;
    }

    public ItemStack getRandomGlass() {
        Material[] glass = {Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE};
        return new ItemStack(glass[new Random().nextInt(glass.length)]);
    }

    public void setRandomGlass(Player player) {
        Inventory inventory = (Inventory) player.getOpenInventory();
        for (int i = 0; i < glassSlots.length; i++)
            inventory.setItem(glassSlots[i], getRandomGlass());
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

    public Inventory getCrystalsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§8Crystals");
        ItemStack crystal = new ItemStack(Material.NETHER_STAR);
        ItemMeta crystalMeta = crystal.getItemMeta();
        List<String> lore = new ArrayList<>();
        String[] rarities = {"Common", "Rare", "Epic", "Legendary"};
        for (int i = 0; i < 4; i++) {
            crystalMeta.setDisplayName(api.getColor(rarities[i].toLowerCase())+rarities[i]+" Crystal");
            lore.clear();
            lore.add("§eAmount: §d"+nbt.getPlayerCrystal(player, rarities[i].toLowerCase()));
            lore.add("§7Click to claim §bx1 "+api.getColor(rarities[i].toLowerCase())+rarities[i]+" Crystal§7!");
            crystalMeta.setLore(lore);
            crystal.setItemMeta(crystalMeta);
            inventory.setItem(10+i*2, crystal);
        }
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(" ");
        spacer.setItemMeta(spacerMeta);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, spacer);
        }
        return inventory;
    }

    public List<String> getPossibleEnchants(Player player, ItemStack item, String rarity) {
        //int level = Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%level_level%"));
        int level = 40;
        int maxEnchants = 8;
        List<String> possibleEnchants = api.getEnchantments(rarity);
        List<String> typeSpecificEnchants = api.getEnchantments(nbt.getCategory(item));
        possibleEnchants.retainAll(typeSpecificEnchants);
        List<String> itemEnchants = nbt.getEnchants(item);
        itemEnchants.removeIf(enchant -> api.getRarity(enchant).equals(rarity));
        if (level >= 35) {
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
        if (itemEnchants != null) {
            List<String> deletedEnchants = new ArrayList<>();
            if (nbt.getEnchants(item).size() >= maxEnchants) {  // max enchantment amount: only upgrades
                for (String enchant : itemEnchants) {
                    if (nbt.getEnchantmentLevel(item, enchant) >= api.getLevelCap(enchant)) {
                        itemEnchants.remove(enchant);
                    }
                }
                if (!itemEnchants.isEmpty()) {
                    return itemEnchants;
                } else
                    return null;
            } else {  // new enchants can be added: all enchants - maxed enchants)
                if (nbt.getEnchants(item) != null) {
                    for (String enchant : itemEnchants) {
                        if (nbt.getEnchantmentLevel(item, enchant) >= api.getLevelCap(enchant)) {
                            possibleEnchants.remove(enchant);
                        }
                    }
                }

            }
        }
        return possibleEnchants;
    }

    public LevelEnchants getLevel(Player player) {
        //int level = Integer.parseInt(PlaceholderAPI.setPlaceholders(player, "%level_level%"));
        LevelEnchants lE;
        int level = 40;
        if (level >= 35) {
            lE = new LevelEnchants(level, 7, -1, -1);
        } else if (level >= 25) {
            lE = new LevelEnchants(level, 6, 35, 7);
        } else if (level >= 15) {
            lE = new LevelEnchants(level, 5, 25, 6);
        } else if (level >= 10) {
            lE = new LevelEnchants(level, 4, 15, 5);
        } else if (level >= 5) {
            lE = new LevelEnchants(level, 3, 10, 4);
        } else {
            lE = new LevelEnchants(level, 2, 5, 3);
        }
        return lE;
    }

    static class LevelEnchants {
        private int level, maxEnchants, nextLevel, nextMaxEnchants;
        public LevelEnchants(int level, int maxEnchants, int nextLevel, int nextMaxEnchants) {
            this.level = level;
            this.maxEnchants = maxEnchants;
            this.nextLevel = nextLevel;
            this.nextMaxEnchants = nextMaxEnchants;
        }
        public int getLevel() {
            return level;
        }
        public int getMaxEnchants() {
            return maxEnchants;
        }
        public int getNextLevel() {
            return getNextLevel();
        }
        public int getNextMaxEnchants() {
            return getNextMaxEnchants();
        }
    }

    public String unlockCrystal(Player player, ItemStack item, String rarity) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Decrypting...");
        List<String> possibleEnchants = getPossibleEnchants(player, item, rarity);
        if (possibleEnchants.isEmpty()) return null;
        Collections.shuffle(possibleEnchants);
        int start = new Random().nextInt(possibleEnchants.size());
        boolean flipAtEnd;
        if (Math.random() <= 0.5) {
            flipAtEnd = true;
            //Bukkit.broadcastMessage(possibleEnchants.get((start+34)%possibleEnchants.size()));
            startDecrypting(player, item, 1, 12, 3, possibleEnchants, start, true, possibleEnchants.get((start+34)%possibleEnchants.size()));
            return possibleEnchants.get((start+34)%possibleEnchants.size());
        } else {
            flipAtEnd = false;
            //Bukkit.broadcastMessage(possibleEnchants.get((start+33)%possibleEnchants.size()));
            startDecrypting(player, item, 1, 12, 2, possibleEnchants, start, false, possibleEnchants.get((start+33)%possibleEnchants.size()));
            return possibleEnchants.get((start+33)%possibleEnchants.size());
        }
    }

    private int taskId, index = 0, lap = 1;

    public void startDecrypting(Player player, ItemStack item, int phase, int countdown, int interval, List<String> enchants, int start, boolean flipAtEnd, String result) {
        player.openInventory(getCrystalOpeningInventory());
        this.index += countdown;
        int[] slots = {10,11,12,13,14,15,16};
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (index == 0) {
                    switch (phase) {
                        case 1:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item, 2, 8, 3, enchants, (start+12)%enchants.size(), flipAtEnd, result);
                            break;
                        case 2:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item,  3, 6, 5, enchants, (start+8)%enchants.size(), flipAtEnd, result);
                            break;
                        case 3:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item,  4, 4, 10, enchants, (start+6)%enchants.size(), flipAtEnd, result);
                            break;
                        case 4:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item,  5, 3, 20, enchants, (start+4)%enchants.size(), flipAtEnd, result);
                            //startDecrypting(player, 5, (int) (Math.random() * 7), 40, enchants, (start+3)%enchants.size());
                            break;
                        default:
                            stopDecrypting();
                            lore.updateLore(item);
                            player.updateInventory();
                            LevelEnchants level = getLevel(player);
                            if (flipAtEnd) {
                                for (int k = 0; k < 7; k++) {
                                    player.getOpenInventory().setItem(slots[k], items.getEnchantInformation(enchants.get((start+k+lap-3+enchants.size())%enchants.size())));
                                }
                                if (level.getMaxEnchants() > nbt.getEnchants(item).size()) { // max. enchants not reached: enchant can be added
                                    player.sendMessage("§a§l+ "+api.getColor(api.getRarity(result))+api.getDisplayName(result)+" §7has been added to your item.");
                                } else { // max. enchants reached or gone over: enchant can be added if already on item
                                    if (nbt.getEnchants(item).contains(result)) {
                                        player.sendMessage("§a§l+ "+api.getColor(api.getRarity(result))+api.getDisplayName(result)+" §7has been added to your item.");
                                    } else {
                                        if (level.getMaxEnchants() == 8 || nbt.getEnchants(item).size() >= 8) {  // maxed. max enchants
                                            player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"+level.getMaxEnchants()+"§f enchantments. You've been returned your crystal.");
                                        } else {  //
                                            player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"+level.getMaxEnchants()+"§f enchantments. You've been returned your crystal."
                                            +"§7§oLevelup to §e§n§o"+level.nextLevel+"§7§o to have §c§n§o"+level.getNextMaxEnchants()+"§7§o enchants)");
                                        }
                                    }

                                }

                                player.sendMessage("§a§l+ "+api.getColor(api.getRarity(result))+api.getDisplayName(result)+" §7has been added to your item.");
                            } else {
                                player.sendMessage("§a§l+ " + api.getColor(api.getRarity(result)) + api.getDisplayName(result) + " §7has been added to your item.");
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
                            break;
                    }
                } else {
                    if (!player.getOpenInventory().getOriginalTitle().equals("§8Decrypting...")) {
                        stopDecrypting();
                        lore.updateLore(item);
                        player.updateInventory();
                        player.sendMessage("§a§l+ "+api.getColor(api.getRarity(result))+api.getDisplayName(result)+" §7has been added to your item.");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
                        return;
                    }
                    for (int glassSlot : glassSlots) {
                        player.getOpenInventory().setItem(glassSlot, getRandomGlass());
                    }
                    for (int k = 0; k < 7; k++) {
                        player.getOpenInventory().setItem(slots[k], items.getEnchantInformation(enchants.get((start+k+lap-3+enchants.size())%enchants.size())));
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 30, 5);
                    index--;
                    lap++;
                }
            }
        }, 0L, interval);
    }

    public void stopDecrypting() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public boolean getDecryption() {
        if (Bukkit.getScheduler().isQueued(taskId)) {
            return true;
        } else
            return false;
    }
}
