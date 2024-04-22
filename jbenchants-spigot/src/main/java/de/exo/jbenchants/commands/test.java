package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantData;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class test implements CommandExecutor {
    API api = Main.instance.api;
    JBEnchantData.NBT nbt = Main.instance.nbt;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            NBTItem nbti = new NBTItem(item);
            if (nbti.hasTag("jbenchants")) {
                player.sendMessage(nbti.getInteger("jbenchants").toString());
                for (String enchants : api.getEnchantments()) {
                    if (nbti.hasTag(enchants)) player.sendMessage(api.getDisplayName(enchants)+" - "+nbt.getEnchantmentLevel(item, enchants));
                }
            }
        }

        return false;
    }
}
