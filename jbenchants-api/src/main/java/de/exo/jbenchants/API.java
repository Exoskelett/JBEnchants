package de.exo.jbenchants;

import java.util.List;

public interface API {

    boolean isConnected();

    void createDefaultTables();

    void addEnchantment(String name, String display_name, String rarity);

    List<String> getEnchantments();

    String getEnchantmentMaterial(String name);

    String getEnchantmentLore(String name);

    List<String> getEnchantments(String type);

    String getName(String displayName);

    String getDisplayName(String name);

    String getRarity(String name);

    int getLevelCap(String name);

    double getProcChance(String name);

    boolean check(String name);

    boolean check(String name, String dbCollum);

    String getColor(String name);

    String getPrefix(String name);

    // crystals:
    String getItemName(String type);

    String[] getItemLore(String type);




    void setLobbySpawn(String world, String location);

    void updateLobbySpawn(String world, String location);

    boolean checkLobbySpawn();

    String getLobbySpawn();

    void registerMap(String map, String world, String location);

    void updateMap(String map, String world, String location);

    boolean checkMap(String world);

    String getMap(String world);

    String getWorld(String map);

    void setSpawnLocation(String world, String location);

    int getSpawnLocationsCount(String world);

    List<String> getSpawnLocations(String world);

}
