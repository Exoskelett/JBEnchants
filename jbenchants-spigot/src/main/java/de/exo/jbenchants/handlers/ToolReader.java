package de.exo.jbenchants.handlers;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToolReader implements Listener {

    API api = Main.instance.api;
    JBEnchantNBT nbt = Main.instance.nbt;
    JBEnchantLore lore = Main.instance.lore;
    JBEnchantHandler handler = Main.instance.handler;
    JBEnchantRegions regions = Main.instance.regions;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) return;
        List<String> enchants = nbt.getEnchants(item);
        Block block = event.getBlock();
        if (nbt.checkLockedBlock(block)) {
            player.sendMessage("§cThis block is locked, you cannot break it.");
            event.setCancelled(true);
            return;
        }
        if (enchants != null) {
            if (regions.checkBlock(block, "mine")) {
                if (block.getType().equals(Material.PACKED_ICE) || block.getType().equals(Material.TRAPPED_CHEST)) {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                    return;
                }
                List<String> procced = new ArrayList<>();
                for (int i = 0; i < enchants.size(); i++) {
                    String enchant = enchants.get(i);
                    if (api.check(enchant, "proccable")) {
                        double chance = api.getProcChance(enchant)*nbt.getEnchantmentLevel(item, enchant)/100;
                        if (Math.random() <= chance) {
                            switch (enchant) { // enchant specific actions:
                                case "alchemy":
                                    if (!(block.getType().equals(Material.COAL_ORE) || block.getType().equals(Material.IRON_ORE) || block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.DIAMOND_ORE)
                                    || block.getType().equals(Material.COAL_BLOCK) || block.getType().equals(Material.COAL_BLOCK) || block.getType().equals(Material.COAL_BLOCK) || block.getType().equals(Material.COAL_BLOCK))) break;
                                    event.setDropItems(false);
                                    procced.add(enchant);
                                    break;
                                case "smelting":
                                    if (!(block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.IRON_ORE) || block.getType().equals(Material.COBBLESTONE) || block.getType().equals(Material.STONE) || block.getType().toString().contains("LOG"))) break;
                                    event.setCancelled(true);
                                    procced.add(enchant);
                                    break;
                                case "treasure_hunter", "regenerate":
                                    event.setCancelled(true);
                                    procced.add(enchant);
                                    break;
                                case "super_breaker":
                                    if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING) && Objects.requireNonNull(player.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() != 0) continue;
                                default:
                                    procced.add(enchant);
                                    break;
                            }
                        }
                    }
                }
                if (!procced.isEmpty())
                    handler.proccToolEnchant(player, item, procced, block);
            } else
                player.sendMessage("§cDu bist in keiner Mine");
        }
    }
}
