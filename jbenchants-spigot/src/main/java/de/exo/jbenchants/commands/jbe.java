package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantHandler;
import de.exo.jbenchants.handlers.JBEnchantItems;
import de.exo.jbenchants.handlers.JBEnchantLore;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class jbe implements CommandExecutor, TabCompleter {
    API api = Main.getAPI();
    JBEnchantNBT nbt = JBEnchantNBT.getInstance();
    JBEnchantLore lore = JBEnchantLore.getInstance();
    JBEnchantHandler handler = JBEnchantHandler.getInstance();
    JBEnchantItems items = JBEnchantItems.getInstance();

    String registerSyntax = "/jbe register [rarity] [name]";
    String addEnchantSyntax = "/jbe enchant add <enchantment> [level]";
    String setEnchantSyntax = "/jbe enchant set <enchantment> <level>";
    String removeEnchantSyntax = "/jbe enchant remove <enchantment>";
    String enchantSyntax = "/jbe enchant <add/set/remove> ...";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/jbe [register/enchant] <...>");
            return false;
        }
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
                                sender.sendMessage("§7You registered the following enchant: " + api.getColor(api.getRarity(name))+api.getDisplayName(name));
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
                                        player.sendMessage("§7You changed your item's level of "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7to "+nbt.getEnchantmentLevel(item, name)+".");
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
                                        player.sendMessage("§7You removed "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7from your item.");
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
                                        if (!nbt.getEnchants(item).contains(name) && level < 0) {
                                            player.sendMessage("§cYour held item doesn't have this enchant applied.");
                                            break;
                                        }
                                        nbt.addEnchantmentLevel(item, name, level);
                                        lore.updateLore(item);
                                        if (nbt.getEnchants(item).contains(name)) {
                                            player.sendMessage("§7You changed your item's level of "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7to "+nbt.getEnchantmentLevel(item, name)+".");
                                        } else
                                            player.sendMessage("§7You removed "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7from your item.");
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                                case "set":
                                    if (api.check(name)) {
                                        nbt.setEnchantmentLevel(item, name, level);
                                        lore.updateLore(item);
                                        if (nbt.getEnchants(item).contains(name)) {
                                            player.sendMessage("§7You changed your item's level of "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7to "+nbt.getEnchantmentLevel(item, name)+".");
                                        } else
                                            player.sendMessage("§7You removed "+api.getColor(api.getRarity(name))+api.getDisplayName(name)+" §7from your item.");
                                    } else
                                        sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                            }
                            break;
                    }
                } else
                    sender.sendMessage("§cYou have to be a player to execute this command.");
                break;
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
                                List<String> enchants = new ArrayList<>();
                                try {
                                    enchants = api.getEnchantments(nbt.getCategory(item));
                                } catch (CommandException ignored) { }
                                if (!enchants.isEmpty()) {
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
                                }
                            case 4:
                                switch (args[1]) {
                                    case "add":
                                        if (!item.getType().equals(Material.AIR)) {
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
                                        }
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
