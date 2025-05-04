package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VeinMinerEnchant {
    Player player;
    ItemStack tool;
    String name;
    Block block;
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();

    public VeinMinerEnchant(Player player, ItemStack tool, String name, Block block) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        this.block = block;
        procc();
    }

    private void procc() {
        int radius = nbt.getEnchantmentLevel(tool, name);
        if (radius > 10)
            radius = 10;
        player.playSound(block.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.1F, 1.0F);
        List<Block> veinMinedBlocks = regions.getBlocksInRadius(block, "mine", radius, true);
        for (Block checkedBlock : veinMinedBlocks) {
            for (ItemStack drop : checkedBlock.getDrops(tool)) {
                if (!checkedBlock.getType().equals(Material.PACKED_ICE))
                    player.getWorld().dropItemNaturally(checkedBlock.getLocation(), drop);
            }
            checkedBlock.setType(Material.AIR, false);
            block.getWorld().spawnParticle(Particle.REDSTONE, checkedBlock
                    .getLocation().getX() + 0.5D, checkedBlock.getLocation().getY() + 0.5D, checkedBlock
                    .getLocation().getZ() + 0.5D, 3, 0.0D, 0.0D, 0.0D, 0.0D, new Particle.DustOptions(
                    Color.fromRGB(255, 0, 255), 2.0F), true);
        }
    }
}
