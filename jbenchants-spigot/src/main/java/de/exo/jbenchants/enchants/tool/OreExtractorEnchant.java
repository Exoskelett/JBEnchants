package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OreExtractorEnchant {
    Player player;
    ItemStack tool;
    String name;
    Block block;
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();

    public OreExtractorEnchant(Player player, ItemStack tool, String name, Block block) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        this.block = block;
        procc();
    }

    private void procc() {
        double radius = (nbt.getEnchantmentLevel(tool, name) + 1) / 2.0D;
        List<Block> extractedBlocks = regions.getBlocksInRadius(block, "mine", radius, true);
        for (int i = 0; i < extractedBlocks.size() - 1; i++) {
            for (ItemStack drop : ((Block)extractedBlocks.get(i)).getDrops(tool))
                player.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
}
