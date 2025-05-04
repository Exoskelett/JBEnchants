package de.exo.jbenchants.items.cleanser;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.tr7zw.nbtapi.NBTItem;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CleanserHandler implements Listener {
    private static CleanserHandler INSTANCE;

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player)event.getWhoClicked();
        if (item == null || !event.getView().getOriginalTitle().equals("to remove)"))
            return;
        event.setCancelled(true);
        if (event.getClickedInventory() == player.getInventory())
            return;
        ItemStack enchItem = event.getClickedInventory().getItem(event.getClickedInventory().getSize() - 1);
        List<String> enchants = this.enchantsMeta.sortEnchants(this.enchantsNBT.getEnchants(enchItem));
        if (event.getSlot() < enchants.size()) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack count = player.getInventory().getItem(i);
                if (count != null && count.equals(enchItem)) {
                    this.enchantsNBT.removeEnchantment(enchItem, ((String)enchants.get(event.getSlot())).substring(2));
                    this.enchantsMeta.updateLore(enchItem);
                    this.nbt.setUsedCleanserChance(player, -1);
                    player.getInventory().setItem(i, enchItem);
                    player.getOpenInventory().close();
                    return;
                }
            }
            player.getOpenInventory().close();
            player.sendMessage("went wrong, please report this incident to staff!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if (!event.getView().getOriginalTitle().equals("to remove)") || this.nbt.getUsedCleanserChance(player) != -1)
            return;
        player.getInventory().addItem(new ItemStack[] { this.cleanser.getCleanser(this.nbt.getUsedCleanserChance(player)) });
        this.nbt.setUsedCleanserChance(player, -1);
    }

    public boolean updateCleanser(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = (meta != null) ? meta.lore() : null;
        if (!nbti.hasTag("cleanser") && lore != null && lore.size() > 2 &&
                Objects.equals(((Component)lore.get(2)).toString(), this.api.getItemLore("cleanser")[2])) {
            int chance = Integer.parseInt(((Component)lore.get(0)).toString().split(" ")[2].substring(2).replace("%", ""));
            nbti.setInteger("chance", Integer.valueOf(chance));
            nbti.applyNBT(item);
            return true;
        }
        return false;
    }
}
