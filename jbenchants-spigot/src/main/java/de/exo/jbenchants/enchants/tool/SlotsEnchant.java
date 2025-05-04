package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SlotsEnchant {
    Player player;
    Block block;
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    private int taskId;
    private int index;

    public SlotsEnchant(Player player, Block block) {
        this.player = player;
        this.block = block;
        procc();
    }

    private void procc() {
        String[] blocks = { "GOLD_BLOCK", "GOLD_BLOCK", "GOLD_BLOCK", "GOLD_BLOCK",
                "COAL_BLOCK", "COAL_BLOCK", "COAL_BLOCK", "COAL_BLOCK",
                "IRON_BLOCK", "IRON_BLOCK", "IRON_BLOCK", "IRON_BLOCK",
                "REDSTONE_BLOCK", "REDSTONE_BLOCK", "REDSTONE_BLOCK", "REDSTONE_BLOCK",
                "LAPIS_BLOCK", "LAPIS_BLOCK", "LAPIS_BLOCK", "LAPIS_BLOCK",
                "EMERALD_BLOCK", "DIAMOND_BLOCK" };
        index = 10;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (index == 0) {
                    nbt.lockBlock(block, false);
                    stopCountdown();
                } else {
                    nbt.lockBlock(block, true);
                    player.playSound(block.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
                    block.setType(Material.valueOf(blocks[new Random().nextInt(blocks.length)]), false);
                    index--;
                }
            }
        }, 0L, 4L);
    }

    private void stopCountdown() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
