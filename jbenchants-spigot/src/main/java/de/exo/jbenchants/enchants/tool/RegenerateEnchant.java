package de.exo.jbenchants.enchants.tool;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RegenerateEnchant {
    Player player;
    ItemStack tool;
    Block block;

    public RegenerateEnchant(Player player, ItemStack tool, Block block) {
        this.player = player;
        this.tool = tool;
        this.block = block;
        procc();
    }

    private void procc() {
        for (ItemStack drop : block.getDrops(tool))
            player.getWorld().dropItemNaturally(block.getLocation(), drop);
    }
}
