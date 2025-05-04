package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsNBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuperBreakerEnchant {
    Player player;
    ItemStack tool;
    String name;
    API api = Main.getAPI();
    EnchantsNBT nbt = EnchantsNBT.getInstance();

    public SuperBreakerEnchant(Player player, ItemStack tool, String name) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        procc();
    }

    private void procc() {
        int duration = nbt.getEnchantmentLevel(tool, name) * 20;
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, 99));
        player.sendMessage(api
                .getColor(api.getRarity(name)) + api.getColor(api.getRarity(name)) + "has been activated!");
        Bukkit.getScheduler().runTaskLater((Plugin)Main.instance, new Runnable() {
            public void run() {
                player.sendMessage(api.getColor(api.getRarity(name)) + api.getColor(api.getRarity(name)) + "has been deactivated.");
            }
        },  duration);
    }
}
