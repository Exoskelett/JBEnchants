package de.exo.jbenchants.items.cleanser;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CleanserGUI {
    private static CleanserGUI INSTANCE;

    private CleanserGUI() {
    }

    protected static CleanserGUI getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CleanserGUI();
        return INSTANCE;
    }

    private final Cleanser cleanser = Cleanser.getInstance();

    public Inventory getCleanserInventory(ItemStack item) {
        Inventory inventory = Bukkit.createInventory(null, 18, Component.text("§8Cleanser §7§o(Click to remove)"));
        List<Component> enchants = item.lore();
        for (int i = 0; i < enchants.size() && i < 17; i++) {
            cleanser.getCleanserEnchantInformation(item, enchants.get(i).toString().substring(2));
            inventory.setItem(i, item);
        }
        inventory.setItem(17, item);
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.displayName(Component.text(" "));
        spacer.setItemMeta(spacerMeta);
        for (int k = 9; k < inventory.getSize(); k++) {
            if (inventory.getItem(k) == null)
                inventory.setItem(k, spacer);
        }
        return inventory;
    }
}
