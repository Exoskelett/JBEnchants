package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantData;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.tr7zw.nbtapi.NBTItem;
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

public class jbe implements CommandExecutor, TabCompleter {
    API api = Main.instance.api;
    JBEnchantData.NBT nbt = Main.instance.nbt;

    String addSyntax = "/jbe add <name> [rarity] [level_cap] [proc_chance]";
    String addEnchantSyntax = "/jbe enchant add <enchantment> <level>";
    String setEnchantSyntax = "/jbe enchant set <enchantment> <level>";
    String removeEnchantSyntax = "/jbe enchant remove <enchantment>";
    String enchantSyntax = "/jbe enchant <player> <name> <level>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (args.length) {
            case 0:
                if (sender.hasPermission("jbe.add")) sender.sendMessage("§cSyntax:\n" + addSyntax);
                break;
            case 1:
                switch (args[0]) {
                    case "add":
                        sender.sendMessage("§cSyntax: " + addSyntax);
                        break;
                    case "enchant":
                        sender.sendMessage("§cSyntax: " + enchantSyntax);
                }
                break;
            case 3:
                switch (args[0]) {
                    case "enchant":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            String name = args[1].toLowerCase(), action = args[2];
                            ItemStack item = player.getItemInHand();
                            if (args[2].equals("remove")) {
                                if (new NBTItem(player.getItemInHand()).hasTag(name)) {
                                    nbt.removeEnchantment(item, name);
                                } else
                                    sender.sendMessage("§cYour held item doesn't have this enchant applied.");
                            }
                        }
                }
                break;
            case 4:
                switch (args[0]) {
                    case "enchant":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            String name = args[1].toLowerCase(), action = args[2];
                            int level = Integer.parseInt(args[3]);
                            ItemStack item = player.getItemInHand();
                            switch (args[1]) {
                                case "add":
                                    if (api.exists(name)) {
                                        nbt.addEnchantmentLevel(item, name, level);
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                case "set":
                                    if (api.exists(name)) {
                                        nbt.setEnchantmentLevel(item, name, level);
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                            }
                        } else
                            sender.sendMessage("§cYou have to be a player to execute this command.");
                }
                break;
            case 5:
                switch (args[0]) {
                    case "add":
                        String name = args[1].toLowerCase(), rarity = args[2].toLowerCase();
                        int level_cap = Integer.parseInt(args[3]);
                        double proc_chance = Double.parseDouble(args[4]);
                        if (api.exists(name)) {
                            sender.sendMessage(name + " " + rarity + " " + level_cap + " " + proc_chance);
                            if (rarity.equals("common") || rarity.equals("rare") || rarity.equals("epic") || rarity.equals("legendary")) {
                                api.addEnchantment(name, name, rarity, level_cap, proc_chance);
                                sender.sendMessage("§7You added the following enchant: " + api.getDisplayName(name));
                            } else
                                sender.sendMessage("§cPlease use one of the following rarities: <common, rare, epic, legendary>");
                        } else
                            sender.sendMessage("§cThis enchantment already exists. You can edit its basic attributes in the database.");
                }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();
        if (args.length > 0) {
            if (args.length == 1) {
                completer.add("add");
                completer.add("enchant");
                return completer;
            }
            switch (args[0]) {
                case "add":
                    switch (args.length) {
                        case 2:
                            completer.add("");
                            return completer;
                        case 3:
                            completer.add("common");
                            completer.add("rare");
                            completer.add("epic");
                            completer.add("legendary");
                            return completer;
                    }
                    break;
                case "enchant":
                        Player player = (Player) sender;
                        ItemStack item = player.getInventory().getItemInMainHand();
                        switch (args.length) {
                            case 2:
                                completer.add("add");
                                completer.add("set");
                                completer.add("remove");
                                return completer;
                            case 3:
                                List<String> enchants = api.getEnchantments();
                                if (args[1].equals("add") || args[1].equals("set")) {
                                    for (int i = 0; i < enchants.size(); i++) {
                                        if (api.check(enchants.get(i)))
                                            completer.add(enchants.get(i));
                                    }
                                    return completer;
                                } else if (args[1].equals("remove")) {
                                    if (nbt.getEnchants(item) != null) {
                                        completer.addAll(nbt.getEnchants(item));
                                        return completer;
                                    }
                                }
                            case 4:
                                switch (args[1]) {
                                    case "add":
                                        if (nbt.getEnchantmentLevel(item, args[2]) < api.getLevelCap(args[2])) {
                                            for (int i = 1; i <= (api.getLevelCap(args[2])-nbt.getEnchantmentLevel(item, args[2])); i++) {
                                                completer.add("" + i);
                                            }
                                        }
                                        return completer;
                                    case "set":
                                        for (int i = 1; i <= api.getLevelCap(args[1]); i++) {
                                            completer.add("" + i);
                                        }
                                        return completer;
                                }
                        }
                    break;
            }
        }
        return null;
    }
}
