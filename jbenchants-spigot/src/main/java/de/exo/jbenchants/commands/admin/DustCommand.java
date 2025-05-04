package de.exo.jbenchants.commands.admin;

import de.exo.jbenchants.items.dust.Dust;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DustCommand implements CommandExecutor, TabCompleter {
    private final Dust dust = Dust.getInstance();

    String dustSyntax = "§c/dust [player] [amount] <rarity> <chance>";
    List<String> rarities = Arrays.asList("common", "rare", "epic", "legendary");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            try {
                Player target = Bukkit.getPlayer(args[0]);
                ItemStack item = null;
                switch (args.length) {
                    case 1:  // player
                        item = dust.getDust("random");
                        sender.sendMessage(target.getDisplayName() + " §7received §f1x " + item.getItemMeta().getDisplayName());
                        break;
                    case 2:  // player + amount
                        item = dust.getDust("random");
                        item.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + item.getItemMeta().getDisplayName());
                        break;
                    case 3:  // player + amount + rarity
                        if (!rarities.contains(args[2].toLowerCase())) {
                            sender.sendMessage("§cPlease select a valid rarity: [common, rare, epic, legendary]");
                            break;
                        }
                        item = dust.getDust(args[2]);
                        item.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + item.getItemMeta().getDisplayName());
                        break;
                    case 4:  // player + amount + rarity + chance
                        if (!rarities.contains(args[2].toLowerCase())) {
                            sender.sendMessage("§cPlease select a valid rarity: [common, rare, epic, legendary]");
                            break;
                        }
                        item = dust.getDust(args[2], Integer.parseInt(args[3]));
                        item.setAmount(Integer.parseInt(args[1]));
                        sender.sendMessage(target.getDisplayName() + " §7received §f" + args[1] + "x " + item.getItemMeta().getDisplayName());
                        break;
                    default:
                        sender.sendMessage(dustSyntax);
                }
                if (item != null) target.getInventory().addItem(item);
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
            sender.sendMessage(dustSyntax);
        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();
        if (args.length > 0)
            switch (args.length) {
                case 3:
                    completer.add("common");
                    completer.add("rare");
                    completer.add("epic");
                    completer.add("legendary");
                    return completer;
                case 2:
                case 4:
                    completer.add("");
                    return completer;
            }
        return null;
    }
}
