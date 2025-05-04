package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExplosiveEnchant {
    Player player;
    ItemStack tool;
    String name;
    Block block;
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();

    public ExplosiveEnchant(Player player, ItemStack tool, String name, Block block) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        this.block = block;
        procc();
    }

    private void procc() {
        double radius = (nbt.getEnchantmentLevel(tool, name) + 1) / 2.0D;
        if (radius > 5.0D)
            radius = 5.0D;
        if (radius < 3.0D) {
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, block.getLocation(), 1);
            player.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1F, 1.0F);
        } else {
            player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, block.getLocation(), 1);
            player.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1F, 1.0F);
        }
        List<Block> explodedBlocks = regions.getBlocksInRadius(block, "mine", radius, false);
        for (Block checkedBlock : explodedBlocks) {
            for (ItemStack drop : checkedBlock.getDrops(tool)) {
                if (!checkedBlock.getType().equals(Material.PACKED_ICE))
                    player.getWorld().dropItemNaturally(checkedBlock.getLocation(), drop);
            }
            checkedBlock.setType(Material.AIR, false);
        }
    }
}
