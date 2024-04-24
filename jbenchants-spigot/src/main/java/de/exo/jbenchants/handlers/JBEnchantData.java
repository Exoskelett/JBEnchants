package de.exo.jbenchants.handlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface JBEnchantData {
    public interface Handler {
        public void procc(Player player, ItemStack tool, List<String> list, Block block);
        public void activate(Player player, ItemStack tool, String name, Block block);
    }
    public interface Lore {
        public List<Integer> getEnchantmentLoreSlots(ItemStack item);
        public void deleteUnusedEnchants(ItemStack item);
        void deleteAllEnchants(ItemStack item);
        public void updateLore(ItemStack item);
        List<String> sortEnchants(List<String> list);
        List<String> sortEnchantsWithLevel(List<String> list, ItemStack item);
    }
    public interface NBT {
        public void addEnchantmentLevel(ItemStack item, String name, int level);
        public void setEnchantmentLevel(ItemStack item, String name, int level);
        public void removeEnchantment(ItemStack item, String name);
        public int getEnchantmentLevel(ItemStack item, String name);
        public List<String> getEnchants(ItemStack item);
        public boolean checkLevelCap(ItemStack item, String name, int level);
    }
}
