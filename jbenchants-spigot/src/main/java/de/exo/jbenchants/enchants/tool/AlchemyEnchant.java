package de.exo.jbenchants.enchants.tool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AlchemyEnchant {
    Player player;
    ItemStack tool;
    Block block;

    public AlchemyEnchant(Player player, ItemStack tool, Block block) {
        this.player = player;
        this.tool = tool;
        this.block = block;
        procc();
    }

    private void procc() {
        if (block.getType().toString().contains("ORE")) {
            switch (block.getType()) {
                case COAL_ORE:
                    block.setType(Material.IRON_ORE, false);
                    break;
                case IRON_ORE:
                    block.setType(Material.GOLD_ORE, false);
                    break;
                case GOLD_ORE:
                    block.setType(Material.DIAMOND_ORE, false);
                    break;
                case DIAMOND_ORE:
                    block.setType(Material.EMERALD_ORE, false);
                    break;
            }
        } else if (block.getType().toString().contains("BLOCK")) {
            switch (block.getType()) {
                case COAL_BLOCK:
                    block.setType(Material.IRON_BLOCK, false);
                    break;
                case IRON_BLOCK:
                    block.setType(Material.GOLD_BLOCK, false);
                    break;
                case GOLD_BLOCK:
                    block.setType(Material.DIAMOND_BLOCK, false);
                    break;
                case DIAMOND_BLOCK:
                    block.setType(Material.EMERALD_BLOCK, false);
                    break;
            }
        }
        for (ItemStack drop : block.getDrops(tool))
            player.getWorld().dropItemNaturally(block.getLocation(), drop);
    }
}
