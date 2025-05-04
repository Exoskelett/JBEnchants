package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class DrillEnchant {
    Player player;
    ItemStack tool;
    String name;
    Block block;
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();
    private int taskId, index;

    public DrillEnchant(Player player, ItemStack tool, String name, Block block) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        this.block = block;
        procc();
    }

    private void procc() {
        playDrillSounds(player.getLocation(), player);
        for (int i = 0; i > -nbt.getEnchantmentLevel(tool, name); i--) {
            Block b1 = player.getWorld().getBlockAt(block.getLocation()).getRelative(1, i, 0);
            Block b2 = player.getWorld().getBlockAt(block.getLocation()).getRelative(-1, i, 0);
            Block b3 = player.getWorld().getBlockAt(block.getLocation()).getRelative(0, i, 1);
            Block b4 = player.getWorld().getBlockAt(block.getLocation()).getRelative(0, i, -1);
            Block b5 = player.getWorld().getBlockAt(block.getLocation()).getRelative(0, i - 1, 0);
            if (regions.checkBlock(b1, "mine")) {
                for (ItemStack drop : b1.getDrops(tool)) {
                    if (!b1.getType().equals(Material.PACKED_ICE))
                        player.getWorld().dropItemNaturally(b1.getLocation(), drop);
                }
                b1.setType(Material.AIR, false);
                block.getWorld().spawnParticle(Particle.REDSTONE,
                        b1.getLocation().getX() + 0.5, b1.getLocation().getY() + 0.5,
                        b1.getLocation().getZ() + 0.5,
                        20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
            }
            if (regions.checkBlock(b2, "mine")) {
                for (ItemStack drop : b2.getDrops(tool)) {
                    if (!b2.getType().equals(Material.PACKED_ICE))
                        player.getWorld().dropItemNaturally(b2.getLocation(), drop);
                }
                b2.setType(Material.AIR, false);
                block.getWorld().spawnParticle(Particle.REDSTONE,
                        b2.getLocation().getX() + 0.5, b2.getLocation().getY() + 0.5,
                        b2.getLocation().getZ() + 0.5,
                        20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
            }
            if (regions.checkBlock(b3, "mine")) {
                for (ItemStack drop : b3.getDrops(tool)) {
                    if (!b3.getType().equals(Material.PACKED_ICE))
                        player.getWorld().dropItemNaturally(b3.getLocation(), drop);
                }
                b3.setType(Material.AIR, false);
                block.getWorld().spawnParticle(Particle.REDSTONE,
                        b3.getLocation().getX() + 0.5, b3.getLocation().getY() + 0.5,
                        b3.getLocation().getZ() + 0.5,
                        20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
            }
            if (regions.checkBlock(b4, "mine")) {
                for (ItemStack drop : b4.getDrops(tool)) {
                    if (!b4.getType().equals(Material.PACKED_ICE))
                        player.getWorld().dropItemNaturally(b4.getLocation(), drop);
                }
                b4.setType(Material.AIR, false);
                block.getWorld().spawnParticle(Particle.REDSTONE,
                        b4.getLocation().getX() + 0.5, b4.getLocation().getY() + 0.5,
                        b4.getLocation().getZ() + 0.5,
                        20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
            }
            if (regions.checkBlock(b5, "mine")) {
                for (ItemStack drop : b5.getDrops(tool)) {
                    if (!b5.getType().equals(Material.PACKED_ICE))
                        player.getWorld().dropItemNaturally(b5.getLocation(), drop);
                }
                b5.setType(Material.AIR, false);
                block.getWorld().spawnParticle(Particle.REDSTONE,
                        b5.getLocation().getX() + 0.5, b5.getLocation().getY() + 0.5,
                        b5.getLocation().getZ() + 0.5,
                        20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
            }
        }
    }

    private void playDrillSounds(final Location location, final Player player) {
        index = 3;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            public void run() {
                if (index == 0) {
                    stopCountdown();
                } else {
                    player.playSound(location, Sound.ITEM_SHIELD_BREAK, 1.0F, 0.1F);
                    index--;
                }
            }
        }, 0L, 4L);
    }

    private void stopCountdown() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
