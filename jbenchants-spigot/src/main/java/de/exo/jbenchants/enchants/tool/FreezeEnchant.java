package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsRegions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FreezeEnchant {
    Player player;
    ItemStack tool;
    Block block;
    EnchantsRegions regions = EnchantsRegions.getInstance();

    public FreezeEnchant(Player player, ItemStack tool, Block block) {
        this.player = player;
        this.tool = tool;
        this.block = block;
        procc();
    }

    private void procc() {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    Block frozenBlock = player.getWorld().getBlockAt(block.getLocation().getBlockX() + j, block
                            .getLocation().getBlockY() + i, block.getLocation().getBlockZ() + k);
                    if (!frozenBlock.getType().equals(Material.AIR) &&
                            !frozenBlock.getType().equals(Material.PACKED_ICE) && regions
                            .checkBlock(frozenBlock, "mine")) {
                        for (ItemStack drop : frozenBlock.getDrops(tool))
                            player.getWorld().dropItemNaturally(frozenBlock.getLocation(), drop);
                        frozenBlock.setType(Material.PACKED_ICE, false);
                    }
                }
            }
        }
    }
}
