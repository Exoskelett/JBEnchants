package de.exo.jbenchants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQL implements API {

    private Connection connection;
    private final String host;
    private final String database;
    private final String username;
    private final String password;

    public MySQL(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || !connection.isValid(1)) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host
                    + "/" + database + "?useSSL=false", username, password);
        }
        return connection;
    }

    @Override
    public boolean isConnected() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    @Override
    public void createDefaultTables() {
        try {
            // enchantments
            PreparedStatement ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS enchantments "
                    + "(id int NOT NULL AUTO_INCREMENT, name varchar(20), display_name varchar(20), rarity varchar(10), level_cap int, proc_chance double, active boolean, notify boolean, PRIMARY KEY (id))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEnchantment(String name, String display_name, String rarity, int level_cap, double proc_chance) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT IGNORE INTO enchantments (name, display_name, rarity, level_cap, proc_chance, active, notify) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, display_name);
            ps.setString(3, rarity);
            ps.setInt(4, level_cap);
            ps.setDouble(5, proc_chance);
            ps.setBoolean(6, false);
            ps.setBoolean(7, false);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getEnchantments() {
        try {
            PreparedStatement getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments");
            ResultSet getEnchantsResult = getEnchants.executeQuery();
            List<String> enchants = new ArrayList<>();
            while (getEnchantsResult.next()) {
                enchants.add(getEnchantsResult.getString("name"));
            }
            return enchants;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName(String displayName) {
        try {
            PreparedStatement getName = getConnection().prepareStatement("SELECT * FROM enchantments WHERE display_name=?");
            getName.setString(1, displayName);
            ResultSet getNameResult = getName.executeQuery();
            if (getNameResult.next()) {
                return getNameResult.getString("name");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDisplayName(String name) {
        try {
            PreparedStatement getName = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getName.setString(1, name);
            ResultSet getNameResult = getName.executeQuery();
            if (getNameResult.next()) {
                String s = "";
                switch (getNameResult.getString("rarity")) {
                    case "common":
                        s += "§f";
                        break;
                    case "rare":
                        s += "§6";
                        break;
                    case "epic":
                        s += "§b";
                        break;
                    case "legendary":
                        s += "§5";
                        break;
                }
                s += getNameResult.getString("display_name");
                return s;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getLevelCap(String name) {
        try {
            PreparedStatement getLevelCap = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getLevelCap.setString(1, name);
            ResultSet getLevelCapResult = getLevelCap.executeQuery();
            if (getLevelCapResult.next()) {
                return getLevelCapResult.getInt("level_cap");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public double getProcChance(String name) {
        try {
            PreparedStatement getProcChance = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getProcChance.setString(1, name);
            ResultSet getProcChanceResult = getProcChance.executeQuery();
            if (getProcChanceResult.next()) {
                return getProcChanceResult.getDouble("proc_chance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean check(String name) {
        try {
            PreparedStatement check = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            check.setString(1, name);
            ResultSet checkResult = check.executeQuery();
            if (checkResult.next()) {
                return checkResult.getBoolean("active");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkNotify(String name) {
        try {
            PreparedStatement checkNotify = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            checkNotify.setString(1, name);
            ResultSet checkNotifyResult = checkNotify.executeQuery();
            if (checkNotifyResult.next()) {
                return checkNotifyResult.getBoolean("notify");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean exists(String name) {
        try {
            PreparedStatement getLevelCap = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getLevelCap.setString(1, name);
            ResultSet getLevelCapResult = getLevelCap.executeQuery();
            if (getLevelCapResult.next()) {
                getLevelCapResult.close();
                return true;
            }
            getLevelCapResult.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // other code:
    @Override
    public boolean exists(UUID player) {
        try {
            PreparedStatement exists = getConnection().prepareStatement("SELECT * FROM players WHERE UUID=?");
            exists.setString(1, player.toString());
            ResultSet existsResults = exists.executeQuery();
            // Does enchantment exist in database?
            if (existsResults.next()) {
                existsResults.close();
                return true;
            }
            existsResults.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setLobbySpawn(String world, String location) {
        try {
            PreparedStatement setLobbySpawn = getConnection().prepareStatement("INSERT IGNORE INTO maps (name, world, location) VALUES (?,?,?)");
            setLobbySpawn.setString(1, "Wartelobby");
            setLobbySpawn.setString(2, world);
            setLobbySpawn.setString(3, location);
            setLobbySpawn.executeUpdate();
            setLobbySpawn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLobbySpawn(String world, String location) {
        try {
            PreparedStatement updateLobbySpawn = getConnection().prepareStatement("UPDATE maps SET world=?,location=? WHERE name=?");
            updateLobbySpawn.setString(1, world);
            updateLobbySpawn.setString(2, location);
            updateLobbySpawn.setString(3, "Wartelobby");
            updateLobbySpawn.executeUpdate();
            updateLobbySpawn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkLobbySpawn() {
        try {
            PreparedStatement checkLobbySpawn = getConnection().prepareStatement("SELECT * FROM maps WHERE name=?");
            checkLobbySpawn.setString(1, "Wartelobby");
            ResultSet checkLobbySpawnResults = checkLobbySpawn.executeQuery();
            if (checkLobbySpawnResults.next()) {
                checkLobbySpawn.close();
                checkLobbySpawnResults.close();
                return true;
            }
            checkLobbySpawn.close();
            checkLobbySpawnResults.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getLobbySpawn() {
        try {
            PreparedStatement getLobbySpawn = getConnection().prepareStatement("SELECT * FROM maps WHERE name=?");
            getLobbySpawn.setString(1, "Wartelobby");
            ResultSet getLobbySpawnResult = getLobbySpawn.executeQuery();
            if (getLobbySpawnResult.next()) {
                String location = getLobbySpawnResult.getString("location");
                getLobbySpawn.close();
                getLobbySpawnResult.close();
                return location;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public void registerMap(String map, String world, String location) {
        try {
            PreparedStatement registerMap = getConnection().prepareStatement("INSERT IGNORE INTO maps (name, world, location) VALUES (?,?,?)");
            registerMap.setString(1, map);
            registerMap.setString(2, world);
            registerMap.setString(3, location);
            registerMap.executeUpdate();
            registerMap.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMap(String map, String world, String location) {
        try {
            PreparedStatement updateMap = getConnection().prepareStatement("UPDATE maps SET world=?,location=? WHERE name=?");
            updateMap.setString(1, world);
            updateMap.setString(2, location);
            updateMap.setString(3, map);
            updateMap.executeUpdate();
            updateMap.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkMap(String world) {
        try {
            PreparedStatement checkMap = getConnection().prepareStatement("SELECT * FROM maps WHERE world=?");
            checkMap.setString(1, world);
            ResultSet checkMapResult = checkMap.executeQuery();
            if (checkMapResult.next()) {
                checkMap.close();
                checkMapResult.close();
                return true;
            }
            checkMap.close();
            checkMapResult.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getMap(String world) {
        try {
            PreparedStatement getMap = getConnection().prepareStatement("SELECT * FROM maps WHERE world=?");
            getMap.setString(1, world);
            ResultSet getMapResult = getMap.executeQuery();
            if (getMapResult.next()) {
                String map = getMapResult.getString("name");
                getMap.close();
                getMapResult.close();
                return map;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public String getWorld(String map) {
        try {
            PreparedStatement getWorld = getConnection().prepareStatement("SELECT * FROM maps WHERE name=?");
            getWorld.setString(1, map);
            ResultSet getWorldResult = getWorld.executeQuery();
            if (getWorldResult.next()) {
                String world = getWorldResult.getString("world");
                getWorld.close();
                getWorldResult.close();
                return world;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    @Override
    public void setSpawnLocation(String world, String location) {
        try {
            PreparedStatement setSpawnLocation = getConnection().prepareStatement("INSERT IGNORE INTO spawnlocations (world, location) VALUES (?,?)");
            setSpawnLocation.setString(1, world);
            setSpawnLocation.setString(2, location);
            setSpawnLocation.executeUpdate();
            setSpawnLocation.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSpawnLocationsCount(String world) {
        try {
            PreparedStatement getSpawnLocationsCount = getConnection().prepareStatement("SELECT COUNT(*) FROM spawnlocations WHERE world=?");
            getSpawnLocationsCount.setString(1, world.toString());
            ResultSet getSpawnLocationsCountResult = getSpawnLocationsCount.executeQuery();
            Boolean helpBoolean = getSpawnLocationsCountResult.next();
            int helpInt = getSpawnLocationsCountResult.getInt(1);

            if (helpBoolean) {
                getSpawnLocationsCount.close();
                getSpawnLocationsCountResult.close();
                if (!helpBoolean) return 0;
                return helpInt;
            } else {
                getSpawnLocationsCount.close();
                getSpawnLocationsCountResult.close();
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<String> getSpawnLocations(String world) {
        try {
            PreparedStatement getSpawnLocation = getConnection().prepareStatement("SELECT * FROM spawnlocations WHERE world=?");
            getSpawnLocation.setString(1, world);
            ResultSet getSpawnLocationResults = getSpawnLocation.executeQuery();
            List<String> spawnLocationList = new ArrayList<>();
            for (int i = 0; i < getSpawnLocationsCount(world); i++) { // Fügt die Location-Strings zur Liste hinzu
                getSpawnLocationResults.next();
                spawnLocationList.add(getSpawnLocationResults.getString("location"));
            }
            return spawnLocationList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

