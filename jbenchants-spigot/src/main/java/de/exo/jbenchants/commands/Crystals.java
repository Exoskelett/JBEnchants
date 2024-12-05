package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.events.GUIHandler;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.exo.jbenchants.items.Crystal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Crystals implements CommandExecutor {
    API api = Main.getAPI();
    JBEnchantNBT nbt = JBEnchantNBT.getInstance();
    Crystal crystal = Crystal.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(getCrystalsInventory((Player) sender));
        }
        return false;
    }

    public Inventory getCrystalsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§8Crystals");
        ItemStack crystal = new ItemStack(Material.NETHER_STAR);
        ItemMeta crystalMeta = crystal.getItemMeta();
        List<String> lore = new ArrayList<>();
        String[] rarities = {"Common", "Rare", "Epic", "Legendary"};
        for (int i = 0; i < 4; i++) {
            crystalMeta.setDisplayName(api.getColor(rarities[i].toLowerCase()) + rarities[i] + " Crystal");
            lore.clear();
            lore.add("§eAmount: §d" + nbt.getPlayerCrystals(player, rarities[i].toLowerCase()));
            lore.add("§7Click to claim §bx1 " + api.getColor(rarities[i].toLowerCase()) + rarities[i] + " Crystal§7!");
            crystalMeta.setLore(lore);
            crystal.setItemMeta(crystalMeta);
            inventory.setItem(10 + i * 2, crystal);
        }
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(" ");
        spacer.setItemMeta(spacerMeta);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, spacer);
        }
        return inventory;
    }
}
