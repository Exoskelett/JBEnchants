package de.exo.jbenchants.enchants.tool;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JumpBoostEnchant {
    Player player;

    public JumpBoostEnchant(Player player) {
        this.player = player;
        procc();
    }

    private void procc() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 2));
    }
}
