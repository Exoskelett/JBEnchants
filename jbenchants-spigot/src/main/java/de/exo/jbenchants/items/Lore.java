package de.exo.jbenchants.items;

public class Lore {
    public static String getChanceGradient(int chance) {
        if (chance >= 80) {
            return "§2" + chance;
        } else if (chance >= 60) {
            return "§a" + chance;
        } else if (chance >= 40) {
            return "§e" + chance;
        } else if (chance >= 20) {
            return "§c" + chance;
        } else
            return "§4" + chance;
    }
}
