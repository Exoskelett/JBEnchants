package de.exo.jbenchants.commands.admin;

import de.exo.jbenchants.items.crystal.Crystal;
import de.exo.jbenchants.items.mystery_crystal.MysteryCrystal;
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

public class CrystalCommand implements CommandExecutor, TabCompleter {
    private final Crystal crystal = Crystal.getInstance();
    private final MysteryCrystal mysteryCrystal = MysteryCrystal.getInstance();

    String normalCrystalSyntax = "§c/crystal normal [player] <amount> <rarity> <chance>";
    String mysteryCrystalSyntax = "§c/crystal mystery [player] <amount> <rarity> <low> <high>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 1) {
            try {
                Player target = Bukkit.getPlayer(args[1]);
                ItemStack item = null;
                switch (args[0]) {
                    case "normal":
                        switch (args.length) {
                            case 2: // type + player
                                item = crystal.getCrystal("random");
                                sender.sendMessage(
                                        target.getDisplayName() + " §7received §f1x " + item.getItemMeta().getDisplayName());
                                break;
                            case 3: // type + player + amount
                                item = crystal.getCrystal("random");
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            case 4: // type + player + amount + rarity
                                item = crystal.getCrystal(args[3]);
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            case 5: // type + player + amount + rarity + chance
                                item = crystal.getCrystal(args[3], Integer.parseInt(args[4]));
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            default:
                                sender.sendMessage("§cToo many arguments.");
                        }
                        target.getInventory().addItem(item);
                        break;
                    case "mystery":
                        switch (args.length) {
                            case 2: // type + player
                                item = mysteryCrystal.getMysteryCrystal("random", 0, 100);
                                sender.sendMessage(
                                        target.getDisplayName() + " §7received §f1x " + item.getItemMeta().getDisplayName());
                                break;
                            case 3: // type + player + amount
                                item = mysteryCrystal.getMysteryCrystal("random", 0, 100);
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            case 4: // type + player + amount + rarity
                                item = mysteryCrystal.getMysteryCrystal(args[3], 0, 100);
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            case 5: // type + player + amount + rarity + low chance
                                item = mysteryCrystal.getMysteryCrystal(args[3], Integer.parseInt(args[4]), 100);
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            case 6: // type + player + amount + rarity + low chance + high chance
                                item = mysteryCrystal.getMysteryCrystal(args[3], Integer.parseInt(args[4]),
                                        Integer.parseInt(args[5]));
                                item.setAmount(Integer.parseInt(args[2]));
                                sender.sendMessage(target.getDisplayName() + " §7received §f" + args[2] + "x "
                                        + item.getItemMeta().getDisplayName());
                                break;
                            default:
                                sender.sendMessage("§cToo many arguments.");
                        }
                        target.getInventory().addItem(item);
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] args) {
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
