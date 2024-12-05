package de.exo.jbenchants.handlers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JBEnchantRegions implements JBEnchantData.Regions {

    private static JBEnchantRegions INSTANCE;

    private JBEnchantRegions() {
    }

    public static JBEnchantRegions getInstance() {
        if (INSTANCE == null)
            INSTANCE = new JBEnchantRegions();
        return INSTANCE;
    }

    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    public boolean checkBlock(Block block, String region) {
        ApplicableRegionSet regionSet = container.createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
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
        ApplicableRegionSet regionSet = container.createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
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
        for (double x = bx - radius; x <= bx + radius; x++) {
            for (double y = by - radius; y <= by + radius; y++) {
                for (double z = bz - radius; z <= bz + radius; z++) {
                    Block checkedBlock = block.getWorld().getBlockAt(new Location(block.getWorld(), x, y, z));
                    if (block.getLocation().distance(checkedBlock.getLocation()) <= radius
                            && !checkedBlock.getType().equals(Material.AIR) && checkBlock(checkedBlock, region)) {
                        if (blockCheck) {
                            if (checkedBlock.getType() == block.getType()) {
                                blocks.add(new Location(block.getWorld(), x, y, z).getBlock());
                            }
                        } else
                            blocks.add(new Location(block.getWorld(), x, y, z).getBlock());
                    }
                }
            }
        }
        return blocks;
    }
}
