package de.exo.jbenchants.enchants;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class EnchantsRegions {
    private static EnchantsRegions INSTANCE;

    public static EnchantsRegions getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EnchantsRegions();
        return INSTANCE;
    }

    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public boolean checkBlock(Block block, String region) {
        ApplicableRegionSet regionSet = this.container.createQuery().getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
        if (regionSet == null || regionSet.getRegions().isEmpty())
            return false;
        boolean blockIsInRegion = false;
        for (ProtectedRegion checkedRegion : regionSet) {
            if (checkedRegion.getId().contains(region))
                blockIsInRegion = true;
        }
        return blockIsInRegion;
    }

    public boolean checkPlayer(Player player, String region) {
        ApplicableRegionSet regionSet = this.container.createQuery().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        if (regionSet == null || regionSet.getRegions().isEmpty())
            return false;
        boolean playerIsInRegion = false;
        for (ProtectedRegion checkedRegion : regionSet) {
            if (checkedRegion.getId().contains(region))
                playerIsInRegion = true;
        }
        return playerIsInRegion;
    }

    public List<Block> getBlocksInRadius(Block block, String region, double radius, boolean blockCheck) {
        List<Block> blocks = new ArrayList<>();
        int bx = block.getLocation().getBlockX();
        int by = block.getLocation().getBlockY();
        int bz = block.getLocation().getBlockZ();
        double x;
        for (x = bx - radius; x <= bx + radius; x++) {
            double y;
            for (y = by - radius; y <= by + radius; y++) {
                double z;
                for (z = bz - radius; z <= bz + radius; z++) {
                    Block checkedBlock = block.getWorld().getBlockAt(new Location(block.getWorld(), x, y, z));
                    if (block.getLocation().distance(checkedBlock.getLocation()) <= radius &&
                            !checkedBlock.getType().equals(Material.AIR) && checkBlock(checkedBlock, region))
                        if (blockCheck) {
                            if (checkedBlock.getType() == block.getType())
                                blocks.add((new Location(block.getWorld(), x, y, z)).getBlock());
                        } else {
                            blocks.add((new Location(block.getWorld(), x, y, z)).getBlock());
                        }
                }
            }
        }
        return blocks;
    }
}
