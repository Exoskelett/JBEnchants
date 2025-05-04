package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.items.crystal.Crystal;
import de.tr7zw.nbtapi.NBTEntity;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class CrystalsCommand implements CommandExecutor, Listener {
    API api = Main.getAPI();

    Crystal crystal = Crystal.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player)
            ((Player)sender).openInventory(getCrystalsInventory((Player)sender));
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player)event.getWhoClicked();
        if (item == null || !event.getView().getOriginalTitle().equals("§8Crystals")) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == player.getInventory() || event.getCurrentItem().getType() != Material.getMaterial(api.getItemMaterial("crystal")))
            return;
        String rarity = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[0].substring(2).toLowerCase();
        player = (Player)event.getWhoClicked();
        if (getPlayerCrystals(player, rarity) > 0)
            if (player.getInventory().firstEmpty() != -1) {
                addPlayerCrystals(player, rarity, -1);
                player.getInventory().addItem(new ItemStack[] { this.crystal.getCrystal(rarity) });
                player.openInventory(getCrystalsInventory(player));
            } else {
                player.sendMessage("§cYou don't have enough space in your inventory.");
            }
    }

    public Inventory getCrystalsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, "§8Crystals");
                ItemStack crystal = new ItemStack(Material.NETHER_STAR);
        ItemMeta crystalMeta = crystal.getItemMeta();
        List<String> lore = new ArrayList<>();
        String[] rarities = { "Common", "Rare", "Epic", "Legendary" };
        for (int i = 0; i < 4; i++) {
            crystalMeta.setDisplayName(api.getColor(rarities[i].toLowerCase()) + api.getColor(rarities[i].toLowerCase()) + " Crystal");
            lore.clear();
            lore.add("§eAmount: §d"+ getPlayerCrystals(player, rarities[i].toLowerCase()));
            lore.add("§7Click to claim §bx1 " + api.getColor(rarities[i].toLowerCase()) + rarities[i] + " Crystal§7!");
            crystalMeta.setLore(lore);
            crystal.setItemMeta(crystalMeta);
            inventory.setItem(10 + i * 2, crystal);
        }
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(" ");
        spacer.setItemMeta(spacerMeta);
        for (int j = 0; j < inventory.getSize(); j++) {
            if (inventory.getItem(j) == null)
                inventory.setItem(j, spacer);
        }
        return inventory;
    }

    public void addPlayerCrystals(Player player, String rarity, int amount) {
        NBTEntity entity = new NBTEntity((Entity)player);
        entity.getPersistentDataContainer().setInteger(rarity, entity.getPersistentDataContainer().getInteger(rarity).intValue() + amount);
    }

    public int getPlayerCrystals(Player player, String rarity) {
        NBTEntity entity = new NBTEntity((Entity)player);
        return entity.getPersistentDataContainer().getInteger(rarity).intValue();
    }
}
