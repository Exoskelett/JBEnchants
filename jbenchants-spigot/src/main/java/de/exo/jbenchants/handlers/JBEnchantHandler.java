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
            if (api.check(list.get(i), "active")) {
                if (api.check(list.get(i), "proccable")) {
                    activate(player, tool, list.get(i), block);
                    notify.add(list.get(i));
                }
            }
        }
        notify = lore.sortEnchants(notify);
        notify(player, notify);
    }
    public void notify(Player player, List<String> list) {
        StringBuilder s = new StringBuilder();
        for (int k = 0; k < list.size(); k++) {
            if (k < list.size()-1) {
                s.append(list.get(k)).append("§7, ");
            } else {
                s.append(list.get(k));
            }
        }
        player.sendActionBar(s+" §7procced.");
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
