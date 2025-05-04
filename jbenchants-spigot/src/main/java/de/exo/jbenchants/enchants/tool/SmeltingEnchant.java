package de.exo.jbenchants.enchants.tool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SmeltingEnchant {
    Player player;
    Block block;

    public SmeltingEnchant(Player player, Block block) {
        this.player = player;
        this.block = block;
        procc();
    }

    private void procc() {
        player.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR, false);
        for (ItemStack drop : block.getDrops()) {
            if (drop.getType().toString().contains("LOG")) {
                player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.COAL));
                continue;
            }
            if (drop.getType().equals(Material.GOLD_ORE)) {
                player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT));
                continue;
            }
            if (drop.getType().equals(Material.IRON_ORE)) {
                player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                continue;
            }
            if (drop.getType().equals(Material.COBBLESTONE)) {
                player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.STONE));
                continue;
            }
            player.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
}
