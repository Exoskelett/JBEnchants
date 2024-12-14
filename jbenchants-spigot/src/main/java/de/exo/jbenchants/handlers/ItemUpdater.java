package de.exo.jbenchants.handlers;

import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.items.cleanser.CleanserHandler;
import de.exo.jbenchants.items.crystal.CrystalHandler;
import de.exo.jbenchants.items.dust.DustHandler;
import de.exo.jbenchants.items.scroll.RepairScrollHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUpdater implements Listener {
    private static ItemUpdater INSTANCE;

    private ItemUpdater() {
    }

    public static ItemUpdater getInstance() {
        if (INSTANCE == null) INSTANCE = new ItemUpdater();
        return INSTANCE;
    }

    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();
    private final CrystalHandler crystalHandler = CrystalHandler.getInstance();
    private final CleanserHandler cleanserHandler = CleanserHandler.getInstance();
    private final DustHandler dustHandler = DustHandler.getInstance();
    private final RepairScrollHandler repairScrollHandler = RepairScrollHandler.getInstance();

    @EventHandler
    public void updateItem(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (!item.getItemMeta().hasLore() || event.getView().getOriginalTitle().contains("§8")) return;
            if (enchantsNBT.updateEnchantedItem(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated enchanted item in your inventory got updated.");
            } else if (crystalHandler.updateCrystal(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated crystal in your inventory got updated.");
            } else if (cleanserHandler.updateCleanser(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated cleanser in your inventory got updated.");
            } else if (dustHandler.updateDust(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated dust in your inventory got updated.");
            } else if (repairScrollHandler.updateScroll(item)) {
                event.setCancelled(true);
                player.sendMessage("§7An outdated repair scroll in your inventory got updated.");
            }
        } catch (NullPointerException ignored) {
        }
    }

}
