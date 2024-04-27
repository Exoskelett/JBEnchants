package de.exo.jbenchants.commands;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantItems;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Cleanser implements CommandExecutor, TabCompleter {

    JBEnchantItems items = Main.instance.items;

    String cleanserSyntax = "§c/cleanser [player] <amount> <chance>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            try {
                Player target = Bukkit.getPlayer(args[0]);
                ItemStack cleanser = null;
                switch (args.length) {
                    case 1:  // player
                        cleanser = items.getCleanser();
                        sender.sendMessage(target.getDisplayName() + " §7received §f1x " + cleanser.getItemMeta().getDisplayName());
                        break;
                    case 2:  // player + amount
                        cleanser = items.getCleanser();
                        cleanser.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + cleanser.getItemMeta().getDisplayName());
                        break;
                    case 3:  // player + amount + chance
                        cleanser = items.getCleanser(Integer.parseInt(args[2]));
                        cleanser.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + cleanser.getItemMeta().getDisplayName());
                        break;
                    default:
                        sender.sendMessage(cleanserSyntax);
                }
                target.getInventory().addItem(cleanser);
            } catch (NullPointerException e) {
                sender.sendMessage("§c'" + args[0] + "' is not online.");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                switch (args.length) {
                    case 3, 4, 5, 6:
                        sender.sendMessage("§c'" + args[args.length - 1] + "' is not a valid number.");
                }
            }
        } else
            sender.sendMessage(cleanserSyntax);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();
        if (args.length > 0) {
            switch (args.length) {
                case 2, 3:
                    completer.add("");
                    return completer;
            }
        }
        return null;
    }
}