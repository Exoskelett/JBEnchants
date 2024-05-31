package de.exo.jbenchants.commands;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.events.GUIHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Crystals implements CommandExecutor {
    GUIHandler guiHandler = Main.instance.guiHandler;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(guiHandler.getCrystalsInventory((Player) sender));
        }
        return false;
    }
}
