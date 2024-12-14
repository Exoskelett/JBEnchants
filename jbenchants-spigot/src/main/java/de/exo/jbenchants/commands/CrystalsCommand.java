package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.items.crystal.Crystal;
import de.tr7zw.nbtapi.NBTEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CrystalsCommand implements CommandExecutor, Listener {
    API api = Main.getAPI();
    Crystal crystal = Crystal.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(getCrystalsInventory((Player) sender));
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item == null || !event.getView().getOriginalTitle().equals("§8Crystals")) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == player.getInventory() || event.getCurrentItem().getType() != Material.getMaterial(api.getItemMaterial("crystal"))) return;
        String rarity = event.getCurrentItem().getItemMeta().displayName().toString().split(" ")[0]
                .substring(2).toLowerCase();
        player = (Player) event.getWhoClicked();
        if (getPlayerCrystals(player, rarity) > 0) {
            if (player.getInventory().firstEmpty() != -1) {
                addPlayerCrystals(player, rarity, -1);
                player.getInventory().addItem(crystal.getCrystal(rarity));
                player.openInventory(new CrystalsCommand().getCrystalsInventory(player));
            } else
                player.sendMessage("§cYou don't have enough space in your inventory.");
        }
    }

    public Inventory getCrystalsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text().color(NamedTextColor.DARK_GRAY).content("Crystals").build());
        ItemStack crystal = new ItemStack(Material.getMaterial(api.getItemMaterial("crystal")));
        ItemMeta crystalMeta = crystal.getItemMeta();
        String[] rarities = {"Common", "Rare", "Epic", "Legendary"};
        for (int i = 0; i < 4; i++) {
            TextColor color = TextColor.fromHexString(api.getColor(rarities[i].toLowerCase()));
            crystalMeta.displayName(Component.text().color(color).content(rarities[i] + " Crystal").build());
            List<Component> loreComponents = Arrays.asList(
                Component.text().color(NamedTextColor.YELLOW).content("Amount: ").append(Component.text().color(NamedTextColor.LIGHT_PURPLE).content(String.valueOf(getPlayerCrystals(player, rarities[i].toLowerCase())))).build(),
                Component.text().color(NamedTextColor.GRAY).content("Click to claim ").append(Component.text().color(NamedTextColor.DARK_PURPLE).content("x1 ")).append(Component.text().color(color).content(rarities[i] + " Crystal")).append(Component.text().color(NamedTextColor.GRAY).content("!")).build()
            );
            crystalMeta.lore(loreComponents);
            crystal.setItemMeta(crystalMeta);
            inventory.setItem(10 + i * 2, crystal);
        }
        ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.displayName(Component.text().content(" ").build());
        spacer.setItemMeta(spacerMeta);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, spacer);
        }
        return inventory;
    }

    public void addPlayerCrystals(Player player, String rarity, int amount) {
        NBTEntity entity = new NBTEntity(player);
        entity.getPersistentDataContainer().setInteger(rarity, entity.getPersistentDataContainer().getInteger(rarity)+amount);
    }

    public int getPlayerCrystals(Player player, String rarity) {
        NBTEntity entity = new NBTEntity(player);
        return entity.getPersistentDataContainer().getInteger(rarity);
    }
}
