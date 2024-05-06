package de.exo.jbenchants.events;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantHandler;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUpdater implements Listener {

    JBEnchantNBT nbt = Main.instance.nbt;
    JBEnchantHandler handler = Main.instance.handler;
    JBEnchantItems items = Main.instance.items;

    @EventHandler
    public void onItemClickUpdate(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            if (handler.updateCrystal(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated crystal in your inventory got updated.");
            } else if (handler.updateCleanser(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated cleanser in your inventory got updated.");
            } else if (handler.updateDust(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated dust in your inventory got updated.");
            } else if (handler.updateScroll(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated repair scroll in your inventory got updated.");
            }
        }
    }

    @EventHandler
    public void onMysteryCrystalRClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        try {
            NBTItem nbti = new NBTItem(item);
            if (nbti.getString("chance").split("-").length == 2) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    int low = Integer.parseInt(nbti.getString("chance").split("-")[0]);
                    int high = Integer.parseInt(nbti.getString("chance").split("-")[1]);
                    int chance = (int) (Math.random()*(high-low)+low);
                    int slot = player.getInventory().getHeldItemSlot();
                    item.setAmount(item.getAmount()-1);
                    player.getInventory().setItem(slot, item);
                    player.getInventory().addItem(items.getCrystal(nbti.getString("crystal"), chance));
                    player.updateInventory();
                } else
                    event.setCancelled(false);
            }
        } catch (NullPointerException ignored) {
        }
    }

}
