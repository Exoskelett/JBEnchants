package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JBEnchantHandler implements JBEnchantData.Handler {

    API api = Main.instance.api;
    JBEnchantLore lore = Main.instance.lore;

    public void procc(Player player, ItemStack tool, List<String> list, Block block) {
        List<String> notify = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (api.checkActive(list.get(i))) {
                if (api.checkNotify(list.get(i))) {
                    activate(player, tool, list.get(i), block);
                    notify.add(list.get(i));
                }
            } else {
                if (api.checkNotify(list.get(i))) {
                    player.sendActionBar(api.getEnchantmentColor(api.getRarity(list.get(i)))+api.getDisplayName(list.get(i)) + " §cis currently disabled.");
                }
            }
        }
        notify = lore.sortEnchants(notify);
        StringBuilder s = new StringBuilder();
        for (int k = 0; k < notify.size(); k++) {
            if (k < notify.size()-1) {
                s.append(notify.get(k)).append("§a, ");
            } else {
                s.append(notify.get(k));
            }
        }
        player.sendActionBar(s+" §aprocced.");
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
