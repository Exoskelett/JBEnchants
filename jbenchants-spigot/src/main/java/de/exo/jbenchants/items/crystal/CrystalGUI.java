package de.exo.jbenchants.items.crystal;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrystalGUI {
    private static CrystalGUI INSTANCE;

    protected CrystalGUI() {
    }

    protected static CrystalGUI getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrystalGUI();
        return INSTANCE;
    }

    protected final int[] glassSlots = { 0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25, 26 };

    protected Inventory getCrystalOpeningInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("§8Decrypting..."));
        ItemStack decryptItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta decryptItemMeta = decryptItem.getItemMeta();
        decryptItemMeta.displayName(Component.text("§7Decrypting"));
        decryptItem.setItemMeta(decryptItemMeta);
        for (int glassSlot : glassSlots) inventory.setItem(glassSlot, getRandomGlass());
        inventory.setItem(4, decryptItem);
        inventory.setItem(22, decryptItem);
        return inventory;
    }

    protected ItemStack getRandomGlass() {
        Material[] glass = { Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE };
        return new ItemStack(glass[new java.util.Random().nextInt(glass.length)]);
    }

    protected void setRandomGlass(Player player) {
        Inventory inventory = (Inventory) player.getOpenInventory();
        for (int glassSlot : glassSlots) inventory.setItem(glassSlot, getRandomGlass());
    }
}
