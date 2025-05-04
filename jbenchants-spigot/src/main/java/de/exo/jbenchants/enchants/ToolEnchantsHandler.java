package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.tool.*;
import de.exo.jbenchants.items.Lore;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToolEnchantsHandler {
    private static ToolEnchantsHandler INSTANCE;

    public static ToolEnchantsHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ToolEnchantsHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsMeta meta = EnchantsMeta.getInstance();
    EnchantsHandler enchantsHandler = EnchantsHandler.getInstance();

    public void proccToolEnchant(Player player, ItemStack tool, List<String> list, Block block) throws InterruptedException {
        List<String> notify = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean[] enchantAttributes = this.api.check(list.get(i), "active", "proccable", "notify");
            if (enchantAttributes[0] && enchantAttributes[1]) {
                if (enchantAttributes[2])
                    notify.add(list.get(i));
                activateToolEnchant(player, tool, list.get(i), block);
            }
        }
        notify = meta.sortEnchants(notify);
        enchantsHandler.notify(player, notify);
    }

    public void activateToolEnchant(Player player, ItemStack tool, String name, Block block) throws InterruptedException {
        switch (name) {
            case "alchemy":
                new AlchemyEnchant(player, tool, block);
                break;
            case "freeze":
                new FreezeEnchant(player, tool, block);
                break;
            case "jump_boost":
                new JumpBoostEnchant(player);
                break;
            case "speed_boost":
                new SpeedBoostEnchant(player);
                break;
            case "treasure_hunter":
                new TreasureHunterEnchant(player, block);
                break;
            case "drill":
                new DrillEnchant(player, tool, name, block);
                break;
            case "midas_touch":
                new MidasTouchEnchant(player, block);
                break;
            case "night_vision":
                new NightVisionEnchant(player);
                break;
            case "ore_extractor":
                new OreExtractorEnchant(player, tool, name, block);
                break;
            case "regenerate":
                new RegenerateEnchant(player, tool, block);
                break;
            case "spare_change":
                new SpareChangeEnchant(player, tool, name);
                break;
            case "haste":
                new HasteEnchant(player, tool, name);
                break;
            case "make_it_rain":
                new MakeItRainEnchant(player, tool, name, block);
                break;
            case "repair":
                new RepairEnchant(player, tool, name);
                break;
            case "slots":
                new SlotsEnchant(player, block);
                break;
            case "smelting":
                new SmeltingEnchant(player, block);
                break;
            case "stop_that":
                new StopThatEnchant(player, block);
                break;
            case "explosive":
                new ExplosiveEnchant(player, tool, name, block);
                break;
            case "lucky":
                new LuckyEnchant();
                break;
            case "super_breaker":
                new SuperBreakerEnchant(player, tool, name);
                break;
            case "vein_miner":
                new VeinMinerEnchant(player, tool, name, block);
                break;
        }
    }
}
