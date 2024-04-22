package de.exo.jbenchants;

//import com.comphenix.protocol.ProtocolLibrary;
//import com.comphenix.protocol.ProtocolManager;
import de.exo.jbenchants.commands.jbe;
import de.exo.jbenchants.commands.test;
import de.exo.jbenchants.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public API api;
    public JBEnchantData.NBT nbt;
    public JBEnchantData.Lore lore;
    public JBEnchantData.Handler handler;
    public static Main instance;
    //public static ProtocolManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        // ProtocolLib
        //manager = ProtocolLibrary.getProtocolManager();

        // MySQL-Database
        Configuration config = YamlConfiguration.loadConfiguration(new File("./plugins/JBEnchants", "config.yml"));
        String host = config.getString("host");
        String port = config.getString("port");
        String database = config.getString("database");
        String username = config.getString("username");
        String password = config.getString("password");

        this.api = new MySQL(host, database, username, password);

        if (api.isConnected()) {
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database is connected.");
            api.createDefaultTables();

            Bukkit.getPluginManager().registerEvents(new ToolReader(), this);
            getCommand("jbe").setExecutor(new jbe());
            getCommand("jbe").setTabCompleter(new jbe());
            getCommand("test").setExecutor(new test());
        } else
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database has yet to be configured.");
    }

    @Override
    public void onDisable() {

    }
}
