package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.exo.jbenchants.items.Crystal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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

import java.util.Arrays;
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
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text().color(NamedTextColor.DARK_GRAY).content("Crystals").build());
        ItemStack crystal = new ItemStack(Material.NETHER_STAR);
        ItemMeta crystalMeta = crystal.getItemMeta();
        String[] rarities = {"Common", "Rare", "Epic", "Legendary"};
        for (int i = 0; i < 4; i++) {
            TextColor color = TextColor.fromHexString(api.getColor(rarities[i].toLowerCase()));
            crystalMeta.displayName(Component.text().color(color).content(rarities[i] + " Crystal").build());
            List<Component> loreComponents = Arrays.asList(
                Component.text().color(NamedTextColor.YELLOW).content("Amount: ").append(Component.text().color(NamedTextColor.LIGHT_PURPLE).content(String.valueOf(nbt.getPlayerCrystals(player, rarities[i].toLowerCase())))).build(),
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
}
