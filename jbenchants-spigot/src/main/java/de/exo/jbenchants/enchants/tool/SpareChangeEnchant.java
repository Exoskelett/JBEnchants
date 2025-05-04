package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsNBT;
import java.text.DecimalFormat;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpareChangeEnchant {
    Player player;
    ItemStack tool;
    String name;
    EnchantsNBT nbt = EnchantsNBT.getInstance();

    public SpareChangeEnchant(Player player, ItemStack tool, String name) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        procc();
    }

    private void procc() {
        double win = (new Random().nextDouble(74.85) + 0.15) * nbt.getEnchantmentLevel(tool, name);
        player.sendMessage("§7You found §a$" + DecimalFormat.getCurrencyInstance().format(win).split(" ")[0]
                + " §7in that block!");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
    }
}
