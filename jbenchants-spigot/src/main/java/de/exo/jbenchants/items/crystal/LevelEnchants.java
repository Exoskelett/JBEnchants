package de.exo.jbenchants.items.crystal;

import org.bukkit.entity.Player;

public class LevelEnchants {
    public final int level;
    public final int maxEnchants;
    public final int nextLevel;
    public final int nextMaxEnchants;

    public LevelEnchants(int level, int maxEnchants, int nextLevel, int nextMaxEnchants) {
        this.level = level;
        this.maxEnchants = maxEnchants;
        this.nextLevel = nextLevel;
        this.nextMaxEnchants = nextMaxEnchants;
    }

    public static LevelEnchants getPlayerMaxEnchants(Player player) {
        // int level = Integer.parseInt(PlaceholderAPI.setPlaceholders(player,
        // "%level_level%"));
        LevelEnchants levelEnchants;
        int level = 40;
        if (level >= 40) {
            levelEnchants = new LevelEnchants(level, 8, -1, -1);
        } else if (level >= 35) {
            levelEnchants = new LevelEnchants(level, 7, 40, 8);
        } else if (level >= 25) {
            levelEnchants = new LevelEnchants(level, 6, 35, 7);
        } else if (level >= 15) {
            levelEnchants = new LevelEnchants(level, 5, 25, 6);
        } else if (level >= 10) {
            levelEnchants = new LevelEnchants(level, 4, 15, 5);
        } else if (level >= 5) {
            levelEnchants = new LevelEnchants(level, 3, 10, 4);
        } else {
            levelEnchants = new LevelEnchants(level, 2, 5, 3);
        }
        return levelEnchants;
    }
}
