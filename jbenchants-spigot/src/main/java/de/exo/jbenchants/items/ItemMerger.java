package de.exo.jbenchants.items;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.commands.CrystalsCommand;
import de.exo.jbenchants.enchants.EnchantsHandler;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.items.cleanser.CleanserGUI;
import de.exo.jbenchants.items.cleanser.CleanserNBT;
import de.exo.jbenchants.items.crystal.Crystal;
import de.exo.jbenchants.items.crystal.CrystalHandler;
import de.exo.jbenchants.items.crystal.CrystalNBT;
import de.exo.jbenchants.items.scroll.RepairScrollHandler;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class ItemMerger implements Listener {

    API api = Main.getAPI();
    private final EnchantsMeta enchantsMeta = EnchantsMeta.getInstance();
    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();
    private final EnchantsHandler enchantsHandler = EnchantsHandler.getInstance();

    @EventHandler
    public void onItemMerge(InventoryClickEvent event) {
        ItemStack origin = event.getCursor();
        ItemStack destination = event.getCurrentItem();
        if (!event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR) || origin == null || destination == null || origin.getAmount() != 1) return;
        Player player = (Player) event.getWhoClicked();
        try {
            NBTItem originNbt = new NBTItem(origin);
            NBTItem destinationNbt = new NBTItem(destination);
            if (!originNbt.hasTag("crystal") && !originNbt.hasTag("cleanser") && !originNbt.hasTag("dust") && !originNbt.hasTag("scroll")) return;
            if (originNbt.hasTag("crystal") && originNbt.getString("chance").split("-").length == 1) {
                if (enchantsNBT.getCategory(destination) != null) {
                    if (enchantsHandler.getPossibleEnchants(player, destination, originNbt.getString("crystal")) != null) {
                        event.setCancelled(true);
                        double chance = (double) Integer.parseInt(originNbt.getString("chance")) / 100;
                        player.setItemOnCursor(null);
                        if (Math.random() <= chance) {
                            CrystalNBT.getInstance().setUsedCrystalChance(player, originNbt.getString("crystal"), Integer.parseInt(originNbt.getString("chance")));
                            String addedEnchant = CrystalHandler.getInstance().unlockCrystal(player, destination, originNbt.getString("crystal"));
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
                        CleanserNBT.getInstance().setUsedCleanserChance(player, originNbt.getInteger("cleanser"));
                        player.setItemOnCursor(null);
                        player.openInventory(CleanserGUI.getInstance().getCleanserInventory(destination));
                        player.sendMessage("§cYour cleanser activated.");
                    } else {
                        player.setItemOnCursor(null);
                        String randomEnchant = enchantsNBT.getEnchants(destination).get(new Random().nextInt(enchantsNBT.getEnchants(destination).size()));
                        enchantsNBT.removeEnchantment(destination, randomEnchant);
                        enchantsMeta.updateLore(destination);
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
                        ItemStack newCrystal = Crystal.getInstance().getCrystal(destinationNbt.getString("crystal"), newChance);
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
                    int newDamage = destinationNbt.getInteger("Damage") - RepairScrollHandler.getInstance().getScrollDurability(originNbt.getString("scroll"));
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
