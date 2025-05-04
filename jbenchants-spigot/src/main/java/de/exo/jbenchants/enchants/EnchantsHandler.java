package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.tool.AlchemyEnchant;
import de.exo.jbenchants.enchants.tool.DrillEnchant;
import de.exo.jbenchants.enchants.tool.ExplosiveEnchant;
import de.exo.jbenchants.enchants.tool.FreezeEnchant;
import de.exo.jbenchants.enchants.tool.HasteEnchant;
import de.exo.jbenchants.enchants.tool.JumpBoostEnchant;
import de.exo.jbenchants.enchants.tool.LuckyEnchant;
import de.exo.jbenchants.enchants.tool.MakeItRainEnchant;
import de.exo.jbenchants.enchants.tool.MidasTouchEnchant;
import de.exo.jbenchants.enchants.tool.NightVisionEnchant;
import de.exo.jbenchants.enchants.tool.OreExtractorEnchant;
import de.exo.jbenchants.enchants.tool.RegenerateEnchant;
import de.exo.jbenchants.enchants.tool.RepairEnchant;
import de.exo.jbenchants.enchants.tool.SlotsEnchant;
import de.exo.jbenchants.enchants.tool.SmeltingEnchant;
import de.exo.jbenchants.enchants.tool.SpareChangeEnchant;
import de.exo.jbenchants.enchants.tool.SpeedBoostEnchant;
import de.exo.jbenchants.enchants.tool.StopThatEnchant;
import de.exo.jbenchants.enchants.tool.SuperBreakerEnchant;
import de.exo.jbenchants.enchants.tool.TreasureHunterEnchant;
import de.exo.jbenchants.enchants.tool.VeinMinerEnchant;
import de.exo.jbenchants.items.crystal.LevelEnchants;
import de.tr7zw.nbtapi.NBTItem;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class EnchantsHandler implements Listener {
    private static EnchantsHandler INSTANCE;

    public static EnchantsHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EnchantsHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsMeta meta = EnchantsMeta.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();

    public void notify(Player player, List<String> list) {
        StringBuilder s = new StringBuilder();
        for (int k = 0; k < list.size(); k++) {
            if (k < list.size() - 1) {
                s.append(list.get(k)).append("§7, ");
            } else {
                s.append(list.get(k));
            }
        }
        if (s != null)
            player.sendActionBar(s + " procced.");
    }

    public boolean checkEnchantUpgradability(Player player, ItemStack item, String enchant) {
        NBTItem nbti = new NBTItem(item);
        if (nbt.getEnchants(item).size() < (LevelEnchants.getPlayerMaxEnchants(player)).maxEnchants) {
            if (nbti.hasTag(enchant))
                return (api.getLevelCap(enchant) > nbti.getInteger(enchant).intValue());
            return true;
        }
        if (nbti.hasTag(enchant))
            return (api.getLevelCap(enchant) > nbti.getInteger(enchant).intValue());
        return false;
    }

    public List<String> getPossibleEnchants(Player player, ItemStack item, String rarity) {
        List<String> possibleEnchants = api.getEnchantments(rarity);
        List<String> typeSpecificEnchants = api.getEnchantments(nbt.getCategory(item));
        possibleEnchants.retainAll(typeSpecificEnchants);
        List<String> itemEnchants = nbt.getEnchants(item);
        itemEnchants.removeIf(enchant -> !api.getRarity(enchant).equals(rarity));
        if (nbt.getEnchants(item).size() < (LevelEnchants.getPlayerMaxEnchants(player)).maxEnchants) {
            if (!itemEnchants.isEmpty())
                for (String enchant : itemEnchants) {
                    if (!checkEnchantUpgradability(player, item, enchant))
                        possibleEnchants.remove(enchant);
                }
            return possibleEnchants;
        }
        if (!itemEnchants.isEmpty()) {
            int initialSize = possibleEnchants.size();
            for (String enchant : itemEnchants) {
                if (!checkEnchantUpgradability(player, item, enchant))
                    possibleEnchants.remove(enchant);
            }
            if (possibleEnchants.size() + itemEnchants.size() == initialSize)
                return null;
            return possibleEnchants;
        }
        return null;
    }
}
