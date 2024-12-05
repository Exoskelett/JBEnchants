package de.exo.jbenchants.events;

import de.exo.jbenchants.handlers.JBEnchantHandler;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.exo.jbenchants.items.Crystal;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUpdater implements Listener {

    private static ItemUpdater INSTANCE;
    private ItemUpdater() {
    }
    public static ItemUpdater getInstance() {
        if (INSTANCE == null) INSTANCE = new ItemUpdater();
        return INSTANCE;
    }

    JBEnchantNBT nbt = JBEnchantNBT.getInstance();
    JBEnchantHandler handler = JBEnchantHandler.getInstance();
    JBEnchantItems items = JBEnchantItems.getInstance();
    Crystal crystal = Crystal.getInstance();

    @EventHandler
    public void updateItem(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (!item.getItemMeta().hasLore()) return;
            if (event.getView().getOriginalTitle().contains("§8")) return;
            if (nbt.updateEnchantedItem(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated enchanted item in your inventory got updated.");
            } else if (nbt.updateCrystal(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated crystal in your inventory got updated.");
            } else if (nbt.updateCleanser(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated cleanser in your inventory got updated.");
            } else if (nbt.updateDust(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated dust in your inventory got updated.");
            } else if (nbt.updateScroll(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated repair scroll in your inventory got updated.");
            }
        } catch (NullPointerException ignored) {
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
                    player.getInventory().addItem(crystal.getCrystal(nbti.getString("crystal"), chance));
                    player.updateInventory();
                } else
                    event.setCancelled(false);
            }
        } catch (NullPointerException ignored) {
        }
    }

}
