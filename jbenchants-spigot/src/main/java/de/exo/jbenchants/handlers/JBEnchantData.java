package de.exo.jbenchants.handlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface JBEnchantData {
    public interface Handler {
        public void procc(Player player, ItemStack tool, List<String> list, Block block);
        public void activate(Player player, ItemStack tool, String name, Block block);

        boolean updateCrystal(ItemStack item);

        boolean updateDust(ItemStack item);

        boolean updateCleanser(ItemStack item);

        boolean updateScroll(ItemStack item);
    }
    public interface Items {

        ItemStack getEnchantInformation(String name);

        ItemStack getCrystal(String rarity);

        ItemStack getCrystal(String rarity, int chance);

        ItemStack getMysteryCrystal(String rarity, int low, int high);

        ItemStack getDust(String rarity);

        ItemStack getDust(String rarity, int chance);

        ItemStack getCleanser();

        ItemStack getCleanser(int chance);

        ItemStack getScroll(String rarity);

        ItemStack getScroll(String rarity, int chance);
    }
    public interface Lore {
        public List<Integer> getEnchantmentLoreSlots(ItemStack item);
        public void deleteUnusedEnchants(ItemStack item);
        void deleteAllEnchants(ItemStack item);
        public void updateLore(ItemStack item);
        List<String> sortEnchants(List<String> list);
        List<String> sortEnchantsWithLevel(List<String> list, ItemStack item);

        void setEnchantmentInfoMeta(ItemStack item, String name);

        List<String> getCrystalLore(String rarity, int chance);

        void updateCrystalLore(ItemStack item);

        void setCrystalMeta(ItemStack item, String rarity, int chance);

        List<String> getMysteryCrystalLore(String rarity, int low, int high);

        void setMysteryCrystalMeta(ItemStack item, String rarity, int low, int high);

        String getChanceGradient(int chance);

        List<String> getDustLore(String rarity, int chance);

        void setDustMeta(ItemStack item, String rarity, int chance);

        List<String> getCleanserLore(int chance);

        void setCleanserMeta(ItemStack item, int chance);

        List<String> getScrollLore(String rarity, int chance);

        void setScrollMeta(ItemStack item, String rarity, int chance);

        int getScrollDurability(String rarity);
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
