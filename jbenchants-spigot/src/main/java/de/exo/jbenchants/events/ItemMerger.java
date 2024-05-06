package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class ItemMerger implements Listener {

    API api = Main.instance.api;
    JBEnchantLore lore = Main.instance.lore;
    JBEnchantItems items = Main.instance.items;
    JBEnchantNBT nbt = Main.instance.nbt;

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
                        String randomEnchant = nbt.getEnchants(destination).get(new Random().nextInt(nbt.getEnchants(destination).size()));
                        nbt.removeEnchantment(destination, randomEnchant);
                        lore.updateLore(destination);
                        player.setItemOnCursor(null);
                        player.sendMessage("§cYour cleanser failed, and removed the "+api.getColor(api.getRarity(randomEnchant))+api.getDisplayName(randomEnchant));  // TBA
                    }
                }
            } else if (originNbt.hasTag("dust")) {
                if (destinationNbt.hasTag("crystal") && destinationNbt.getString("chance").split("-").length == 1) {
                    if (Objects.equals(originNbt.getString("dust"), destinationNbt.getString("crystal"))) {
                        Bukkit.broadcastMessage(destinationNbt.getInteger("chance") +" "+ originNbt.getInteger("chance"));
                        int oldChance = Integer.parseInt(destinationNbt.getString("chance"));
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
