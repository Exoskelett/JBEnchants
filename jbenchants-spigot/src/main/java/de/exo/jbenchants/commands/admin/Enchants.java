package de.exo.jbenchants.commands.admin;

import de.exo.jbenchants.events.GUIHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Enchants implements CommandExecutor {

    GUIHandler guis = GUIHandler.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.openInventory(guis.getEnchantsInventory());
        }
        return false;
    }
}
