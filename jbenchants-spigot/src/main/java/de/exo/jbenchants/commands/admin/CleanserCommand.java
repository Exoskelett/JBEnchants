package de.exo.jbenchants.commands.admin;

import de.exo.jbenchants.items.cleanser.Cleanser;
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

public class CleanserCommand implements CommandExecutor, TabCompleter {
    private final Cleanser cleanser = Cleanser.getInstance();

    String cleanserSyntax = "§c/cleanser [player] <amount> <chance>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            try {
                Player target = Bukkit.getPlayer(args[0]);
                ItemStack item = null;
                switch (args.length) {
                    case 1:  // player
                        item = cleanser.getCleanser();
                        sender.sendMessage(target.getDisplayName() + " §7received §f1x " + item.getItemMeta().getDisplayName());
                        break;
                    case 2:  // player + amount
                        item = cleanser.getCleanser();
                        item.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + item.getItemMeta().getDisplayName());
                        break;
                    case 3:  // player + amount + chance
                        item = cleanser.getCleanser(Integer.parseInt(args[2]));
                        item.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + item.getItemMeta().getDisplayName());
                        break;
                    default:
                        sender.sendMessage(cleanserSyntax);
                }
                target.getInventory().addItem(item);
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