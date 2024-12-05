package de.exo.jbenchants.items;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.events.GUIHandler;
import de.exo.jbenchants.handlers.JBEnchantHandler;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bendy
 */
public class Crystal implements Listener, CommandExecutor, TabCompleter {

    private static Crystal INSTANCE;

    private Crystal() {
    }

    public static Crystal getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Crystal();
        return INSTANCE;
    }

    API api = Main.getAPI();
    JBEnchantLore lore = JBEnchantLore.getInstance();
    JBEnchantItems items = JBEnchantItems.getInstance();
    JBEnchantNBT nbt = JBEnchantNBT.getInstance();
    JBEnchantHandler handler = JBEnchantHandler.getInstance();
    GUIHandler guiHandler = GUIHandler.getInstance();

    // Prerequisites

    public ItemStack getCrystal(String rarity) {
        int chance = (int) Math.floor(Math.random() * 100);
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

    public ItemStack getCrystal(String rarity, int chance) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
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
        nbti.setString("chance", "" + chance);
        nbti.applyNBT(item);
        lore.setCrystalMeta(item, rarity, chance);
        return item;
    }

    public ItemStack getMysteryCrystal(String rarity, int low, int high) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        item.getItemMeta().addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(item.getItemMeta());
        item.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
        NBTItem nbti = new NBTItem(item);
        switch (rarity) {
            case "legendary", "epic", "rare", "common":
                nbti.setString("crystal", rarity);
                nbti.setString("chance", low + "-" + high);
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

    public void tryNaturalEnchant(Player player, ItemStack item, String enchant) { // adds enchant to item from crystal
                                                                                   // opening, if possible
        LevelEnchants level = getPlayerMaxEnchants(player);
        if (player.isOnline()) {
            if (checkEnchantUpgradability(player, item, enchant)) {
                nbt.addEnchantmentLevel(item, enchant, 1);
                nbt.setUsedItemChance(player, api.getRarity(enchant), -1);
                lore.updateLore(item);
                player.updateInventory();
                player.sendMessage("§a§l+ " + api.getColor(api.getRarity(enchant)) + api.getDisplayName(enchant)
                        + " §7has been added to your item.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
            } else {
                if (player.getInventory().firstEmpty() != -1) {
                    String[] checkCrystalReturn = nbt.getUsedItemChance(player, "crystal").split("-");
                    String rarity = checkCrystalReturn[0];
                    int chance = Integer.parseInt(checkCrystalReturn[1]);
                    nbt.setUsedItemChance(player, rarity, -1);
                    if (level.nextMaxEnchants == -1) { // check whether player reached personal or total enchantment
                                                       // limit
                        player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"
                                + level.maxEnchants + "§f enchantments. You've been returned your crystal.");
                    } else
                        player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"
                                + level.maxEnchants + "§f enchantments. You've been returned your crystal."
                                + "§7§oLevelup to §e§n§o" + level.nextLevel + "§7§o to have §c§n§o"
                                + level.nextMaxEnchants + "§7§o enchants");
                    if (chance > 4) { // check whether crystal can be returned
                        player.getInventory().addItem(getCrystal(rarity, chance - 4));
                    } else
                        player.sendMessage(
                                "§cThe returned crystal broke because the previous success chance was 4% or lower.");
                } else {
                    if (level.nextMaxEnchants == -1) {
                        player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"
                                + level.maxEnchants
                                + "§f enchantments. As your inventory is full, please rejoin when you have space available.");
                    } else {
                        player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"
                                + level.maxEnchants
                                + "§f enchantments. As your inventory is full, please rejoin when you have space available."
                                + "§7§oLevelup to §e§n§o" + level.nextLevel + "§7§o to have §c§n§o"
                                + level.nextMaxEnchants + "§7§o enchants");
                    }
                }
            }
        }
    }

    public boolean checkEnchantUpgradability(Player player, ItemStack item, String enchant) { // true = 1 or more levels
        NBTItem nbti = new NBTItem(item);
        if (nbt.getEnchants(item).size() < getPlayerMaxEnchants(player).maxEnchants) {
            if (nbti.hasTag(enchant))
                return api.getLevelCap(enchant) > nbti.getInteger(enchant);
            return true;
        } else {
            if (nbti.hasTag(enchant))
                return api.getLevelCap(enchant) > nbti.getInteger(enchant);
            return false;
        }
    }

    public List<String> getPossibleEnchants(Player player, ItemStack item, String rarity) {
        List<String> possibleEnchants = api.getEnchantments(rarity);
        List<String> typeSpecificEnchants = api.getEnchantments(nbt.getCategory(item));
        possibleEnchants.retainAll(typeSpecificEnchants);
        List<String> itemEnchants = nbt.getEnchants(item);
        itemEnchants.removeIf(enchant -> !api.getRarity(enchant).equals(rarity));
        if (nbt.getEnchants(item).size() < getPlayerMaxEnchants(player).maxEnchants) { // all enchants - max. level
                                                                                       // enchants
            if (!itemEnchants.isEmpty()) {
                for (String enchant : itemEnchants)
                    if (!checkEnchantUpgradability(player, item, enchant))
                        possibleEnchants.remove(enchant);
            }
            return possibleEnchants;
        } else { // already existing enchants - max. level enchants
            if (!itemEnchants.isEmpty()) {
                int initialSize = possibleEnchants.size();
                for (String enchant : itemEnchants)
                    if (!checkEnchantUpgradability(player, item, enchant))
                        possibleEnchants.remove(enchant);
                if (possibleEnchants.size() + itemEnchants.size() == initialSize)
                    return null;
                return possibleEnchants;
            }
            return null;
        }
    }

    // Events

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String checkCrystalReturn = nbt.getUsedItemChance(player, "crystal");
        if (checkCrystalReturn != null && player.getInventory().firstEmpty() != -1) {
            String[] returnedCrystal = checkCrystalReturn.split("-");
            player.getInventory().addItem(getCrystal(returnedCrystal[0], Integer.parseInt(returnedCrystal[1])));
            nbt.setUsedItemChance(event.getPlayer(), returnedCrystal[0], -1);
            player.sendMessage(
                    "§cOopsies, seems like we've encountered an issue with returning a crystal to you before.\nThe same crystal got refunded to your inventory.");
        }
    }

    // Command

    String normalCrystalSyntax = "§c/crystal normal [player] <amount> <rarity> <chance>";
    String mysteryCrystalSyntax = "§c/crystal mystery [player] <amount> <rarity> <low> <high>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {
        if (args.length > 1) {
            try {
                Player target = Bukkit.getPlayer(args[1]);
                ItemStack crystal = null;
                switch (args[0]) {
                    case "normal":
                        switch (args.length) {
                            case 2: // type + player
                                crystal = getCrystal("random");
                                sender.sendMessage(
                                        target.getName() + " §7received §f1x " + crystal.getItemMeta().displayName());
                                break;
                            case 3: // type + player + amount
                                crystal = getCrystal("random");
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            case 4: // type + player + amount + rarity
                                crystal = getCrystal(args[3]);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            case 5: // type + player + amount + rarity + chance
                                crystal = getCrystal(args[3], Integer.parseInt(args[4]));
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            default:
                                sender.sendMessage("§cToo many arguments.");
                        }
                        target.getInventory().addItem(crystal);
                        break;
                    case "mystery":
                        switch (args.length) {
                            case 2: // type + player
                                crystal = getMysteryCrystal("random", 0, 100);
                                sender.sendMessage(
                                        target.getName() + " §7received §f1x " + crystal.getItemMeta().displayName());
                                break;
                            case 3: // type + player + amount
                                crystal = getMysteryCrystal("random", 0, 100);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            case 4: // type + player + amount + rarity
                                crystal = getMysteryCrystal(args[3], 0, 100);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            case 5: // type + player + amount + rarity + low chance
                                crystal = getMysteryCrystal(args[3], Integer.parseInt(args[4]), 100);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            case 6: // type + player + amount + rarity + low chance + high chance
                                crystal = getMysteryCrystal(args[3], Integer.parseInt(args[4]),
                                        Integer.parseInt(args[5]));
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getName() + " §7received §f" + args[2] + "x "
                                        + crystal.getItemMeta().displayName());
                                break;
                            default:
                                sender.sendMessage("§cToo many arguments.");
                        }
                        target.getInventory().addItem(crystal);
                        break;
                    default:
                        sender.sendMessage(normalCrystalSyntax);
                        sender.sendMessage(mysteryCrystalSyntax);
                }
            } catch (NullPointerException e) {
                sender.sendMessage("§c'" + args[1] + "' is not online.");
            } catch (NumberFormatException e) {
                switch (args.length) {
                    case 3, 4, 5, 6:
                        sender.sendMessage("§c'" + args[args.length - 1] + "' is not a valid number.");
                }
            }
        } else {
            sender.sendMessage(normalCrystalSyntax);
            sender.sendMessage(mysteryCrystalSyntax);
        }
        return false;
    }

    // TabCompleter

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();
        if (args.length > 0) {
            switch (args.length) {
                case 1:
                    completer.add("normal");
                    completer.add("mystery");
                    return completer;
                case 4:
                    completer.add("common");
                    completer.add("rare");
                    completer.add("epic");
                    completer.add("legendary");
                    return completer;
                case 3, 5, 6:
                    completer.add("");
                    return completer;
            }
        }
        return null;
    }

    // GUIs

    private int[] glassSlots = { 0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25, 26 };

    public Inventory getCrystalOpeningInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("§8Decrypting..."));
        ItemStack decryptItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta decryptItemMeta = decryptItem.getItemMeta();
        decryptItemMeta.displayName(Component.text("§7Decrypting"));
        decryptItem.setItemMeta(decryptItemMeta);
        for (int i = 0; i < glassSlots.length; i++)
            inventory.setItem(glassSlots[i], getRandomGlass());
        inventory.setItem(4, decryptItem);
        inventory.setItem(22, decryptItem);
        return inventory;
    }

    public ItemStack getRandomGlass() {
        Material[] glass = { Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE };
        return new ItemStack(glass[new java.util.Random().nextInt(glass.length)]);
    }

    public void setRandomGlass(Player player) {
        Inventory inventory = (Inventory) player.getOpenInventory();
        for (int i = 0; i < glassSlots.length; i++)
            inventory.setItem(glassSlots[i], getRandomGlass());
    }

    public LevelEnchants getPlayerMaxEnchants(Player player) {
        // int level = Integer.parseInt(PlaceholderAPI.setPlaceholders(player,
        // "%level_level%"));
        LevelEnchants levelEnchants;
        int level = 40;
        if (level >= 40) {
            levelEnchants = new LevelEnchants(level, 8, -1, -1);
        } else if (level >= 35) {
            levelEnchants = new LevelEnchants(level, 7, 40, 8);
        } else if (level >= 25) {
            levelEnchants = new LevelEnchants(level, 6, 35, 7);
        } else if (level >= 15) {
            levelEnchants = new LevelEnchants(level, 5, 25, 6);
        } else if (level >= 10) {
            levelEnchants = new LevelEnchants(level, 4, 15, 5);
        } else if (level >= 5) {
            levelEnchants = new LevelEnchants(level, 3, 10, 4);
        } else {
            levelEnchants = new LevelEnchants(level, 2, 5, 3);
        }
        return levelEnchants;
    }

    static class LevelEnchants {
        @SuppressWarnings("unused")
        private final int level, maxEnchants, nextLevel, nextMaxEnchants;

        public LevelEnchants(int level, int maxEnchants, int nextLevel, int nextMaxEnchants) {
            this.level = level;
            this.maxEnchants = maxEnchants;
            this.nextLevel = nextLevel;
            this.nextMaxEnchants = nextMaxEnchants;
        }
    }

    public String unlockCrystal(Player player, ItemStack item, String rarity) {
        List<String> possibleEnchants = getPossibleEnchants(player, item, rarity);
        if (possibleEnchants.isEmpty())
            return null;
        Collections.shuffle(possibleEnchants);
        int start = new java.util.Random().nextInt(possibleEnchants.size());
        int flipAtEnd = 0; // 0 = no flip
        if (Math.random() <= 0.5)
            flipAtEnd = 1; // 1 = flip
        startDecrypting(player, item, 1, 12, 2 + flipAtEnd, possibleEnchants, start, flipAtEnd,
                possibleEnchants.get((start + 33 + flipAtEnd) % possibleEnchants.size()));
        return possibleEnchants.get((start + 33 + flipAtEnd) % possibleEnchants.size());
    }

    private int taskId, index = 0, lap = 1;

    public void startDecrypting(Player player, ItemStack item, int phase, int countdown, int interval,
            List<String> enchants, int start, int flipAtEnd, String result) {
        player.openInventory(getCrystalOpeningInventory());
        this.index += countdown;
        int[] slots = { 10, 11, 12, 13, 14, 15, 16 };
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (index == 0) {
                    switch (phase) {
                        case 1:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item, 2, 8, 3, enchants, (start + 12) % enchants.size(), flipAtEnd,
                                    result);
                            break;
                        case 2:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item, 3, 6, 5, enchants, (start + 8) % enchants.size(), flipAtEnd,
                                    result);
                            break;
                        case 3:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item, 4, 4, 10, enchants, (start + 6) % enchants.size(), flipAtEnd,
                                    result);
                            break;
                        case 4:
                            stopDecrypting();
                            lap = 1;
                            startDecrypting(player, item, 5, 3, 20, enchants, (start + 4) % enchants.size(), flipAtEnd,
                                    result);
                            // startDecrypting(player, 5, (int) (Math.random() * 7), 40, enchants,
                            // (start+3)%enchants.size());
                            break;
                        default:
                            stopDecrypting();
                            if (flipAtEnd == 1)
                                for (int k = 0; k < 7; k++) {
                                    player.getOpenInventory().setItem(slots[k], items.getEnchantInformation(
                                            enchants.get((start + k + lap - 3 + enchants.size()) % enchants.size())));
                                }
                            tryNaturalEnchant(player, item, result);
                            break;
                    }
                } else {
                    if (!player.getOpenInventory().getOriginalTitle().equals("§8Decrypting...")) {
                        stopDecrypting();
                        tryNaturalEnchant(player, item, result);
                        return;
                    }
                    for (int glassSlot : glassSlots) {
                        player.getOpenInventory().setItem(glassSlot, getRandomGlass());
                    }
                    for (int k = 0; k < 7; k++) {
                        player.getOpenInventory().setItem(slots[k], items.getEnchantInformation(
                                enchants.get((start + k + lap - 3 + enchants.size()) % enchants.size())));
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
