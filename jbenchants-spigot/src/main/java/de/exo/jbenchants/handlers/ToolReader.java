package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToolReader implements Listener {

    API api = Main.instance.api;
    JBEnchantNBT nbt = Main.instance.nbt;
    JBEnchantLore lore = Main.instance.lore;
    JBEnchantHandler handler = Main.instance.handler;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        List<String> enchants = nbt.getEnchants(item);
        Block block = event.getBlock();
        if (enchants != null) {
            List<String> procced = new ArrayList<>();
            for (int i = 0; i < enchants.size(); i++) {
                String enchant = enchants.get(i);
                double chance = api.getProcChance(enchant)*nbt.getEnchantmentLevel(item, enchant)/100;
                if (Math.random() <= chance) {
                    procced.add(enchant);
                }
            }
            if (!procced.isEmpty())
                handler.procc(player, item, procced, block);
        }
    }
}
