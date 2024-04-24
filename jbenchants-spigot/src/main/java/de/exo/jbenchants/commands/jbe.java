package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class jbe implements CommandExecutor, TabCompleter {
    API api = Main.instance.api;
    JBEnchantNBT nbt = Main.instance.nbt;
    JBEnchantLore lore = Main.instance.lore;

    String registerSyntax = "/jbe register <name> [rarity] [level_cap] [proc_chance]";
    String addEnchantSyntax = "/jbe enchant add <enchantment> <level>";
    String setEnchantSyntax = "/jbe enchant set <enchantment> <level>";
    String removeEnchantSyntax = "/jbe enchant remove <enchantment>";
    String enchantSyntax = "/jbe enchant <player> <name> <level>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (args.length) {
            case 0:
                if (sender.hasPermission("jbe.add")) sender.sendMessage("§cSyntax:\n" + registerSyntax);
                break;
            case 1:
                switch (args[0]) {
                    case "register":
                        sender.sendMessage("§cSyntax: " + registerSyntax);
                        break;
                    case "enchant":
                        sender.sendMessage("§cSyntax: " + enchantSyntax);
                        break;
                }
                break;
            case 3:
                switch (args[0]) {
                    case "register":
                        if (args[2].equalsIgnoreCase("special")) {
                            api.addEnchantment(args[1].toLowerCase(), args[1], "special", 0, 0);
                            sender.sendMessage("§7You added the following enchant: " + api.getEnchantmentColor(api.getRarity(args[1]))+api.getDisplayName(args[1]));
                        }
                        break;
                    case "enchant":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            String name = args[2].toLowerCase();
                            ItemStack item = player.getInventory().getItemInMainHand();
                            switch (args[1]) {
                                case "add":
                                    if (api.exists(name)) {
                                        nbt.addEnchantmentLevel(item, name, 1);
                                        lore.updateLore(item);
                                        player.sendMessage("§7You changed your item's level of "+api.getEnchantmentColor(api.getRarity(name))+api.getDisplayName(name)+" §7to "+nbt.getEnchantmentLevel(item, name));
                                    } else
                                        player.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                                case "set":
                                    if (nbt.getEnchants(item).contains(name)) {
                                        player.sendMessage("§cPlease specify a level, as your item already has this enchantment.");
                                    } else {
                                        nbt.setEnchantmentLevel(item, name, 1);
                                        lore.updateLore(item);
                                    }
                                case "remove":
                                    if (nbt.getEnchants(item).contains(name)) {
                                        nbt.removeEnchantment(item, name);
                                        lore.updateLore(item);
                                    } else
                                        player.sendMessage("§cYour held item doesn't have this enchant applied.");
                                    break;
                            }
                        }
                }
                break;
            case 4:
                switch (args[0]) {
                    case "enchant":
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            String name = args[2].toLowerCase();
                            int level = Integer.parseInt(args[3]);
                            ItemStack item = player.getInventory().getItemInMainHand();
                            switch (args[1]) {
                                case "add":
                                    if (api.exists(name)) {
                                        nbt.addEnchantmentLevel(item, name, level);
                                        lore.updateLore(item);
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                                case "set":
                                    if (api.exists(name)) {
                                        nbt.setEnchantmentLevel(item, name, level);
                                        lore.updateLore(item);
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                            }
                        } else
                            sender.sendMessage("§cYou have to be a player to execute this command.");
                }
                break;
            case 5:
                switch (args[0]) {
                    case "register":
                        String name = args[1].toLowerCase(), rarity = args[2].toLowerCase();
                        int level_cap = Integer.parseInt(args[3]);
                        double proc_chance = Double.parseDouble(args[4]);
                        if (!api.exists(name)) {
                            if (rarity.equals("common") || rarity.equals("rare") || rarity.equals("epic") || rarity.equals("legendary") || rarity.equals("special")) {
                                api.addEnchantment(name, name, rarity, level_cap, proc_chance);
                                sender.sendMessage("§7You added the following enchant: " + api.getEnchantmentColor(api.getRarity(name))+api.getDisplayName(name));
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
                completer.add("register");
                completer.add("enchant");
                return completer;
            }
            switch (args[0]) {
                case "register":
                    switch (args.length) {
                        case 2:
                            completer.add("");
                            return completer;
                        case 3:
                            completer.add("common");
                            completer.add("rare");
                            completer.add("epic");
                            completer.add("legendary");
                            completer.add("special");
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
                                switch (args[1]) {
                                    case "add", "set":
                                        for (int i = 0; i < enchants.size(); i++) {
                                            if (api.checkActive(enchants.get(i)))
                                                completer.add(enchants.get(i));
                                        }
                                        return completer;
                                    case "remove":
                                        if (nbt.getEnchants(item) != null) {
                                            completer.addAll(nbt.getEnchants(item));
                                            return completer;
                                        } else {
                                            completer.add("");
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
                                        } else {
                                            for (int i = 1; i <= api.getLevelCap(args[2]); i++) {
                                                completer.add("" + i);
                                            }
                                        }
                                        return completer;
                                    case "set":
                                        for (int i = 1; i <= api.getLevelCap(args[2]); i++) {
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
