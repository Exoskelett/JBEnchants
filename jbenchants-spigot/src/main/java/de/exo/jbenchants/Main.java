package de.exo.jbenchants;

import de.exo.jbenchants.commands.*;
import de.exo.jbenchants.commands.admin.*;
import de.exo.jbenchants.enchants.EnchantsHandler;
import de.exo.jbenchants.events.GUIHandler;
import de.exo.jbenchants.handlers.ItemUpdater;
import de.exo.jbenchants.events.ItemMerger;
import de.exo.jbenchants.handlers.*;
import de.exo.jbenchants.items.cleanser.CleanserHandler;
import de.exo.jbenchants.items.crystal.CrystalHandler;
import de.exo.jbenchants.items.dust.DustHandler;
import de.exo.jbenchants.items.mystery_crystal.MysteryCrystalHandler;
import de.exo.jbenchants.items.scroll.RepairScrollHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public static API api = null;
    public static Main instance;

    @SuppressWarnings("unused")
    private static Economy eco = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        api = getAPI();

        if (api.isConnected()) {
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database is connected.");
            api.createDefaultTables();

            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(GUIHandler.getInstance(), this);
            pluginManager.registerEvents(ItemMerger.getInstance(), this);
            pluginManager.registerEvents(ItemUpdater.getInstance(), this);
            pluginManager.registerEvents(ToolReader.getInstance(), this);
            pluginManager.registerEvents(EnchantsHandler.getInstance(), this);
            // Cleanser
            pluginManager.registerEvents(CleanserHandler.getInstance(), this);
            getCommand("cleanser").setExecutor(new CleanserCommand());
            getCommand("cleanser").setTabCompleter(new CleanserCommand());
            // Crystal
            pluginManager.registerEvents(CrystalHandler.getInstance(), this);
            getCommand("crystal").setExecutor(new CrystalCommand());
            getCommand("crystal").setTabCompleter(new CrystalCommand());
            // Crystals
            getCommand("crystals").setExecutor(new CrystalsCommand());
            // Dust
            pluginManager.registerEvents(DustHandler.getInstance(), this);
            getCommand("dust").setExecutor(new DustCommand());
            getCommand("dust").setTabCompleter(new DustCommand());
            // Enchants
            getCommand("enchants").setExecutor(new EnchantsCommand());
            // Mystery Crystal
            pluginManager.registerEvents(MysteryCrystalHandler.getInstance(), this);
            // RepairScroll
            pluginManager.registerEvents(RepairScrollHandler.getInstance(), this);
            getCommand("repairscroll").setExecutor(new RepairScrollCommand());
            getCommand("repairscroll").setTabCompleter(new RepairScrollCommand());
            // Misc
            pluginManager.registerEvents(new CrystalsCommand(), this);
            pluginManager.registerEvents(new EnchantsCommand(), this);
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
        if (api == null || !api.isConnected()) {
            Configuration config = YamlConfiguration.loadConfiguration(new File("./plugins/JBEnchants", "config.yml"));
            String host = config.getString("host");
            int port = config.getInt("port");
            String database = config.getString("database");
            String username = config.getString("username");
            String password = config.getString("password");
            api = new MySQL(host, port, database, username, password);
        }
        return api;
    }

    @SuppressWarnings("unused")
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        eco = rsp.getProvider();
        return true;
    }
}
