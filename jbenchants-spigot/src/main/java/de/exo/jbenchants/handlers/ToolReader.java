package de.exo.jbenchants.handlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToolReader implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getActiveItem();
        if (item.getType().equals(Material.DIAMOND_PICKAXE)) {
            List<String> lore = item.getLore();
            List<String> enchantments = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                if (!lore.get(i).equals("")) break;

            }
        }
    }
}
