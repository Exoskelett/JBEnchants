package de.exo.jbenchants.commands;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.handlers.JBEnchantNBT;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Rewards implements CommandExecutor {
    JBEnchantNBT nbt = Main.instance.nbt;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 3) {
            nbt.addPlayerCrystal(Bukkit.getPlayer(args[0]), args[1], Integer.parseInt(args[2]));
        }
        return false;
    }
}
