package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.Main;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class StopThatEnchant implements Listener {
    Player player;
    Block block;

    public StopThatEnchant() {}

    public StopThatEnchant(Player player, Block block) {
        this.player = player;
        this.block = block;
        procc();
    }

    private void procc() {
        Villager v = (Villager) player.getWorld().spawn(block.getLocation(), Villager.class);
        v.setCustomName("§d§lMagoo");
        player.sendMessage("§c§lHey, Stop That!");
        block.getWorld().spawnParticle(Particle.REDSTONE,
                block.getLocation().getX() + 0.3, block.getLocation().getY() + 0.7,
                block.getLocation().getZ() + 0.3,
                100, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
        Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (!v.isDead()) {
                    player.playSound(v.getLocation(), Sound.ENTITY_VILLAGER_DEATH, 0.1F, 1);
                    v.teleport(v.getLocation().add(0, -300, 0));
                }
            }
        }, 200);
    }

    @EventHandler
    public void onVillagerKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager && event.getEntity().getKiller() != null) {
            double chance = Math.random();
            if (chance <= 0.05D) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.DIAMOND));
            } else if (chance <= 0.1D) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.EMERALD));
            } else if (chance <= 0.45D) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.IRON_INGOT));
            } else if (chance <= 0.8D) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.GOLD_INGOT));
            } else {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.COAL));
            }
        }
    }
}
