package de.exo.jbenchants.enchants.tool;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBoostEnchant {
    Player player;

    public SpeedBoostEnchant(Player player) {
        this.player = player;
        procc();
    }

    private void procc() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 2));
    }
}
