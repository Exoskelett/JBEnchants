package de.exo.jbenchants.enchants.tool;

import de.exo.jbenchants.enchants.EnchantsNBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairEnchant {
    Player player;
    ItemStack tool;
    String name;
    EnchantsNBT nbt = EnchantsNBT.getInstance();

    public RepairEnchant(Player player, ItemStack tool, String name) {
        this.player = player;
        this.tool = tool;
        this.name = name;
        procc();
    }

    private void procc() {
        nbt.repairTool(tool, nbt.getEnchantmentLevel(tool, name) * 3);
    }
}
