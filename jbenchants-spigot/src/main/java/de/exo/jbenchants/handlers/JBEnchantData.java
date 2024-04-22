package de.exo.jbenchants.handlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface JBEnchantData {
    public interface Handler {
        public void procc(Player player, ItemStack tool, String name, Block block);
        public void activate(Player player, ItemStack tool, String name, Block block);
    }
    public interface Lore {
        public List<Integer> getEnchantmentLoreSlots(ItemStack item);

        public void updateLore(ItemStack item);
    }
    public interface NBT {
        public void addEnchantmentLevel(ItemStack item, String name, int level);
        public void setEnchantmentLevel(ItemStack item, String name, int level);
        public void removeEnchantment(ItemStack item, String name);
        public void removeEnchantmentLevel(ItemStack item, String name, int level);
        public int getEnchantmentLevel(ItemStack item, String name);
        public List<String> getEnchants(ItemStack item);
    }
}
