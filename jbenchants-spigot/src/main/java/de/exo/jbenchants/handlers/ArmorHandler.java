package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArmorHandler {
    private static ArmorHandler INSTANCE;

    public static ArmorHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ArmorHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsMeta lore = EnchantsMeta.getInstance();
    ArmorEnchantsHandler handler = ArmorEnchantsHandler.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) throws InterruptedException {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Entity attacker = event.getDamager();
        if (player == attacker) return;

        List<ItemStack> armor = new ArrayList<>(List.of(player.getInventory().getArmorContents()));
        if (player.getInventory().getItemInOffHand().getType() != Material.AIR) armor.add(player.getInventory().getItemInOffHand());
        List<String> procced = new ArrayList<>();
        for (ItemStack item : armor) {
            List<String> enchants = nbt.getEnchants(item);
            if (enchants == null) continue;
            for (int i = 0; i < enchants.size(); i++) {
                String enchant = enchants.get(i);
                if (api.check(enchant, "proccable")) {
                    double chance = (api.getProcChance(enchant)*nbt.getEnchantmentLevel(item, enchant)+3)/100;  // (level * procc_chance + 3) %
                    if (Math.random() <= chance) {
                        switch (enchant) { // enchant specific actions:
                            case "mending":
                                PlayerInventory inv = player.getInventory();
                                if (Objects.requireNonNull(inv.getHelmet()).isSimilar(item)) {
                                    procced.add(enchant+"_helmet");
                                } else if (Objects.requireNonNull(inv.getChestplate()).isSimilar(item)) {
                                    procced.add(enchant+"_chestplate");
                                } else if (Objects.requireNonNull(inv.getLeggings()).isSimilar(item)) {
                                procced.add(enchant+"_leggings");
                                } else if (Objects.requireNonNull(inv.getBoots()).isSimilar(item)) {
                                procced.add(enchant+"_boots");
                                } else if (inv.getItemInOffHand().isSimilar(item))
                                    procced.add(enchant+"_offhand");
                                break;
                            default:
                                procced.add(enchant);
                        }
                    }
                }
            }
        }
//        if (!procced.isEmpty())
//            handler.proccArmorEnchant(player, item, procced, block);


//        ItemStack item = player.getInventory().getItemInMainHand();
//        if (item.getType().equals(Material.AIR)) return;
//        List<String> enchants = nbt.getEnchants(item);
//        Block block = event.getBlock();
//        if (nbt.checkLockedBlock(block)) {
//            player.sendMessage("§cThis block is locked, you cannot break it.");
//            event.setCancelled(true);
//            return;
//        }
//        if (enchants != null) {
//            if (regions.checkBlock(block, "mine")) {
//                if (block.getType().equals(Material.PACKED_ICE) || block.getType().equals(Material.TRAPPED_CHEST)) {
//                    event.setCancelled(true);
//                    block.setType(Material.AIR);
//                    return;
//                }
//                List<String> procced = new ArrayList<>();
//                for (int i = 0; i < enchants.size(); i++) {
//                    String enchant = enchants.get(i);
//                    if (api.check(enchant, "proccable")) {
//                        double chance = api.getProcChance(enchant)*nbt.getEnchantmentLevel(item, enchant)/100;
//                        if (Math.random() <= chance) {
//                            switch (enchant) { // enchant specific actions:
//                                case "alchemy":
//                                    if (!(block.getType().equals(Material.COAL_ORE) || block.getType().equals(Material.IRON_ORE) || block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.DIAMOND_ORE)
//                                            || block.getType().equals(Material.COAL_BLOCK) || block.getType().equals(Material.COAL_BLOCK) || block.getType().equals(Material.COAL_BLOCK) || block.getType().equals(Material.COAL_BLOCK))) break;
//                                    event.setDropItems(false);
//                                    procced.add(enchant);
//                                    break;
//                                case "smelting":
//                                    if (!(block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.IRON_ORE) || block.getType().equals(Material.COBBLESTONE) || block.getType().equals(Material.STONE) || block.getType().toString().contains("LOG"))) break;
//                                    event.setCancelled(true);
//                                    procced.add(enchant);
//                                    break;
//                                case "treasure_hunter", "regenerate":
//                                    event.setCancelled(true);
//                                    procced.add(enchant);
//                                    break;
//                                case "super_breaker":
//                                    if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING) && Objects.requireNonNull(player.getPotionEffect(PotionEffectType.FAST_DIGGING)).getAmplifier() != 0) continue;
//                                default:
//                                    procced.add(enchant);
//                            }
//                        }
//                    }
//                }
//                if (!procced.isEmpty())
//                    handler.proccToolEnchant(player, item, procced, block);
//            } else
//                player.sendMessage("§cDu bist in keiner Mine");
//        }
    }
}
