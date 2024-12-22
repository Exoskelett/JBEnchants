package de.exo.jbenchants.items.dust;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.items.crystal.Crystal;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class DustHandler implements Listener {
    private static DustHandler INSTANCE;

    private DustHandler() {
    }

    public static DustHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DustHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    private final Crystal crystal = Crystal.getInstance();

//    @EventHandler
//    public void onDustMerge(InventoryClickEvent event) {
//        try {
//            if (!event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
//            ItemStack origin = event.getCursor();
//            NBTItem originNbt = new NBTItem(origin);
//            ItemStack destination = event.getCurrentItem();
//            if (destination == null || !originNbt.hasTag("dust") || Objects.requireNonNull(event.getCursor()).getAmount() != 1) return;
//            NBTItem destinationNbt = new NBTItem(destination);
//            Player player = (Player) event.getWhoClicked();
//            if (destinationNbt.getString("chance").split("-").length == 1) {
//                if (Objects.equals(originNbt.getString("dust"), destinationNbt.getString("crystal"))) {
//                    event.setCancelled(true);
//                    int oldChance = Integer.parseInt(destinationNbt.getString("chance"));
//                    int newChance = oldChance + originNbt.getInteger("chance");
//                    if (newChance > 100) newChance = 100;
//                    ItemStack newCrystal = crystal.getCrystal(destinationNbt.getString("crystal"), newChance);
//                    destination.setAmount(destination.getAmount()-1);
//                    player.getInventory().setItem(event.getSlot(), destination);
//                    if (player.getInventory().getItem(event.getSlot()) == null) {
//                        player.getInventory().setItem(event.getSlot(), newCrystal);
//                    } else
//                        player.getInventory().addItem(newCrystal);
//                    player.setItemOnCursor(null);
//                }
//            }
//        } catch (NullPointerException ignored) {
//        }
//    }

    public boolean updateDust(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta != null ? meta.lore() : null;
        if (!nbti.hasTag("dust") && lore != null && lore.size() > 2) {
            if (Objects.equals(lore.get(2).toString(), api.getItemLore("dust")[2])) {
                String rarity = lore.get(0).toString().split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("dust", rarity);
                String chance = lore.get(1).toString().split(" ")[3].substring(2).replace("%", "");
                nbti.setString("chance", chance);
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }
}
