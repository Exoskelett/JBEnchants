package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsHandler;
import de.exo.jbenchants.enchants.EnchantsItems;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class jbe implements CommandExecutor, TabCompleter {
    API api = Main.getAPI();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsMeta lore = EnchantsMeta.getInstance();
    EnchantsHandler handler = EnchantsHandler.getInstance();
    EnchantsItems items = EnchantsItems.getInstance();

    String registerSyntax = "/jbe register [rarity] [name]";
    String addEnchantSyntax = "/jbe enchant add <enchantment> [level]";
    String setEnchantSyntax = "/jbe enchant set <enchantment> <level>";
    String removeEnchantSyntax = "/jbe enchant remove <enchantment>";
    String enchantSyntax = "/jbe enchant <add/set/remove> ...";
    List<String> rarities = Arrays.asList("common", "rare", "epic", "legendary", "special");

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String rarity;
        String name;
        if (args.length == 0) {
            sender.sendMessage("§c/jbe [register/enchant] <...>");
            return false;
        }
        switch (args[0]) {
            case "register":
                switch (args.length) {
                    case 1:
                        sender.sendMessage("" + registerSyntax);
                        break;
                    case 3:
                        rarity = args[1].toLowerCase();
                        name = args[2].toLowerCase();
                        if (!api.check(name)) {
                            if (rarities.contains(rarity)) {
                                api.addEnchantment(name, name, rarity);
                                sender.sendMessage("§7You registered the following enchant: " + api.getColor(api.getRarity(name)) + api.getDisplayName(name));
                                break;
                            } else
                                sender.sendMessage("§cPlease select a valid rarity: [common, rare, epic, legendary]");
                            break;
                        }
                        sender.sendMessage("§cThis enchantment already exists. You can edit its basic attributes in the database.");
                        break;
                }
                break;
            case "enchant":
                if (sender instanceof Player) {
                    String str1, action;
                    int level;
                    Player player = (Player)sender;
                    ItemStack item = player.getInventory().getItemInMainHand();
                    switch (args.length) {
                        case 1:
                            player.sendMessage("" + enchantSyntax);
                            break;
                        case 3:
                            str1 = args[2].toLowerCase();
                            switch (args[1]) {
                                case "add":
                                    if (api.check(str1)) {
                                        nbt.addEnchantmentLevel(item, str1, 1);
                                        lore.updateLore(item);
                                        player.sendMessage("§7You changed your item's level of " + api.getColor(api.getRarity(str1)) + api.getDisplayName(str1) + " " + nbt.getEnchantmentLevel(item, str1) + ".");
                                        break;
                                    }
                                    player.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                                case "set":
                                    if (nbt.getEnchants(item).contains(str1)) {
                                        player.sendMessage("§cPlease specify a level, as your item already has this enchantment.");
                                        break;
                                    }
                                    nbt.setEnchantmentLevel(item, str1, 1);
                                    lore.updateLore(item);
                                    break;
                                case "remove":
                                    if (nbt.getEnchants(item).contains(str1)) {
                                        nbt.removeEnchantment(item, str1);
                                        lore.updateLore(item);
                                        player.sendMessage("removed " + api.getColor(api.getRarity(str1)) + api.getDisplayName(str1) + " your item.");
                                        break;
                                    }
                                    player.sendMessage("§cYour held item doesn't have this enchant applied.");
                                    break;
                            }
                            break;
                        case 4:
                            action = args[1].toLowerCase();
                            try {
                                level = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage("§cSpecify a valid number as level. Not '" + args[3] + "'");
                                break;
                            }
                            str1 = args[2].toLowerCase();
                            switch (action) {
                                case "add":
                                    if (api.check(str1)) {
                                        if (!nbt.getEnchants(item).contains(str1) && level < 0) {
                                            player.sendMessage("§cYour held item doesn't have this enchant applied.");
                                            break;
                                        }
                                        nbt.addEnchantmentLevel(item, str1, level);
                                        lore.updateLore(item);
                                        if (nbt.getEnchants(item).contains(str1)) {
                                            player.sendMessage("§7You changed your item's level of " + api.getColor(api.getRarity(str1)) + api.getDisplayName(str1) + " " + nbt.getEnchantmentLevel(item, str1) + ".");
                                            break;
                                        }
                                        player.sendMessage("§7You removed " + api.getColor(api.getRarity(str1)) + api.getDisplayName(str1) + " your item.");
                                        break;
                                    }
                                    sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                                case "set":
                                    if (api.check(str1)) {
                                        nbt.setEnchantmentLevel(item, str1, level);
                                        lore.updateLore(item);
                                        if (nbt.getEnchants(item).contains(str1)) {
                                            player.sendMessage("§7You changed your item's level of " + api.getColor(api.getRarity(str1)) + api.getDisplayName(str1) + " " + nbt.getEnchantmentLevel(item, str1) + ".");
                                            break;
                                        }
                                        player.sendMessage("§7You removed " + api.getColor(api.getRarity(str1)) + api.getDisplayName(str1) + " your item.");
                                        break;
                                    }
                                    sender.sendMessage("§cThis enchantment doesn't exist.");
                                    break;
                            }
                            break;
                    }
                    break;
                }
                sender.sendMessage("§cYou have to be a player to execute this command.");
                break;
        }
        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completer = new ArrayList<>();
        if (args.length > 0) {
            Player player;
            ItemStack item;
            List<String> enchants;
            int i;
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
                    player = (Player)sender;
                    item = player.getInventory().getItemInMainHand();
                    switch (args.length) {
                        case 2:
                            completer.add("add");
                            completer.add("set");
                            completer.add("remove");
                            return completer;
                        case 3:
                            enchants = new ArrayList<>();
                            try {
                                enchants = api.getEnchantments(nbt.getCategory(item));
                            } catch (Exception exception) {}
                            if (!enchants.isEmpty()) {
                                int j;
                                switch (args[1]) {
                                    case "add":
                                    case "set":
                                        for (j = 0; j < enchants.size(); j++) {
                                            if (api.check(enchants.get(j), "active"))
                                                completer.add(enchants.get(j));
                                        }
                                        return completer;
                                    case "remove":
                                        if (nbt.getEnchants(item) != null) {
                                            completer.addAll(nbt.getEnchants(item));
                                            return completer;
                                        }
                                        completer.add("");
                                        return completer;
                                }
                            }
                        case 4:
                            switch (args[1]) {
                                case "add":
                                    if (!item.getType().equals(Material.AIR)) {
                                        if (nbt.getEnchantmentLevel(item, args[2]) < api.getLevelCap(args[2])) {
                                            for (int j = 1; j <= api.getLevelCap(args[2]) - nbt.getEnchantmentLevel(item, args[2]); j++)
                                                completer.add("" + j);
                                        } else {
                                            for (int j = 1; j <= api.getLevelCap(args[2]); j++)
                                                completer.add("" + j);
                                        }
                                        return completer;
                                    }
                                case "set":
                                    for (i = 1; i <= api.getLevelCap(args[2]); i++)
                                        completer.add("" + i);
                                    return completer;
                            }
                            break;
                    }
                    break;
            }
        }
        return null;
    }
}
