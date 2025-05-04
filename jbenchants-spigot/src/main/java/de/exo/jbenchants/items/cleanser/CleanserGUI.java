package de.exo.jbenchants.items.cleanser;

import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CleanserGUI {
    private static CleanserGUI INSTANCE;

    public static CleanserGUI getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CleanserGUI();
        return INSTANCE;
    }

    private final Cleanser cleanser = Cleanser.getInstance();

    public Inventory getCleanserInventory(ItemStack item) {
        Inventory inventory = Bukkit.createInventory(null, 18, "to remove)");
        List<String> enchants = EnchantsMeta.getInstance().sortEnchants(EnchantsNBT.getInstance().getEnchants(item));
        for (int i = 0; i < enchants.size() && i < 17; i++)
            inventory.setItem(i, this.cleanser.getCleanserEnchantInformation(item, ((String)enchants.get(i)).substring(2)));
        inventory.setItem(17, item);
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(" ");
        spacer.setItemMeta(spacerMeta);
        for (int k = 9; k < inventory.getSize(); k++) {
            if (inventory.getItem(k) == null)
                inventory.setItem(k, spacer);
        }
        return inventory;
    }
}
