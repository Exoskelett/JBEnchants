package de.exo.jbenchants;

import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void createDefaultTables() {
        try {
            // enchantments
            PreparedStatement ps1 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS enchantments "
                    + "(id int NOT NULL AUTO_INCREMENT, name varchar(20), display_name varchar(20), rarity varchar(10), category varchar(10), level_cap int, proc_chance double, active boolean, proccable boolean, notify boolean, material varchar(30), lore varchar(255), PRIMARY KEY (id))");
            ps1.executeUpdate();

            PreparedStatement ps2 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS rarities "
                    + "(id int NOT NULL AUTO_INCREMENT, rarity varchar(10), prefix varchar(20), color varchar(10), PRIMARY KEY (id))");
            ps2.executeUpdate();
            PreparedStatement ps2Check = getConnection().prepareStatement("SELECT COUNT(*) FROM rarities");
            ResultSet ps2CheckResult = ps2Check.executeQuery();
            ps2CheckResult.next();
            int row2Count = ps2CheckResult.getInt(1);
            ps2Check.close();
            if (row2Count == 0) {
            String[] rarities = {"legendary", "epic", "rare", "common", "special"};
            String[] colors = {"§5", "§b", "§6", "§f", "§7"};
            for (int i = 0; i < rarities.length; i++) {
                String query = "INSERT IGNORE INTO rarities (rarity, color) VALUES (?,?)";
                PreparedStatement ps3 = getConnection().prepareStatement(query);
                ps3.setString(1, rarities[i]);
                ps3.setString(2, colors[i]);
                ps3.executeUpdate();
            }
            }

            PreparedStatement ps4 = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS items "
                    + "(id int NOT NULL AUTO_INCREMENT, type varchar(20), display_name varchar(50), lore varchar(255), PRIMARY KEY (id))");
            ps4.executeUpdate();
            PreparedStatement ps4Check = getConnection().prepareStatement("SELECT COUNT(*) FROM items");
            ResultSet ps4CheckResult = ps4Check.executeQuery();
            ps4CheckResult.next();
            int row4Count = ps4CheckResult.getInt(1);
            ps4Check.close();
            if (row4Count == 0) {
                String[] types = {"crystal", "mysteryCrystal", "dust", "cleanser", "scroll"};
                for (int i = 0; i < 5; i++) {
                    String query = "INSERT IGNORE INTO items (type, display_name, lore) VALUES (?,?,?)";
                    PreparedStatement ps5 = getConnection().prepareStatement(query);
                    ps5.setString(1, types[i]);
                    ps5.setString(2, null);
                    ps5.setString(3, null);
                    ps5.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEnchantment(String name, String display_name, String rarity) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT IGNORE INTO enchantments (name, display_name, rarity, category, level_cap, proc_chance, active, proccable, notify, material, lore) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, display_name);
            ps.setString(3, rarity);
            ps.setString(4, null);
            ps.setInt(5, 0);
            ps.setDouble(6, 0);
            ps.setBoolean(7, false);
            ps.setBoolean(8, false);
            ps.setBoolean(9, false);
            ps.setString(10, "ENCHANTED_BOOK");
            ps.setString(11, "N/A");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getEnchantmentMaterial(String name) {
        try {
            PreparedStatement getEnchantmentMaterial = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getEnchantmentMaterial.setString(1, name);
            ResultSet getEnchantmentMaterialResults = getEnchantmentMaterial.executeQuery();
            if (getEnchantmentMaterialResults.next()) {
                return getEnchantmentMaterialResults.getString("material");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getEnchantmentLore(String name) {
        try {
            PreparedStatement getEnchantmentLore = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getEnchantmentLore.setString(1, name);
            ResultSet getEnchantmentLoreResults = getEnchantmentLore.executeQuery();
            if (getEnchantmentLoreResults.next()) {
                return getEnchantmentLoreResults.getString("lore");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
    public List<String> getEnchantments(String category) {  // categories: tool, axe, weapon, armor, fishing, bow, common, rare, epic, legendary
        try {
            PreparedStatement getEnchants;
            switch (category) {
                case "common", "rare", "epic", "legendary":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE rarity=?");
                    getEnchants.setString(1, category);
                    break;
                case "axe":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE category='tool' OR category='weapon' OR category='bow' OR category='*'");
                    break;
                case "weapon":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE category='weapon' OR category='bow' OR category='*'");
                    break;
                case "helmet", "chestplate", "leggings", "boots":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE category='armor' OR category=? OR category='*'");
                    getEnchants.setString(1, category);
                    break;
                case "armor":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE category='armor' OR category='helmet' OR category='chestplate' OR category='leggings' OR category='boots' OR category='*'");
                    break;
                case "fishing":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE category='fishing' OR category='*'");
                    break;
                case "bow":
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments "
                            + "WHERE category='bow' OR category='*'");
                    break;
                default:
                    getEnchants = getConnection().prepareStatement("SELECT * FROM enchantments WHERE category=? OR category='*'");
                    getEnchants.setString(1, category);
                    break;
            }
            ResultSet getEnchantsResult = getEnchants.executeQuery();
            List<String> enchants = new ArrayList<>();
            while (getEnchantsResult.next()) {
                if (!((category.equals("axe") || category.equals("weapon")) && getEnchantsResult.getString("name").equals("pierce")))
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
            displayName = displayName.substring(2);
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
                return getNameResult.getString("display_name");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRarity(String name) {
        try {
            PreparedStatement getRarity = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            getRarity.setString(1, name);
            ResultSet getRarityResult = getRarity.executeQuery();
            if (getRarityResult.next()) {
                return getRarityResult.getString("rarity");
            }
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
            PreparedStatement exists = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            exists.setString(1, name);
            ResultSet existsResult = exists.executeQuery();
            if (existsResult.next()) {
                existsResult.close();
                return true;
            }
            existsResult.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean check(String name, String dbString) {
        try {
            PreparedStatement check = getConnection().prepareStatement("SELECT * FROM enchantments WHERE name=?");
            check.setString(1, name);
            ResultSet checkResult = check.executeQuery();
            if (checkResult.next()) {
                return checkResult.getBoolean(dbString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getColor(String rarity) {
        try {
            PreparedStatement getEnchantmentColor = getConnection().prepareStatement("SELECT * FROM rarities WHERE rarity=?");
            getEnchantmentColor.setString(1, rarity);
            ResultSet getEnchantmentColorResult = getEnchantmentColor.executeQuery();
            if (getEnchantmentColorResult.next()) {
                return getEnchantmentColorResult.getString("color");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPrefix(String rarity) {
        try {
            PreparedStatement getEnchantmentPrefix = getConnection().prepareStatement("SELECT * FROM rarities WHERE rarity=?");
            getEnchantmentPrefix.setString(1, rarity);
            ResultSet getEnchantmentPrefixResult = getEnchantmentPrefix.executeQuery();
            if (getEnchantmentPrefixResult.next()) {
                return getEnchantmentPrefixResult.getString("prefix");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Items

    @Override
    public String getItemName(String type) {
        try {
            PreparedStatement getCrystalName = getConnection().prepareStatement("SELECT * FROM items WHERE type=?");
            getCrystalName.setString(1, type);
            ResultSet getCrystalNameResults = getCrystalName.executeQuery();
            if (getCrystalNameResults.next()) {
                return getCrystalNameResults.getString("display_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] getItemLore(String type) {
        try {
            PreparedStatement getCrystalLore = getConnection().prepareStatement("SELECT * FROM items WHERE type=?");
            getCrystalLore.setString(1, type);
            ResultSet getCrystalLoreResults = getCrystalLore.executeQuery();
            if (getCrystalLoreResults.next()) {
                return getCrystalLoreResults.getString("lore").split(":nl:");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }














    // other code:
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

