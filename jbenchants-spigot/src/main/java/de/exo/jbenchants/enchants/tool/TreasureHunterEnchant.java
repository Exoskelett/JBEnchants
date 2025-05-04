package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class TreasureHunterEnchant implements Listener {
    Player player;

    Block block;

    EnchantsNBT nbt = EnchantsNBT.getInstance();

    EnchantsRegions regions = EnchantsRegions.getInstance();

    public TreasureHunterEnchant() {}

    public TreasureHunterEnchant(Player player, Block block) {
        this.player = player;
        this.block = block;
        procc();
    }

    private void procc() {
        block.setType(Material.TRAPPED_CHEST, false);
        Chest chest = (Chest) block.getState();
        Inventory chestInv = chest.getInventory();
        nbt.checkTreasureHunter(block, player);
        String[] loot = { "DIAMOND", "EMERALD", "GOLD_INGOT", "IRON_INGOT", "COAL" };
        for (int i = 0; i < new Random().nextInt(10) + 1; i++) {
            chestInv.setItem(new Random().nextInt(27),
                    new ItemStack(Material.valueOf(loot[new Random().nextInt(4)])));
        }
        Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (block.getType().equals(Material.TRAPPED_CHEST)) {
                    block.setType(Material.AIR, false);
                    nbt.lockBlock(block, false);
                }
            }
        }, 200);
    }

    @EventHandler
    public void onTreasureChestBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (regions.checkBlock(block, "mine") && nbt.checkLockedBlock(block)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                @Override
                public void run() {
                    if (nbt.checkLockedBlock(block)) {
                        if (!block.getType().equals(Material.TRAPPED_CHEST))
                            event.getPlayer().sendMessage("§aThe block you tried to break can be broken now.");
                        nbt.lockBlock(block, false);
                    }
                }
            }, 200);
        }
    }
}
