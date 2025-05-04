package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsNBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HasteEnchant {
    Player player;
    ItemStack tool;
    String name;
    EnchantsNBT nbt = EnchantsNBT.getInstance();

    public HasteEnchant(Player player, ItemStack tool, String name) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        procc();
    }

    private void procc() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, nbt.getEnchantmentLevel(tool, name) * 40, 0));
    }
}
