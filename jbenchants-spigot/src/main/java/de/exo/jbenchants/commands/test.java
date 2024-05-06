package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class test implements CommandExecutor {
    API api = Main.instance.api;
    JBEnchantNBT nbt = Main.instance.nbt;
    JBEnchantLore lore = Main.instance.lore;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            NBTItem nbti = new NBTItem(item);
            switch (args.length) {
                case 0:
                    player.sendMessage("Debug:\n"
                            +"JBEnchants: ("+nbti.getInteger("jbenchants")+") "+nbt.getEnchants(item).toString()
                            +"\nLore: "+item.getLore().toString());
                    break;
                case 1:
                    switch (args[0]) {
                        case "nbt":
                            for (String keys : nbti.getKeys()) {
                                if (!nbti.getString(keys).isEmpty())
                                    player.sendMessage(keys + " - " + nbti.getString(keys));
                                if (nbti.getInteger(keys) != 0)
                                    player.sendMessage(keys + " - " + nbti.getInteger(keys));
                            }
                            break;
                        case "update":
                            player.sendMessage("Updating Lore:");
                            lore.updateLore(item);
                            break;
                        case "delete":
                            player.sendMessage("Deleting Lore:");
                            lore.deleteUnusedEnchants(item);
                            break;
                        case "deleteAll":
                            player.sendMessage("Deleting every Lore:");
                            lore.deleteAllEnchants(item);
                            break;
                        case "slots":
                            player.sendMessage("Enchantment-Slots: "+lore.getEnchantmentLoreSlots(item).toString());
                            break;
                        case "crystal":
                            player.sendMessage("Rarity: "+nbti.getString("crystal"));
                            player.sendMessage("Chance: "+nbti.getString("chance"));
                            break;
                    }
                case 2:
                    switch (args[0]) {
                        case "setCrystal":
                            nbti.setString("crystal", args[1]);
                            nbti.applyNBT(item);
                            break;
                        case "setChance":
                            nbti.setString("chance", args[1]);
                            nbti.applyNBT(item);
                            break;
                    }
            }
        }

        return false;
    }
}
