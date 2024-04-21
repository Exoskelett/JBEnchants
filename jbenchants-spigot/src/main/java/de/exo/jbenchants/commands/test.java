package de.exo.jbenchants.commands;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.EnchantsNBT;
import de.exo.jbenchants.handlers.JBEnchant;
import de.exo.jbenchants.handlers.JBEnchantData;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class test implements CommandExecutor {
    API api = Main.instance.api;
    EnchantsNBT nbt = Main.instance.nbtHandler;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            NBTItem nbti = new NBTItem(item);
            if (nbti.hasTag("jbenchants")) {
                player.sendMessage(nbti.getInteger("jbenchants").toString());
                for (String enchants : api.getEnchantments()) {
                    if (nbti.hasTag(enchants)) player.sendMessage(new JBEnchant(enchants, nbt.getEnchantmentLevel(item, enchants)).toString());
                }
            }
        }

        return false;
    }
}
