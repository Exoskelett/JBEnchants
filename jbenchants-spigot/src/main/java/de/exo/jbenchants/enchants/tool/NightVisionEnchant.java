package de.exo.jbenchants.enchants.tool;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionEnchant {
    Player player;

    public NightVisionEnchant(Player player) {
        this.player = player;
        procc();
    }

    private void procc() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, 0));
    }
}
