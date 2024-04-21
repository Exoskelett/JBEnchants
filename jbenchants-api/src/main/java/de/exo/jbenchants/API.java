package de.exo.jbenchants;

import java.util.List;
import java.util.UUID;

public interface API {

    boolean isConnected();

    void createDefaultTables();

    void addEnchantment(String name, String display_name, String rarity, int level_cap, double proc_chance);

    List<String> getEnchantments();

    String getDisplayName(String name);

    int getLevelCap(String name);


    boolean exists(String name);

    boolean exists(UUID player);

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
