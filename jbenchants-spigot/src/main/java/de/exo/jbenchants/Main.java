package de.exo.jbenchants;

import de.exo.jbenchants.commands.*;
import de.exo.jbenchants.events.Crystals;
import de.exo.jbenchants.events.Merging;
import de.exo.jbenchants.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public API api;
    public JBEnchantNBT nbt;
    public JBEnchantLore lore;
    public JBEnchantHandler handler;
    public JBEnchantItems items;
    public static Main instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        // MySQL-Database
        Configuration config = YamlConfiguration.loadConfiguration(new File("./plugins/JBEnchants", "config.yml"));
        String host = config.getString("host");
        String port = config.getString("port");
        String database = config.getString("database");
        String username = config.getString("username");
        String password = config.getString("password");

        this.api = new MySQL(host, database, username, password);
        nbt = new JBEnchantNBT();
        lore = new JBEnchantLore();
        handler = new JBEnchantHandler();
        items = new JBEnchantItems();

        if (api.isConnected()) {
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database is connected.");
            api.createDefaultTables();

            Bukkit.getPluginManager().registerEvents(new Merging(), this);
            Bukkit.getPluginManager().registerEvents(new ToolReader(), this);
            getCommand("cleanser").setExecutor(new Cleanser());
            getCommand("cleanser").setTabCompleter(new Cleanser());
            getCommand("crystal").setExecutor(new Crystal());
            getCommand("crystal").setTabCompleter(new Crystal());
            Bukkit.getPluginManager().registerEvents(new Crystals(), this);
            getCommand("dust").setExecutor(new Dust());
            getCommand("dust").setTabCompleter(new Dust());
            getCommand("repairscroll").setExecutor(new RepairScroll());
            getCommand("repairscroll").setTabCompleter(new RepairScroll());
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
