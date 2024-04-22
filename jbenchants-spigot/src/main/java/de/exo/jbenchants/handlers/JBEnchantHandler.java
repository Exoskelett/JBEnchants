package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JBEnchantHandler implements JBEnchantData.Handler {
    API api = Main.instance.api;
    public void procc(Player player, ItemStack tool, String name, Block block) {
        if (api.check(name)) {
            if (api.checkNotify(name)) {
                activate(player, tool, name, block);
                player.sendActionBar(api.getDisplayName(name)+" §aprocced.");
            }
        } else {
            player.sendActionBar(api.getDisplayName(name)+" §cis currently disabled.");
        }

    }
    public void activate(Player player, ItemStack tool, String name, Block block) {
        switch (name) {
                // common enchants
            case "alchemy":
                break;
                // rare enchants
            case "drill":
                break;
                // epic enchants
            case "haste":
                break;
                // legendary enchants
            case "efficiency":
                break;
        }
    }
}
