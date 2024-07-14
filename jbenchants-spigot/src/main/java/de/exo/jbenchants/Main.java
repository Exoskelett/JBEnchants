package de.exo.jbenchants;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.exo.jbenchants.commands.*;
import de.exo.jbenchants.events.GUIHandler;
import de.exo.jbenchants.events.ItemUpdater;
import de.exo.jbenchants.events.ItemMerger;
import de.exo.jbenchants.handlers.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public API api;
    public JBEnchantNBT nbt;
    public JBEnchantLore lore;
    public JBEnchantRegions regions;
    public JBEnchantHandler handler;
    public JBEnchantItems items;
    public GUIHandler guiHandler;
    public static Main instance;

    private static Economy eco = null;

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
        regions = new JBEnchantRegions();
        handler = new JBEnchantHandler();
        items = new JBEnchantItems();
        guiHandler = new GUIHandler();

        if (api.isConnected()) {
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database is connected.");
            api.createDefaultTables();

            Bukkit.getPluginManager().registerEvents(new GUIHandler(), this);
            Bukkit.getPluginManager().registerEvents(new ItemMerger(), this);
            Bukkit.getPluginManager().registerEvents(new ItemUpdater(), this);
            Bukkit.getPluginManager().registerEvents(new ToolReader(), this);
            Bukkit.getPluginManager().registerEvents(new JBEnchantHandler(), this);
            getCommand("cleanser").setExecutor(new Cleanser());
            getCommand("cleanser").setTabCompleter(new Cleanser());
            getCommand("crystal").setExecutor(new Crystal());
            getCommand("crystal").setTabCompleter(new Crystal());
            getCommand("crystals").setExecutor(new Crystals());
            getCommand("dust").setExecutor(new Dust());
            getCommand("dust").setTabCompleter(new Dust());
            getCommand("enchants").setExecutor(new Enchants());
            getCommand("repairscroll").setExecutor(new RepairScroll());
            getCommand("repairscroll").setTabCompleter(new RepairScroll());
            getCommand("rewards").setExecutor(new Rewards());
            getCommand("jbe").setExecutor(new jbe());
            getCommand("jbe").setTabCompleter(new jbe());
            getCommand("test").setExecutor(new test());
        } else
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database has yet to be configured.");
    }

    @Override
    public void onDisable() {

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        eco = rsp.getProvider();
        return true;
    }
}
