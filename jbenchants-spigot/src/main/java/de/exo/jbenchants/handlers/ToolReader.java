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
    JBEnchantData.NBT nbt = Main.instance.nbt;
    JBEnchantData.Lore lore = Main.instance.lore;
    JBEnchantData.Handler handler = Main.instance.handler;
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getActiveItem();
        List<String> enchants = nbt.getEnchants(tool);
        Block block = event.getBlock();
        if (enchants != null) {
            lore.updateLore(tool);
            for (int i = 0; i < enchants.size(); i++) {
                String enchant = enchants.get(i);
                double chance = api.getProcChance(enchant)*nbt.getEnchantmentLevel(tool, enchant);
                if (Math.random() <= chance) {
                    handler.procc(player, tool, enchant, block);
                }
            }
        }
    }
}
