package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantHandler;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.exo.jbenchants.items.Crystal;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class ItemMerger implements Listener {

    private static ItemMerger INSTANCE;
    private ItemMerger() {
    }
    public static ItemMerger getInstance() {
        if (INSTANCE == null) INSTANCE = new ItemMerger();
        return INSTANCE;
    }

    API api = Main.getAPI();
    JBEnchantLore lore = JBEnchantLore.getInstance();
    JBEnchantItems items = JBEnchantItems.getInstance();
    JBEnchantNBT nbt = JBEnchantNBT.getInstance();
    JBEnchantHandler handler = JBEnchantHandler.getInstance();
    GUIHandler guiHandler = GUIHandler.getInstance();
    Crystal crystal = Crystal.getInstance();

    @EventHandler
    public void onItemMerge(InventoryClickEvent event) {
        if (!event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
        if (Objects.requireNonNull(event.getCursor()).getAmount() != 1) return;
        Player player = (Player) event.getWhoClicked();
        try {
            ItemStack origin = event.getCursor();
            NBTItem originNbt = new NBTItem(origin);
            ItemStack destination = event.getCurrentItem();
            NBTItem destinationNbt = new NBTItem(destination);
            if (!originNbt.hasTag("crystal") && !originNbt.hasTag("cleanser") && !originNbt.hasTag("dust") && !originNbt.hasTag("scroll")) return;
            if (originNbt.hasTag("crystal") && originNbt.getString("chance").split("-").length == 1) {
                if (nbt.getCategory(destination) != null) {
                    if (crystal.getPossibleEnchants(player, destination, originNbt.getString("crystal")) != null) {
                        event.setCancelled(true);
                        double chance = (double) Integer.parseInt(originNbt.getString("chance")) / 100;
                        player.setItemOnCursor(null);
                        if (Math.random() <= chance) {
                            nbt.setUsedItemChance(player, originNbt.getString("crystal"), Integer.parseInt(originNbt.getString("chance")));
                            String addedEnchant = crystal.unlockCrystal(player, destination, originNbt.getString("crystal"));
                        } else {
                            player.sendMessage("§cYour crystal failed.");
                        }
                    } else {
                        event.setCancelled(true);
                        player.sendMessage("§cThere are no more enchantments of this tier left for you to get, please try different crystal rarities.");
                    }
                }
            } else if (originNbt.hasTag("cleanser")) {
                if (destinationNbt.hasTag("jbenchants")) {
                    event.setCancelled(true);
                    double chance = (double) originNbt.getInteger("cleanser")/100;
                    if (Math.random() <= chance) {
                        nbt.setUsedItemChance(player, "cleanser", originNbt.getInteger("cleanser"));
                        player.setItemOnCursor(null);
                        player.openInventory(guiHandler.getCleanserInventory(destination));
                        player.sendMessage("§cYour cleanser activated.");
                    } else {
                        player.setItemOnCursor(null);
                        String randomEnchant = nbt.getEnchants(destination).get(new Random().nextInt(nbt.getEnchants(destination).size()));
                        nbt.removeEnchantment(destination, randomEnchant);
                        lore.updateLore(destination);
                        player.sendMessage("§cYour cleanser failed and removed a random enchantment: "+api.getColor(api.getRarity(randomEnchant))+api.getDisplayName(randomEnchant));  // TBA
                    }
                }
            } else if (originNbt.hasTag("dust")) {
                if (destinationNbt.hasTag("crystal") && destinationNbt.getString("chance").split("-").length == 1) {
                    if (Objects.equals(originNbt.getString("dust"), destinationNbt.getString("crystal"))) {
                        event.setCancelled(true);
                        int oldChance = Integer.parseInt(destinationNbt.getString("chance"));
                        int newChance = oldChance + originNbt.getInteger("chance");
                        if (newChance > 100) newChance = 100;
                        ItemStack newCrystal = crystal.getCrystal(destinationNbt.getString("crystal"), newChance);
                        destination.setAmount(destination.getAmount()-1);
                        player.getInventory().setItem(event.getSlot(), destination);
                        if (player.getInventory().getItem(event.getSlot()) == null) {
                            player.getInventory().setItem(event.getSlot(), newCrystal);
                        } else
                            player.getInventory().addItem(newCrystal);
                        player.setItemOnCursor(null);
                    }
                }
            } else if (originNbt.hasTag("scroll")) {
                event.setCancelled(true);
                double chance = (double) originNbt.getInteger("chance")/100;
                if (Math.random() <= chance) {
                    int newDamage = destinationNbt.getInteger("Damage") - lore.getScrollDurability(originNbt.getString("scroll"));
                    if (newDamage < 0) newDamage = 0;
                    destinationNbt.setInteger("Damage", newDamage);
                    destinationNbt.applyNBT(destination);
                    player.setItemOnCursor(null);
                } else {
                    player.setItemOnCursor(null);
                    player.sendMessage("This scroll sucked!");  // TBA
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

}
