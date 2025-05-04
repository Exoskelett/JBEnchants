package de.exo.jbenchants.enchants.tool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MidasTouchEnchant {
    Player player;
    Block block;

    public MidasTouchEnchant(Player player, Block block) {
        this.player = player;
        this.block = block;
        procc();
    }

    private void procc() {
        double chance = Math.random();
        if (chance <= 0.1D) {
            player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_BLOCK));
        } else {
            player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT));
        }
    }
}
