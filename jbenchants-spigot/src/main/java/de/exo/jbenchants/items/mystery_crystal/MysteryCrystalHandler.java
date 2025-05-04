package de.exo.jbenchants.items.mystery_crystal;

import de.exo.jbenchants.items.crystal.Crystal;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MysteryCrystalHandler implements Listener {
    private static MysteryCrystalHandler INSTANCE;

    public static MysteryCrystalHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MysteryCrystalHandler();
        return INSTANCE;
    }

    private final Crystal crystal = Crystal.getInstance();

    @EventHandler
    public void onMysteryCrystalRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        try {
            NBTItem nbti = new NBTItem(item);
            if ((nbti.getString("chance").split("-")).length == 2)
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    int low = Integer.parseInt(nbti.getString("chance").split("-")[0]);
                    int high = Integer.parseInt(nbti.getString("chance").split("-")[1]);
                    int chance = (int)(Math.random() * (high - low) + low);
                    int slot = player.getInventory().getHeldItemSlot();
                    item.setAmount(item.getAmount() - 1);
                    player.getInventory().setItem(slot, item);
                    player.getInventory().addItem(new ItemStack[] { this.crystal.getCrystal(nbti.getString("crystal"), chance) });
                    player.updateInventory();
                } else {
                    event.setCancelled(false);
                }
        } catch (NullPointerException nullPointerException) {}
    }
}
