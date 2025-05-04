package de.exo.jbenchants;

import de.exo.jbenchants.commands.CrystalsCommand;
import de.exo.jbenchants.commands.EnchantsCommand;
import de.exo.jbenchants.commands.admin.CleanserCommand;
import de.exo.jbenchants.commands.admin.CrystalCommand;
import de.exo.jbenchants.commands.admin.DustCommand;
import de.exo.jbenchants.commands.admin.RepairScrollCommand;
import de.exo.jbenchants.commands.jbdebug;
import de.exo.jbenchants.commands.jbe;
import de.exo.jbenchants.enchants.EnchantsHandler;
import de.exo.jbenchants.enchants.armor.ReinforcedEnchant;
import de.exo.jbenchants.enchants.tool.StopThatEnchant;
import de.exo.jbenchants.enchants.tool.TreasureHunterEnchant;
import de.exo.jbenchants.events.GUIHandler;
import de.exo.jbenchants.handlers.ItemUpdater;
import de.exo.jbenchants.handlers.ToolHandler;
import de.exo.jbenchants.items.ItemMerger;
import de.exo.jbenchants.items.cleanser.CleanserHandler;
import de.exo.jbenchants.items.crystal.CrystalHandler;
import de.exo.jbenchants.items.dust.DustHandler;
import de.exo.jbenchants.items.mystery_crystal.MysteryCrystalHandler;
import de.exo.jbenchants.items.scroll.RepairScrollHandler;
import java.io.File;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static API api = null;

    public static Main instance;

    private static Economy eco = null;

    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        api = getAPI();
        if (api.isConnected()) {
            Bukkit.getLogger().info("[JBEnchants] MySQL-Database is connected.");
            api.createDefaultTables();
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(GUIHandler.getInstance(), this);
            pluginManager.registerEvents(ItemUpdater.getInstance(), this);
            pluginManager.registerEvents(ToolHandler.getInstance(), this);
            pluginManager.registerEvents(EnchantsHandler.getInstance(), this);
            pluginManager.registerEvents(CleanserHandler.getInstance(), this);
            getCommand("cleanser").setExecutor(new CleanserCommand());
            getCommand("cleanser").setTabCompleter(new CleanserCommand());
            pluginManager.registerEvents(CrystalHandler.getInstance(), this);
            getCommand("crystal").setExecutor(new CrystalCommand());
            getCommand("crystal").setTabCompleter(new CrystalCommand());
            getCommand("crystals").setExecutor(new CrystalsCommand());
            pluginManager.registerEvents(DustHandler.getInstance(), this);
            getCommand("dust").setExecutor(new DustCommand());
            getCommand("dust").setTabCompleter(new DustCommand());
            getCommand("enchants").setExecutor(new EnchantsCommand());
            pluginManager.registerEvents(MysteryCrystalHandler.getInstance(), this);
            pluginManager.registerEvents(RepairScrollHandler.getInstance(), this);
            getCommand("repairscroll").setExecutor(new RepairScrollCommand());
            getCommand("repairscroll").setTabCompleter(new RepairScrollCommand());
            pluginManager.registerEvents(new CrystalsCommand(), this);
            pluginManager.registerEvents(new EnchantsCommand(), this);
            pluginManager.registerEvents(new ItemMerger(), this);
            getCommand("jbe").setExecutor(new jbe());
            getCommand("jbe").setTabCompleter(new jbe());
            getCommand("jbdebug").setExecutor(new jbdebug());
            pluginManager.registerEvents(new ReinforcedEnchant(), this);
            pluginManager.registerEvents(new StopThatEnchant(), this);
            pluginManager.registerEvents(new TreasureHunterEnchant(), this);
        } else {
            Bukkit.getLogger().warning("[JBEnchants] MySQL-Database has yet to be configured.");
        }
    }

    public void onDisable() {}

    public static API getAPI() {
        if (api == null || !api.isConnected()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File("./plugins/JBEnchants", "config.yml"));
            String host = yamlConfiguration.getString("host");
            int port = yamlConfiguration.getInt("port");
            String database = yamlConfiguration.getString("database");
            String username = yamlConfiguration.getString("username");
            String password = yamlConfiguration.getString("password");
            api = new MySQL(host, port, database, username, password);
        }
        return api;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        eco = (Economy)rsp.getProvider();
        return true;
    }
}
