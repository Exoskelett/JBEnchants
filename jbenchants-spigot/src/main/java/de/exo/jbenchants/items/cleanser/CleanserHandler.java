package de.exo.jbenchants.items.cleanser;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CleanserHandler implements Listener {
    private static CleanserHandler INSTANCE;

    private CleanserHandler() {
    }

    public static CleanserHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CleanserHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    private final Cleanser cleanser = Cleanser.getInstance();
    private final CleanserGUI gui = CleanserGUI.getInstance();
    private final CleanserNBT nbt = CleanserNBT.getInstance();
    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();
    private final EnchantsMeta enchantsMeta = EnchantsMeta.getInstance();

//    @EventHandler
//    public void onCleanserMerge(InventoryClickEvent event) {
//        try {
//            if (!event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) return;
//            ItemStack origin = event.getCursor();
//            NBTItem originNbt = new NBTItem(origin);
//            ItemStack destination = event.getCurrentItem();
//            if (destination == null || !originNbt.hasTag("scroll") || Objects.requireNonNull(event.getCursor()).getAmount() != 1) return;
//            NBTItem destinationNbt = new NBTItem(destination);
//            Player player = (Player) event.getWhoClicked();
//            if (!originNbt.hasTag("cleanser") || !destinationNbt.hasTag("jbenchants")) return;
//            event.setCancelled(true);
//            double chance = (double) originNbt.getInteger("cleanser")/100;
//            if (Math.random() <= chance) {
//                nbt.setUsedCleanserChance(player, originNbt.getInteger("cleanser"));
//                player.setItemOnCursor(null);
//                player.openInventory(gui.getCleanserInventory(destination));
//                player.sendMessage("§cYour cleanser activated.");
//            } else {
//                player.setItemOnCursor(null);
//                String randomEnchant = enchantsNBT.getEnchants(destination).get(new Random().nextInt(enchantsNBT.getEnchants(destination).size()));
//                enchantsNBT.removeEnchantment(destination, randomEnchant);
//                enchantsMeta.updateLore(destination);
//                player.sendMessage("§cYour cleanser failed and removed a random enchantment: "+api.getColor(api.getRarity(randomEnchant))+api.getDisplayName(randomEnchant));  // TBA
//            }
//        } catch (NullPointerException ignored) {
//        }
//    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item == null || !event.getView().getOriginalTitle().equals("§8Cleanser §7§o(Click to remove)")) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == player.getInventory()) return;
        ItemStack enchItem = event.getClickedInventory().getItem(event.getClickedInventory().getSize() - 1);
        List<String> enchants = enchantsMeta.sortEnchants(enchantsNBT.getEnchants(enchItem));
        if (event.getSlot() < enchants.size()) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack count = player.getInventory().getItem(i);
                if (count != null && count.equals(enchItem)) {
                    enchantsNBT.removeEnchantment(enchItem, enchants.get(event.getSlot()).substring(2));
                    enchantsMeta.updateLore(enchItem);
                    nbt.setUsedCleanserChance(player,  -1);
                    player.getInventory().setItem(i, enchItem);
                    player.getOpenInventory().close();
                    return;
                }
            }
            player.getOpenInventory().close();
            player.sendMessage("§cSomething went wrong, please report this incident to staff!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!event.getView().getOriginalTitle().equals("§8Cleanser §7§o(Click to remove)") || nbt.getUsedCleanserChance(player) != -1) return;
        player.getInventory().addItem(cleanser.getCleanser(nbt.getUsedCleanserChance(player)));
        nbt.setUsedCleanserChance(player, -1);
    }

    public boolean updateCleanser(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta != null ? meta.lore() : null;
        if (!nbti.hasTag("cleanser") && lore != null && lore.size() > 2) {
            if (Objects.equals(lore.get(2).toString(), api.getItemLore("cleanser")[2])) {
                int chance = Integer.parseInt(lore.get(0).toString().split(" ")[2].substring(2).replace("%", ""));
                nbti.setInteger("chance", chance);
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }
}
