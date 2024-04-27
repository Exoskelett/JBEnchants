package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantHandler;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import org.bukkit.Bukkit;
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
    JBEnchantHandler handler = Main.instance.handler;
    JBEnchantItems items = Main.instance.items;

    String registerSyntax = "/jbe register [rarity] [name]";
    String addEnchantSyntax = "/jbe enchant add <enchantment> <level>";
    String setEnchantSyntax = "/jbe enchant set <enchantment> <level>";
    String removeEnchantSyntax = "/jbe enchant remove <enchantment>";
    String enchantSyntax = "/jbe enchant <player> <name> <level>";
    String normalCrystalSyntax = "/jbe crystal normal [player] [amount] <rarity> <chance>";
    String mysteryCrystalSyntax = "/jbe crystal mystery [player] [amount] <rarity> <low> <high>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (args[0]) {
            case "register":
                switch (args.length) {
                    case 1:
                        sender.sendMessage("§cSyntax: " + registerSyntax);
                        break;
                    case 3:
                        String rarity = args[1].toLowerCase();
                        String name = args[2].toLowerCase();
                        if (!api.check(name)) {
                            if (rarity.equals("common") || rarity.equals("rare") || rarity.equals("epic") || rarity.equals("legendary") || rarity.equals("special")) {
                                api.addEnchantment(name, name, rarity);
                                sender.sendMessage("§7You added the following enchant: " + api.getColor(api.getRarity(name))+api.getDisplayName(name));
                            } else
                                sender.sendMessage("§cPlease use one of the following rarities: <common, rare, epic, legendary>");
                        } else
                            sender.sendMessage("§cThis enchantment already exists. You can edit its basic attributes in the database.");
                        break;
                }
                break;
            case "enchant":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    ItemStack item = player.getInventory().getItemInMainHand();
                    switch (args.length) {
                        case 1:
                            player.sendMessage("§cSyntax: " + enchantSyntax);
                            break;
                        case 3:
                            String name = args[2].toLowerCase();
                            switch (args[1]) {
                                case "add":
                                    if (api.check(name)) {
                                        nbt.addEnchantmentLevel(item, name, 1);
                                        lore.updateLore(item);
                                        player.sendMessage("§7You changed your item's level of "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7to "+nbt.getEnchantmentLevel(item, name));
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
                                    break;
                                case "remove":
                                    if (nbt.getEnchants(item).contains(name)) {
                                        nbt.removeEnchantment(item, name);
                                        lore.updateLore(item);
                                    } else
                                        player.sendMessage("§cYour held item doesn't have this enchant applied.");
                                    break;
                            }
                            break;
                        case 4:
                            String action = args[1].toLowerCase();
                            int level;
                            try {
                                level = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage("§cSpecify a valid number as level. Not '"+args[3]+"'");
                                break;
                            }
                            name = args[2].toLowerCase();
                            switch (action) {
                                case "add":
                                    if (api.check(name)) {
                                        nbt.addEnchantmentLevel(item, name, level);
                                        lore.updateLore(item);
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                                case "set":
                                    if (api.check(name)) {
                                        nbt.setEnchantmentLevel(item, name, level);
                                        lore.updateLore(item);
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                            }
                            break;
                    }
                } else
                    sender.sendMessage("§cYou have to be a player to execute this command.");
                break;
            case "crystal":
                switch (args.length) {
                    case 1, 2:
                        sender.sendMessage("§cSyntax: " + normalCrystalSyntax);
                        sender.sendMessage("§cSyntax: " + mysteryCrystalSyntax);
                        break;
                    default:
                        Player target;
                        switch (args[1]) {
                            case "normal":
                                try {
                                    target = Bukkit.getPlayer(args[2]);
                                    ItemStack crystal = null;
                                    switch (args.length) {
                                        case 3:  // type + player
                                            crystal = items.getCrystal("random");
                                            sender.sendMessage(target.getDisplayName()+" §7received §f1x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 4:  // type + player + amount
                                            crystal = items.getCrystal("random");
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f1x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 5:  // type + player + amount + rarity
                                            crystal = items.getCrystal(args[4]);
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f"+args[3]+"x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 6:  // type + player + amount + rarity + chance
                                            crystal = items.getCrystal(args[4], Integer.parseInt(args[5]));
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f"+args[3]+"x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        default:
                                            sender.sendMessage("§cToo many arguments.");
                                    }
                                    target.getInventory().addItem(crystal);
                                } catch (NullPointerException e) {
                                    sender.sendMessage("§c'"+args[2]+"' is not online.");
                                    e.printStackTrace();
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§c'"+args[3]+"' is not a valid number.");
                                }
                                break;
                            case "mystery":
                                try {
                                    target = Bukkit.getPlayer(args[2]);
                                    ItemStack crystal = null;
                                    switch (args.length) {
                                        case 3:  // type + player
                                            crystal = items.getMysteryCrystal("random", 0, 100);
                                            sender.sendMessage(target.getDisplayName()+" §7received §f1x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 4:  // type + player + amount
                                            crystal = items.getMysteryCrystal("random", 0, 100);
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f1x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 5:  // type + player + amount + rarity
                                            crystal = items.getMysteryCrystal(args[4], 0, 100);
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f"+args[3]+"x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 6:  // type + player + amount + rarity + low chance
                                            crystal = items.getMysteryCrystal(args[4], Integer.parseInt(args[5]), 100);
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f"+args[3]+"x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        case 7:  // type + player + amount + rarity + low chance + high chance
                                            crystal = items.getMysteryCrystal(args[4], Integer.parseInt(args[5]), Integer.parseInt(args[6]));
                                            crystal.setAmount(Integer.parseInt(args[3]));
                                            sender.sendMessage(target.getDisplayName()+" §7received §f"+args[3]+"x "+crystal.getItemMeta().getDisplayName());
                                            break;
                                        default:
                                            sender.sendMessage("§cToo many arguments.");
                                    }
                                    target.getInventory().addItem(crystal);
                                } catch (NullPointerException e) {
                                    sender.sendMessage("§c'"+args[2]+"' is not online.");
                                    e.printStackTrace();
                                } catch (NumberFormatException e) {
                                    switch (args.length) {
                                        case 4, 5, 6, 7:
                                            sender.sendMessage("§c'"+args[args.length-1]+"' is not a valid number.");
                                    }
                                }
                                break;
                            default:
                                sender.sendMessage("§cSyntax: " + normalCrystalSyntax);
                                sender.sendMessage("§cSyntax: " + mysteryCrystalSyntax);
                        }
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
                completer.add("crystal");
                return completer;
            }
            switch (args[0]) {
                case "register":
                    switch (args.length) {
                        case 2:
                            completer.add("common");
                            completer.add("rare");
                            completer.add("epic");
                            completer.add("legendary");
                            completer.add("special");
                            return completer;
                        case 3:
                            completer.add("");
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
                                List<String> enchants = api.getEnchantments(nbt.getCategory(item));
                                switch (args[1]) {
                                    case "add", "set":
                                        for (int i = 0; i < enchants.size(); i++) {
                                            if (api.check(enchants.get(i), "active"))
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
                case "crystal":
                    switch (args.length) {
                        case 2:
                            completer.add("normal");
                            completer.add("mystery");
                            return completer;
                        case 5:
                            completer.add("common");
                            completer.add("rare");
                            completer.add("epic");
                            completer.add("legendary");
                            return completer;
                        case 4, 6, 7:
                            completer.add("");
                            return completer;
                    }
                    break;
            }
        }
        return null;
    }
}
