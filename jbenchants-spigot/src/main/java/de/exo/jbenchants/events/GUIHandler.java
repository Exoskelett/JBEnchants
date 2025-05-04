package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsItems;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import de.exo.jbenchants.items.crystal.Crystal;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHandler implements Listener {
    private static GUIHandler INSTANCE;

    public static GUIHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GUIHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();

    EnchantsItems items = EnchantsItems.getInstance();

    EnchantsMeta lore = EnchantsMeta.getInstance();

    EnchantsNBT nbt = EnchantsNBT.getInstance();

    EnchantsRegions regions = EnchantsRegions.getInstance();

    Crystal crystal = Crystal.getInstance();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder;
        Player player = (Player)event.getPlayer();
        switch (event.getView().getOriginalTitle()) {
            case "Hunter":
                holder = event.getInventory().getHolder();
                if (holder instanceof Chest && this.regions.checkBlock(((Chest)holder).getBlock(), "mine"))
                    ((Chest)holder).getBlock().setType(Material.AIR);
                break;
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player)event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest && this.regions.checkBlock(((Chest)holder).getBlock(), "mine") &&
                !this.nbt.checkTreasureHunter(((Chest)holder).getBlock(), player)) {
            event.setCancelled(true);
        } else if (holder instanceof Chest && this.regions.checkBlock(((Chest)holder).getBlock(), "mine")) {
            Inventory chestInventory = event.getInventory();
            player.getWorld().getBlockAt(event.getInventory().getLocation()).setType(Material.AIR);
            event.setCancelled(true);
            player.openInventory(chestInventory);
        }
    }
}
