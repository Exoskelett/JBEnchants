package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MakeItRainEnchant {
    Player player;
    ItemStack tool;
    String name;
    Block block;
    EnchantsNBT nbt = EnchantsNBT.getInstance();

    public MakeItRainEnchant(Player player, ItemStack tool, String name, Block block) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        this.block = block;
        procc();
    }

    private void procc() {
        final ItemStack randomItem;
        final Location origin = block.getLocation().add(0.5D, 0.5D, 0.5D);
        if (block.getRelative(0, 1, 0).getType() == Material.AIR)
            origin.add(0.0D, 1.1D, 0.0D);
        final World world = block.getWorld();
        final List<Item> itemList = new ArrayList<>();
        Random random = new Random();
        try {
            randomItem = block.getDrops(tool).iterator().next();
        } catch (NoSuchElementException ignored) {
            return;
        }
        (new BukkitRunnable() {
            int level = nbt.getEnchantmentLevel(tool, name);

            public void run() {
                for (int i = 0; i < 3 * level; i++) {
                    double yaw = i * 90.0D / level;
                    Vector v = MakeItRainEnchant.getSphericalVector(1.0D, yaw, 1.0D);
                    Location dropLocation = origin.clone().add(v);
                    final Item item = world.dropItem(dropLocation, randomItem.clone());
                    item.setVelocity(new Vector(0, 0, 0));
                    item.setPickupDelay(2147483647);
                    item.setInvulnerable(true);
                    item.setSilent(true);
                    itemList.add(item);
                    (new BukkitRunnable() {
                        int ticks = 0;

                        public void run() {
                            if (ticks == 0) {
                                item.setGravity(false);
                                item.setVelocity(new Vector(0.0D, 0.6D, 0.0D));
                            }
                            if (ticks >= 3 || item.isDead()) {
                                item.setGravity(true);
                                cancel();
                                return;
                            }
                            ticks++;
                        }
                    }).runTaskTimer((Plugin)Main.instance, 0L, 1L);
                }
            }
        }).runTaskLater((Plugin)Main.instance, 1L);
        (new BukkitRunnable() {
            public void run() {
                if (!itemList.isEmpty()) {
                    Item removedItem = itemList.remove(0);
                    removedItem.remove();
                }
            }
        }).runTaskLater((Plugin)Main.instance, 38L);
        (new BukkitRunnable() {
            public void run() {
                for (Item item : itemList) {
                    if (!item.isDead()) {
                        world.createExplosion(item.getLocation(), 0.0F, false, false);
                        Vector pushVector = item.getLocation().toVector().subtract(origin.toVector()).normalize().multiply(0.6D);
                        item.setVelocity(pushVector);
                    }
                }
            }
        }).runTaskLater((Plugin)Main.instance, 39L);
        (new BukkitRunnable() {
            public void run() {
                for (Item item : itemList) {
                    if (!item.isDead()) {
                        item.setInvulnerable(false);
                        item.setPickupDelay(0);
                    }
                }
            }
        }).runTaskLater((Plugin)Main.instance, 40L);
    }

    private static Vector getSphericalVector(double radius, double yaw, double pitch) {
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);
        double x = radius * Math.cos(radPitch) * Math.cos(radYaw);
        double y = radius * Math.sin(radPitch);
        double z = radius * Math.cos(radPitch) * Math.sin(radYaw);
        return new Vector(x, y, z);
    }
}
