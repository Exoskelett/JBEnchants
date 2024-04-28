package de.exo.jbenchants.events;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.io.BufferedReader;
import java.util.Objects;

public class Merging implements Listener {

    JBEnchantLore lore = Main.instance.lore;
    JBEnchantItems items = Main.instance.items;

    @EventHandler
    public void onItemMerge(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        try {
            ItemStack origin = event.getCursor();
            NBTItem originNbt = new NBTItem(origin);
            ItemStack destination = event.getCurrentItem();
            NBTItem destinationNbt = new NBTItem(destination);
            if (originNbt.hasTag("crystal")) {

            } else if (originNbt.hasTag("cleanser")) {
                if (destinationNbt.hasTag("jbenchants")) {
                    double chance = (double) originNbt.getInteger("cleanser")/100;
                    if (Math.random() <= chance) {
                        player.sendMessage("Yay, this worked! But there is no GUI yet, so... sorry about that");
                        // TBA
                    } else {
                        event.setCancelled(true);
                        player.setItemOnCursor(null);
                        player.sendMessage("This cleanser sucked!");  // TBA
                    }
                }
            } else if (originNbt.hasTag("dust")) {
                if (destinationNbt.hasTag("crystal")) {
                    if (Objects.equals(originNbt.getString("dust"), destinationNbt.getString("crystal"))) {
                        Bukkit.broadcastMessage(destinationNbt.getInteger("chance") +" "+ originNbt.getInteger("chance"));
                        int oldChance = destinationNbt.getInteger("chance");
                        int newChance = oldChance + originNbt.getInteger("chance");
                        if (newChance > 100) newChance = 100;
                        ItemStack newCrystal = items.getCrystal(destinationNbt.getString("crystal"), newChance);
                        destination.setAmount(destination.getAmount()-1);
                        player.getInventory().setItem(event.getSlot(), destination);
                        if (player.getInventory().getItem(event.getSlot()) == null) {
                            player.getInventory().setItem(event.getSlot(), newCrystal);
                        } else
                            player.getInventory().addItem(newCrystal);
                        origin.setAmount(origin.getAmount()-1);
                        player.setItemOnCursor(origin);
                        event.setCancelled(true);
                    }
                }
            } else if (originNbt.hasTag("scroll")) {
                double chance = (double) originNbt.getInteger("chance")/100;
                if (Math.random() <= chance) {
                    int newDamage = destinationNbt.getInteger("Damage")-lore.getScrollDurability(originNbt.getString("scroll"));
                    if (newDamage < 0) newDamage = 0;
                    destinationNbt.setInteger("Damage", newDamage);
                    destinationNbt.applyNBT(destination);
                    event.setCancelled(true);
                    player.setItemOnCursor(null);
                } else {
                    event.setCancelled(true);
                    player.setItemOnCursor(null);
                    player.sendMessage("This scroll sucked!");  // TBA
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

}
