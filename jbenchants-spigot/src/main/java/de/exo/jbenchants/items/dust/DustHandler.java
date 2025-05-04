package de.exo.jbenchants.items.dust;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.items.crystal.Crystal;
import de.tr7zw.nbtapi.NBTItem;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DustHandler implements Listener {
    private static DustHandler INSTANCE;

    public static DustHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DustHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final Crystal crystal = Crystal.getInstance();

    public boolean updateDust(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = (meta != null) ? meta.lore() : null;
        if (!nbti.hasTag("dust") && lore != null && lore.size() > 2 &&
                Objects.equals(((Component)lore.get(2)).toString(), this.api.getItemLore("dust")[2])) {
            String rarity = ((Component)lore.get(0)).toString().split(" ")[1].substring(2).substring(0, 1).toUpperCase();
            nbti.setString("dust", rarity);
            String chance = ((Component)lore.get(1)).toString().split(" ")[3].substring(2).replace("%", "");
            nbti.setString("chance", chance);
            nbti.applyNBT(item);
            return true;
        }
        return false;
    }
}
