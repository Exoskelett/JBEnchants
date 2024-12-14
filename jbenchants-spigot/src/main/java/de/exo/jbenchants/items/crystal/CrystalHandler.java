package de.exo.jbenchants.items.crystal;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsHandler;
import de.exo.jbenchants.enchants.EnchantsItems;
import de.exo.jbenchants.enchants.EnchantsLore;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CrystalHandler implements Listener {
    private static CrystalHandler INSTANCE;

    private CrystalHandler() {
    }

    public static CrystalHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrystalHandler();
        return INSTANCE;
    }

    private final API api = Main.getAPI();
    private final Crystal crystal = Crystal.getInstance();
    private final CrystalGUI gui = CrystalGUI.getInstance();
    private final CrystalNBT nbt = CrystalNBT.getInstance();
    private final CrystalMeta lore = CrystalMeta.getInstance();

    private final EnchantsHandler enchantsHandler = EnchantsHandler.getInstance();
    private final EnchantsItems enchantsItems = EnchantsItems.getInstance();
    private final EnchantsLore enchantsLore = EnchantsLore.getInstance();
    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String checkCrystalReturn = nbt.getUsedCrystalChance(player);
        if (checkCrystalReturn != null && player.getInventory().firstEmpty() != -1) {
            String[] returnedCrystal = checkCrystalReturn.split("-");
            player.getInventory().addItem(crystal.getCrystal(returnedCrystal[0], Integer.parseInt(returnedCrystal[1])));
            nbt.setUsedCrystalChance(event.getPlayer(), returnedCrystal[0], -1);
            player.sendMessage(
                    "§cOopsies, seems like we've encountered an issue with returning a crystal to you before.\nThe same crystal got refunded to your inventory.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item == null || !event.getView().getOriginalTitle().equals("§8Decrypting...")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onCrystalMerge(InventoryClickEvent event) {
        try {
            if (!event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
            ItemStack origin = event.getCursor();
            NBTItem originNbt = new NBTItem(origin);
            ItemStack destination = event.getCurrentItem();
            if (destination == null || !originNbt.hasTag("scroll") || Objects.requireNonNull(event.getCursor()).getAmount() != 1) return;
            NBTItem destinationNbt = new NBTItem(destination);
            Player player = (Player) event.getWhoClicked();
            if (!originNbt.hasTag("crystal") || enchantsNBT.getCategory(destination) == null || originNbt.getString("chance").split("-").length != 1) return;
            if (enchantsHandler.getPossibleEnchants(player, destination, originNbt.getString("crystal")) != null) {
                event.setCancelled(true);
                double chance = (double) Integer.parseInt(originNbt.getString("chance")) / 100;
                player.setItemOnCursor(null);
                if (Math.random() <= chance) {
                    nbt.setUsedCrystalChance(player, originNbt.getString("crystal"), Integer.parseInt(originNbt.getString("chance")));
                    // String addedEnchant = crystal.unlockCrystal(player, destination, originNbt.getString("crystal"));
                } else {
                    player.sendMessage("§cYour crystal failed.");
                }
            } else {
                event.setCancelled(true);
                player.sendMessage("§cThere are no more enchantments of this tier left for you to get, please try different crystal rarities.");
            }
        } catch (NullPointerException ignored) {
        }
    }

    private void tryNaturalEnchant(Player player, ItemStack item, String enchant) { // adds enchant to item from crystal opening, if possible
        LevelEnchants level = LevelEnchants.getPlayerMaxEnchants(player);
        if (player.isOnline()) {
            if (enchantsHandler.checkEnchantUpgradability(player, item, enchant)) {
                enchantsNBT.addEnchantmentLevel(item, enchant, 1);
                nbt.setUsedCrystalChance(player, api.getRarity(enchant), -1);
                enchantsLore.updateLore(item);
                player.updateInventory();
                player.sendMessage("§a§l+ " + api.getColor(api.getRarity(enchant)) + api.getDisplayName(enchant)
                        + " §7has been added to your item.");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50, 1);
            } else {
                if (player.getInventory().firstEmpty() != -1) {
                    String[] checkCrystalReturn = nbt.getUsedCrystalChance(player).split("-");
                    String rarity = checkCrystalReturn[0];
                    int chance = Integer.parseInt(checkCrystalReturn[1]);
                    nbt.setUsedCrystalChance(player, rarity, -1);
                    if (level.nextMaxEnchants == -1) { // check whether player reached personal or total enchantment limit
                        player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"
                                + level.maxEnchants + "§f enchantments. You've been returned your crystal.");
                    } else
                        player.sendMessage("§c§lHey! §fWe couldn't fit the enchant onto your item as the max is §c§n"
                                + level.maxEnchants + "§f enchantments. You've been returned your crystal."
                                + "§7§oLevelup to §e§n§o" + level.nextLevel + "§7§o to have §c§n§o"
                                + level.nextMaxEnchants + "§7§o enchants");
                    if (chance > 4) { // check whether crystal can be returned
                        player.getInventory().addItem(crystal.getCrystal(rarity, chance - 4));
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

    public String unlockCrystal(Player player, ItemStack item, String rarity) {
        List<String> possibleEnchants = enchantsHandler.getPossibleEnchants(player, item, rarity);
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
        player.openInventory(gui.getCrystalOpeningInventory());
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
                            // (start+3)%enchants.size());
                            break;
                        default:
                            stopDecrypting();
                            if (flipAtEnd == 1)
                                for (int k = 0; k < 7; k++) {
                                    player.getOpenInventory().setItem(slots[k], enchantsItems.getEnchantInformation(
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
                    for (int glassSlot : gui.glassSlots) {
                        player.getOpenInventory().setItem(glassSlot, gui.getRandomGlass());
                    }
                    for (int k = 0; k < 7; k++) {
                        player.getOpenInventory().setItem(slots[k], enchantsItems.getEnchantInformation(
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

    public boolean updateCrystal(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta != null ? meta.lore() : null;
        if (!nbti.hasTag("crystal") && lore != null && item.getType().equals(Material.NETHER_STAR)) {
            if (Objects.equals(lore.get(3).toString(), api.getItemLore("crystal")[3])) {
                String rarity = lore.get(0).toString().split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("crystal", rarity);
                String chance = lore.get(1).toString().split(" ")[2].substring(2).replace("%", "");
                nbti.setString("chance", chance);
                nbti.applyNBT(item);
                return true;
            } else if (Objects.equals(lore.get(2).toString(), api.getItemLore("mysteryCrystal")[2])) {
                String rarity = lore.get(0).toString().split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("crystal", rarity);
                String chance = lore.get(6).toString().split(" ")[1].substring(2).replace("%", "");
                switch (chance.split("-").length) {
                    case 1:
                        nbti.setString("chance", "0-100");
                        break;
                    case 2:
                        nbti.setString("chance", chance);
                        break;
                }
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }
}
