package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsLore;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class jbdebug implements CommandExecutor {
    API api = Main.getAPI();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsLore lore = EnchantsLore.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            NBTEntity nbte = new NBTEntity(player);
            switch (args.length) {
                case 0:
                    NBTItem nbti = new NBTItem(item);
                    player.sendMessage("Debug:\n"
                            +"JBEnchants: ("+nbti.getInteger("jbenchants")+") "+nbt.getEnchants(item).toString()
                            +"\nLore: "+item.getLore().toString());
                    break;
                case 1:
                    switch (args[0]) {
                        case "nbt":
                            nbti = new NBTItem(item);
                            for (String keys : nbti.getKeys()) {
                                if (!nbti.getString(keys).isEmpty())
                                    player.sendMessage(keys + " - " + nbti.getString(keys));
                                if (nbti.getInteger(keys) != 0)
                                    player.sendMessage(keys + " - " + nbti.getInteger(keys));
                            }
                            break;
                        case "nbte":
                            for (String keys : nbte.getPersistentDataContainer().getKeys()) {
                                if (!nbte.getPersistentDataContainer().getString(keys).isEmpty())
                                    player.sendMessage(keys + " - " + nbte.getPersistentDataContainer().getString(keys));
                                if (nbte.getPersistentDataContainer().getInteger(keys) != 0)
                                    player.sendMessage(keys + " - " + nbte.getPersistentDataContainer().getInteger(keys));
                            }
                            break;
                        case "nbtb":
                            NBTBlock nbtb = new NBTBlock(player.getTargetBlock((Set<Material>) null, 10));
                            for (String keys : nbtb.getData().getKeys()) {
                                if (!nbtb.getData().getString(keys).isEmpty())
                                    player.sendMessage(keys + " - " + nbtb.getData().getString(keys));
                                if (nbtb.getData().getInteger(keys) != 0)
                                    player.sendMessage(keys + " - " + nbtb.getData().getInteger(keys));
                            }
                            break;
                        case "update":
                            player.sendMessage("Updating Lore:");
                            lore.updateLore(item);
                            break;
                        case "updateItem":
                            if (item.getItemMeta().hasLore()) {
                                player.sendMessage("Updating Lore:");
                                lore.updateLore(item);
                                break;
                            }
                            break;
                        case "delete":
                            player.sendMessage("Deleting Lore:");
                            lore.deleteUnusedEnchants(item);
                            break;
                        case "deleteAll":
                            player.sendMessage("Deleting every Lore:");
                            lore.deleteAllEnchants(item);
                            break;
                        case "slots":
                            player.sendMessage("Enchantment-Slots: "+lore.getEnchantmentLoreSlots(item).toString());
                            break;
                        case "crystal":
                            nbti = new NBTItem(item);
                            player.sendMessage("Rarity: "+nbti.getString("crystal"));
                            player.sendMessage("Chance: "+nbti.getString("chance"));
                            break;
                        case "enchants":
                            if (!item.getEnchantments().isEmpty()) {
                                player.sendMessage(item.getEnchantments().toString());
                            } else
                                player.sendMessage("§cKeine Enchants :/");
                            break;
                        case "lock":
                            if (nbt.checkLockedBlock(player.getTargetBlock((Set<Material>) null, 10))) {
                                nbt.lockBlock(player.getTargetBlock((Set<Material>) null, 10), false);
                                player.sendMessage("§aBlock has been unlocked.");
                            } else {
                                nbt.lockBlock(player.getTargetBlock((Set<Material>) null, 10), true);
                                player.sendMessage("§cBlock has been locked.");
                            }
                            break;
                    }
                case 2:
                    switch (args[0]) {
                        case "setCrystal":
                            nbti = new NBTItem(item);
                            nbti.setString("crystal", args[1]);
                            nbti.applyNBT(item);
                            break;
                        case "setChance":
                            nbti = new NBTItem(item);
                            nbti.setString("chance", args[1]);
                            nbti.applyNBT(item);
                            break;
                        case "setUsedCrystal":
                            String[] crystalData = args[1].split("-");
                            nbte.getPersistentDataContainer().setInteger(crystalData[0], Integer.valueOf(crystalData[1]));
                            break;
                        case "remove":
                            nbte.getPersistentDataContainer().removeKey(args[1]);
                            break;
                        case "hasTag":
                            nbti = new NBTItem(item);
                            player.sendMessage(""+nbti.hasTag(args[1]));
                    }
            }
        }

        return false;
    }
}
