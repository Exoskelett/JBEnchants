package de.exo.jbenchants.items.scroll;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.tr7zw.nbtapi.NBTItem;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairScrollHandler implements Listener {
    private static RepairScrollHandler INSTANCE;

    public static RepairScrollHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RepairScrollHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();

    private final EnchantsNBT enchantsNBT = EnchantsNBT.getInstance();

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
        List<Component> lore = (meta != null) ? meta.lore() : null;
        if (!nbti.hasTag("scroll") && lore != null && lore.size() > 3 &&
                Objects.equals(((Component)lore.get(3)).toString(), this.api.getItemLore("scroll")[3])) {
            String rarity = ((Component)lore.get(0)).toString().split(" ")[1].substring(2).substring(0, 1).toUpperCase();
            nbti.setString("scroll", rarity);
            String chance = ((Component)lore.get(1)).toString().split(" ")[2].substring(2).replace("%", "");
            nbti.setString("chance", chance);
            nbti.applyNBT(item);
            return true;
        }
        return false;
    }
}
