package de.exo.jbenchants;

import de.exo.jbenchants.commands.*;
import de.exo.jbenchants.commands.admin.*;
import de.exo.jbenchants.events.GUIHandler;
import de.exo.jbenchants.events.ItemUpdater;
import de.exo.jbenchants.events.ItemMerger;
import de.exo.jbenchants.handlers.*;
import de.exo.jbenchants.items.Crystal;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public static API api = getAPI();;
    public static Main instance;

    private static Economy eco = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        if (api.isConnected()) {
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database is connected.");
            api.createDefaultTables();

            Bukkit.getPluginManager().registerEvents(GUIHandler.getInstance(), this);
            Bukkit.getPluginManager().registerEvents(ItemMerger.getInstance(), this);
            Bukkit.getPluginManager().registerEvents(ItemUpdater.getInstance(), this);
            Bukkit.getPluginManager().registerEvents(ToolReader.getInstance(), this);
            Bukkit.getPluginManager().registerEvents(JBEnchantHandler.getInstance(), this);
            getCommand("cleanser").setExecutor(new Cleanser());
            getCommand("cleanser").setTabCompleter(new Cleanser());
            Bukkit.getPluginManager().registerEvents(Crystal.getInstance(), this);
            getCommand("crystal").setExecutor(Crystal.getInstance());
            getCommand("crystal").setTabCompleter(Crystal.getInstance());
            getCommand("crystals").setExecutor(new Crystals());
            getCommand("dust").setExecutor(new Dust());
            getCommand("dust").setTabCompleter(new Dust());
            getCommand("enchants").setExecutor(new Enchants());
            getCommand("repairscroll").setExecutor(new RepairScroll());
            getCommand("repairscroll").setTabCompleter(new RepairScroll());
            getCommand("jbe").setExecutor(new jbe());
            getCommand("jbe").setTabCompleter(new jbe());
            getCommand("jbdebug").setExecutor(new jbdebug());
        } else
            Bukkit.getLogger().warning("[JBEnchants] MySQL-Database has yet to be configured.");
    }

    @Override
    public void onDisable() {

    }

    public static API getAPI() {
        if (!api.isConnected()) {
            Configuration config = YamlConfiguration.loadConfiguration(new File("./plugins/JBEnchants", "config.yml"));
            String host = config.getString("host");
            String port = config.getString("port");
            String database = config.getString("database");
            String username = config.getString("username");
            String password = config.getString("password");
            api = new MySQL(host, database, username, password);
        }
        return api;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        eco = rsp.getProvider();
        return true;
    }
}
