package de.exo.jbenchants.commands;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantItems;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Crystal implements CommandExecutor, TabCompleter {

    JBEnchantItems items = Main.instance.items;

    String normalCrystalSyntax = "§c/crystal normal [player] <amount> <rarity> <chance>";
    String mysteryCrystalSyntax = "§c/crystal mystery [player] <amount> <rarity> <low> <high>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 1) {
            try {
                Player target = Bukkit.getPlayer(args[1]);
                ItemStack crystal = null;
                switch (args[0]) {
                    case "normal":
                        switch (args.length) {
                            case 2:  // type + player
                                crystal = items.getCrystal("random");
                                sender.sendMessage(target.getDisplayName() + " §7received §f1x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 3:  // type + player + amount
                                crystal = items.getCrystal("random");
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 4:  // type + player + amount + rarity
                                crystal = items.getCrystal(args[3]);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 5:  // type + player + amount + rarity + chance
                                crystal = items.getCrystal(args[3], Integer.parseInt(args[4]));
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            default:
                                sender.sendMessage("§cToo many arguments.");
                        }
                        sender.sendMessage(target.getDisplayName() + " §7received §f1x " + crystal.getItemMeta().getDisplayName());
                        target.getInventory().addItem(crystal);
                        break;
                    case "mystery":
                        switch (args.length) {
                            case 2:  // type + player
                                crystal = items.getMysteryCrystal("random", 0, 100);
                                sender.sendMessage(target.getDisplayName() + " §7received §f1x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 3:  // type + player + amount
                                crystal = items.getMysteryCrystal("random", 0, 100);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 4:  // type + player + amount + rarity
                                crystal = items.getMysteryCrystal(args[3], 0, 100);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 5:  // type + player + amount + rarity + low chance
                                crystal = items.getMysteryCrystal(args[3], Integer.parseInt(args[4]), 100);
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            case 6:  // type + player + amount + rarity + low chance + high chance
                                crystal = items.getMysteryCrystal(args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                                crystal.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x " + crystal.getItemMeta().getDisplayName());
                                break;
                            default:
                                sender.sendMessage("§cToo many arguments.");
                        }
                        target.getInventory().addItem(crystal);
                        break;
                    default:
                        sender.sendMessage(normalCrystalSyntax);
                        sender.sendMessage(mysteryCrystalSyntax);
                }
            } catch (NullPointerException e) {
                sender.sendMessage("§c'" + args[1] + "' is not online.");
            } catch (NumberFormatException e) {
                switch (args.length) {
                    case 3, 4, 5, 6:
                        sender.sendMessage("§c'" + args[args.length - 1] + "' is not a valid number.");
                }
            }
        } else {
            sender.sendMessage(normalCrystalSyntax);
            sender.sendMessage(mysteryCrystalSyntax);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();
        if (args.length > 0) {
            switch (args.length) {
                case 1:
                    completer.add("normal");
                    completer.add("mystery");
                    return completer;
                case 4:
                    completer.add("common");
                    completer.add("rare");
                    completer.add("epic");
                    completer.add("legendary");
                    return completer;
                case 3, 5, 6:
                    completer.add("");
                    return completer;
            }
        }
        return null;
    }
}
