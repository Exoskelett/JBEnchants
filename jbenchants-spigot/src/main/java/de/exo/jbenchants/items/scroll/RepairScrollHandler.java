package de.exo.jbenchants.items.scroll;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
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

public class RepairScrollHandler implements Listener {
    private static RepairScrollHandler INSTANCE;

    private RepairScrollHandler() {
    }

    public static RepairScrollHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepairScrollHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();

    @EventHandler
    public void onRepairScrollMerge(InventoryClickEvent event) {
        try {
            if (!event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
            ItemStack origin = event.getCursor();
            NBTItem originNbt = new NBTItem(origin);
            ItemStack destination = event.getCurrentItem();
            if (destination == null || !originNbt.hasTag("scroll") || Objects.requireNonNull(event.getCursor()).getAmount() != 1) return;
            Player player = (Player) event.getWhoClicked();
            if (!originNbt.hasTag("scroll")) return;
            event.setCancelled(true);
            double chance = (double) originNbt.getInteger("chance")/100;
            if (Math.random() <= chance) {
                enchantsNBT.repairTool(destination, getScrollDurability(originNbt.getString("scroll")));
                player.setItemOnCursor(null);
            } else {
                player.setItemOnCursor(null);
                player.sendMessage("This scroll sucked!");  // TBA
            }
        } catch (NullPointerException ignored) {
        }
    }

    public int getScrollDurability(String rarity) {
        switch (rarity) {
            case "legendary":
                return 300;
            case "epic":
                return 150;
            case "rare":
                return 100;
            case "common":
                return 50;
        }
        return 0;
    }

    public boolean updateScroll(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta != null ? meta.lore() : null;
        if (!nbti.hasTag("scroll") && lore != null && lore.size() > 3) {
            if (Objects.equals(lore.get(3).toString(), api.getItemLore("scroll")[3])) {
                String rarity = lore.get(0).toString().split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("scroll", rarity);
                String chance = lore.get(1).toString().split(" ")[2].substring(2).replace("%", "");
                nbti.setString("chance", chance);
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }
}
