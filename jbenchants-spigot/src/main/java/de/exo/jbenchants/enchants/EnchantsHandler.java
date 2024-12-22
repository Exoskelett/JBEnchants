package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.items.crystal.LevelEnchants;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.*;

public class EnchantsHandler implements Listener {

    private static EnchantsHandler INSTANCE;

    private EnchantsHandler() {
    }

    public static EnchantsHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EnchantsHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsMeta lore = EnchantsMeta.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();

    public void proccToolEnchant(Player player, ItemStack tool, List<String> list, Block block)
            throws InterruptedException {
        List<String> notify = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean[] enchantAttributes = api.check(list.get(i), "active", "proccable", "notify");
            if (!enchantAttributes[0] || !enchantAttributes[1]) continue;
            if (enchantAttributes[2]) notify.add(list.get(i));
            activateToolEnchant(player, tool, list.get(i), block);
        }
        notify = lore.sortEnchants(notify);
        notify(player, notify);
    }

    public void proccArmorEnchant(Player player, Player target, ItemStack armor, List<String> list) {
        List<String> notify = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean[] enchantAttributes = api.check(list.get(i), "active", "proccable", "notify");
            if (!enchantAttributes[0] || !enchantAttributes[1]) continue;
            if (enchantAttributes[2]) notify.add(list.get(i));
            activateArmorEnchant(player, target, armor, list.get(i));
        }
        notify = lore.sortEnchants(notify);
        notify(player, notify);
    }

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
            ((Audience) player).sendActionBar(Component.text(s + " procced.", NamedTextColor.GRAY));
    }

    private int taskId, index;

    @SuppressWarnings("incomplete-switch")
    public void activateToolEnchant(Player player, ItemStack tool, String name, Block block)
            throws InterruptedException {
        switch (name) {
            // common enchants
            case "alchemy":
                if (block.getType().toString().contains("ORE")) {
                    switch (block.getType()) {
                        case COAL_ORE -> block.setType(Material.IRON_ORE, false);
                        case IRON_ORE -> block.setType(Material.GOLD_ORE, false);
                        case GOLD_ORE -> block.setType(Material.DIAMOND_ORE, false);
                        case DIAMOND_ORE -> block.setType(Material.EMERALD_ORE, false);
                    }
                } else if (block.getType().toString().contains("BLOCK")) {
                    switch (block.getType()) {
                        case COAL_BLOCK -> block.setType(Material.IRON_BLOCK, false);
                        case IRON_BLOCK -> block.setType(Material.GOLD_BLOCK, false);
                        case GOLD_BLOCK -> block.setType(Material.DIAMOND_BLOCK, false);
                        case DIAMOND_BLOCK -> block.setType(Material.EMERALD_BLOCK, false);
                    }
                }
                for (ItemStack drop : block.getDrops(tool)) {
                    player.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
                break;
            case "freeze":
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        for (int k = -1; k < 2; k++) {
                            Block frozenBlock = player.getWorld().getBlockAt(block.getLocation().getBlockX() + j,
                                    block.getLocation().getBlockY() + i, block.getLocation().getBlockZ() + k);
                            if (!frozenBlock.getType().equals(Material.AIR)
                                    && !frozenBlock.getType().equals(Material.PACKED_ICE)
                                    && regions.checkBlock(frozenBlock, "mine")) {
                                for (ItemStack drop : frozenBlock.getDrops(tool)) {
                                    player.getWorld().dropItemNaturally(frozenBlock.getLocation(), drop);
                                }
                                frozenBlock.setType(Material.PACKED_ICE, false);
                            }
                        }
                    }
                }
                break;
            case "jump_boost":
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, 2));
                break;
            case "speed_boost":
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 2));
                break;
            case "treasure_hunter":
                // InventoryCloseEvent in class GUIHandler
                block.setType(Material.TRAPPED_CHEST, false);
                Chest chest = (Chest) block.getState();
                Inventory chestInv = chest.getInventory();
                nbt.checkTreasureHunter(block, player);
                String[] loot = { "DIAMOND", "EMERALD", "GOLD_INGOT", "IRON_INGOT", "COAL" };
                for (int i = 0; i < new Random().nextInt(10) + 1; i++) {
                    chestInv.setItem(new Random().nextInt(27),
                            new ItemStack(Material.valueOf(loot[new Random().nextInt(4)])));
                }
                Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                    @Override
                    public void run() {
                        if (block.getType().equals(Material.TRAPPED_CHEST)) {
                            block.setType(Material.AIR, false);
                            nbt.lockBlock(block, false);
                        }
                    }
                }, 200);
                break;
            // rare enchants
            case "drill":
                playDrillSounds(player.getLocation(), player);
                for (int i = 0; i > -nbt.getEnchantmentLevel(tool, name); i--) {
                    Block b1 = player.getWorld().getBlockAt(block.getLocation()).getRelative(1, i, 0);
                    Block b2 = player.getWorld().getBlockAt(block.getLocation()).getRelative(-1, i, 0);
                    Block b3 = player.getWorld().getBlockAt(block.getLocation()).getRelative(0, i, 1);
                    Block b4 = player.getWorld().getBlockAt(block.getLocation()).getRelative(0, i, -1);
                    Block b5 = player.getWorld().getBlockAt(block.getLocation()).getRelative(0, i - 1, 0);
                    if (regions.checkBlock(b1, "mine")) {
                        for (ItemStack drop : b1.getDrops(tool)) {
                            if (!b1.getType().equals(Material.PACKED_ICE))
                                player.getWorld().dropItemNaturally(b1.getLocation(), drop);
                        }
                        b1.setType(Material.AIR, false);
                        block.getWorld().spawnParticle(Particle.REDSTONE,
                                b1.getLocation().getX() + 0.5, b1.getLocation().getY() + 0.5,
                                b1.getLocation().getZ() + 0.5,
                                20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
                    }
                    if (regions.checkBlock(b2, "mine")) {
                        for (ItemStack drop : b2.getDrops(tool)) {
                            if (!b2.getType().equals(Material.PACKED_ICE))
                                player.getWorld().dropItemNaturally(b2.getLocation(), drop);
                        }
                        b2.setType(Material.AIR, false);
                        block.getWorld().spawnParticle(Particle.REDSTONE,
                                b2.getLocation().getX() + 0.5, b2.getLocation().getY() + 0.5,
                                b2.getLocation().getZ() + 0.5,
                                20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
                    }
                    if (regions.checkBlock(b3, "mine")) {
                        for (ItemStack drop : b3.getDrops(tool)) {
                            if (!b3.getType().equals(Material.PACKED_ICE))
                                player.getWorld().dropItemNaturally(b3.getLocation(), drop);
                        }
                        b3.setType(Material.AIR, false);
                        block.getWorld().spawnParticle(Particle.REDSTONE,
                                b3.getLocation().getX() + 0.5, b3.getLocation().getY() + 0.5,
                                b3.getLocation().getZ() + 0.5,
                                20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
                    }
                    if (regions.checkBlock(b4, "mine")) {
                        for (ItemStack drop : b4.getDrops(tool)) {
                            if (!b4.getType().equals(Material.PACKED_ICE))
                                player.getWorld().dropItemNaturally(b4.getLocation(), drop);
                        }
                        b4.setType(Material.AIR, false);
                        block.getWorld().spawnParticle(Particle.REDSTONE,
                                b4.getLocation().getX() + 0.5, b4.getLocation().getY() + 0.5,
                                b4.getLocation().getZ() + 0.5,
                                20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
                    }
                    if (regions.checkBlock(b5, "mine")) {
                        for (ItemStack drop : b5.getDrops(tool)) {
                            if (!b5.getType().equals(Material.PACKED_ICE))
                                player.getWorld().dropItemNaturally(b5.getLocation(), drop);
                        }
                        b5.setType(Material.AIR, false);
                        block.getWorld().spawnParticle(Particle.REDSTONE,
                                b5.getLocation().getX() + 0.5, b5.getLocation().getY() + 0.5,
                                b5.getLocation().getZ() + 0.5,
                                20, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
                    }
                }
                break;
            case "midas_touch":
                double chance = Math.random();
                if (chance <= 0.1) {
                    player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_BLOCK));
                } else {
                    player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT));
                }
                break;
            case "night_vision":
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, 0));
                break;
            case "ore_extractor":
                double radius = (double) (nbt.getEnchantmentLevel(tool, name) + 1) / 2;
                List<Block> extractedBlocks = regions.getBlocksInRadius(block, "mine", radius, true);
                for (int i = 0; i < extractedBlocks.size() - 1; i++) {
                    for (ItemStack drop : extractedBlocks.get(i).getDrops(tool)) {
                        player.getWorld().dropItemNaturally(block.getLocation(), drop);
                    }
                }
                break;
            case "regenerate":
                for (ItemStack drop : block.getDrops(tool)) {
                    player.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
                break;
            case "spare_change":
                double win = (new Random().nextDouble(74.85) + 0.15) * nbt.getEnchantmentLevel(tool, name);

                player.sendMessage("§7You found §a$" + DecimalFormat.getCurrencyInstance().format(win).split(" ")[0]
                        + " §7in that block!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5F);
                break;
            // epic enchants
            case "haste":
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.FAST_DIGGING, nbt.getEnchantmentLevel(tool, name) * 40, 0));
                break;
            case "make_it_rain":
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location location = block.getLocation();
                        if (player.getWorld().getBlockAt(location.add(0, 1, 0)).getType() == Material.AIR)
                            location.add(0, 0.6, 0);
                        location = new Location(location.getWorld(), location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5);
                        List<Item> itemList = new ArrayList<>();
                        player.sendMessage("1");
                        // Loop 4*level amount of items
                        for (int i = 0; i < 4 * nbt.getEnchantmentLevel(tool, name); i++) {
                            double yaw = i * (90.0 / nbt.getEnchantmentLevel(tool, name));
                            org.bukkit.util.Vector vector = new org.bukkit.util.Vector(1, 1, 1).normalize()
                                    .multiply(new Vector(1, 1, 1));
                            player.sendMessage("2");
                            vector.setY(1);
                            vector.setX(Math.cos(Math.toRadians(yaw)));
                            vector.setZ(Math.sin(Math.toRadians(yaw)));
                            Location newLocation = location.add(vector);
                            // Drop item with specific properties
                            player.sendMessage("drop loop");
                            for (ItemStack drop : block.getDrops(tool)) {
                                Item droppedItem = player.getWorld().dropItemNaturally(newLocation, drop);
                                player.sendMessage("drop item");
                                droppedItem.setVelocity(new Vector(0, 0, 0));
                                droppedItem.setPickupDelay(Integer.MAX_VALUE);
                                droppedItem.setInvulnerable(true);
                                droppedItem.setSilent(true);
                                itemList.add(droppedItem);
                                // Apply levitation effect and push items
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        droppedItem.setVelocity(new Vector(0, 0.6, 0).add(vector.multiply(0.1)));
                                        player.sendMessage("velocity");
                                        player.getWorld().playEffect(newLocation, Effect.MOBSPAWNER_FLAMES, 1);
                                    }
                                }.runTaskLater(Main.instance, 1L);
                            }
                        }
                        if (!itemList.isEmpty()) {
                            itemList.remove(0);
                        }
                        // Wait 28 ticks and create explosions
                        Location finalLocation = location;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Item item : itemList) {
                                    Location itemLoc = item.getLocation();
                                    player.getWorld().createExplosion(itemLoc.getX(), itemLoc.getY(), itemLoc.getZ(), 0, false, false);
                                    player.sendMessage("fake explosion");
                                    Vector pushVector = itemLoc.toVector().subtract(finalLocation.toVector())
                                            .normalize().multiply(0.6);
                                    item.setVelocity(pushVector);
                                    player.sendMessage("push vector");
                                }
                                // Wait 1 tick and adjust item properties
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        for (Item item : itemList) {
                                            item.setInvulnerable(false);
                                            item.setPickupDelay(0);
                                            player.sendMessage("clear item properties");
                                        }
                                    }
                                }.runTaskLater(Main.instance, 1L);
                            }
                        }.runTaskLater(Main.instance, 28L);
                    }
                }.runTaskLater(Main.instance, 1L);
                break;
            case "repair":
                nbt.repairTool(tool, nbt.getEnchantmentLevel(tool, name) * 3);
                break;
            case "slots":
                String[] blocks = { "GOLD_BLOCK", "GOLD_BLOCK", "GOLD_BLOCK", "GOLD_BLOCK",
                        "COAL_BLOCK", "COAL_BLOCK", "COAL_BLOCK", "COAL_BLOCK",
                        "IRON_BLOCK", "IRON_BLOCK", "IRON_BLOCK", "IRON_BLOCK",
                        "REDSTONE_BLOCK", "REDSTONE_BLOCK", "REDSTONE_BLOCK", "REDSTONE_BLOCK",
                        "LAPIS_BLOCK", "LAPIS_BLOCK", "LAPIS_BLOCK", "LAPIS_BLOCK",
                        "EMERALD_BLOCK", "DIAMOND_BLOCK" };
                this.index = 10;
                taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
                    @Override
                    public void run() {
                        if (index == 0) {
                            nbt.lockBlock(block, false);
                            stopCountdown();
                        } else {
                            nbt.lockBlock(block, true);
                            player.playSound(block.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
                            block.setType(Material.valueOf(blocks[new Random().nextInt(blocks.length)]), false);
                            index--;
                        }
                    }
                }, 0L, 4L);
                break;
            case "smelting":
                player.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR, false);
                for (ItemStack drop : block.getDrops()) {
                    if (drop.getType().toString().contains("LOG")) {
                        player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.COAL));
                    } else if (drop.getType().equals(Material.GOLD_ORE)) {
                        player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.GOLD_INGOT));
                    } else if (drop.getType().equals(Material.IRON_ORE)) {
                        player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.IRON_INGOT));
                    } else if (drop.getType().equals(Material.COBBLESTONE)) {
                        player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.STONE));
                    } else
                        player.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
                break;
            case "stop_that":
                Villager v = (Villager) player.getWorld().spawn(block.getLocation(), Villager.class);
                v.setCustomName("§d§lMagoo");
                player.sendMessage("§c§lHey, Stop That!");
                block.getWorld().spawnParticle(Particle.REDSTONE,
                        block.getLocation().getX() + 0.3, block.getLocation().getY() + 0.7,
                        block.getLocation().getZ() + 0.3,
                        100, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(64, 64, 64), 2), true);
                Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                    @Override
                    public void run() {
                        if (!v.isDead()) {
                            player.playSound(v.getLocation(), Sound.ENTITY_VILLAGER_DEATH, 0.1F, 1);
                            v.teleport(v.getLocation().add(0, -300, 0));
                        }
                    }
                }, 200);
                break;
            // legendary enchants
            case "explosive":
                radius = (double) (nbt.getEnchantmentLevel(tool, name) + 1) / 2;
                if (radius > 5)
                    radius = 5;
                if (radius < 3) {
                    player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, block.getLocation(), 1);
                    player.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1F, 1);
                } else {
                    player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, block.getLocation(), 1);
                    player.playSound(block.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1F, 1);
                }
                List<Block> explodedBlocks = regions.getBlocksInRadius(block, "mine", radius, false);
                for (Block checkedBlock : explodedBlocks) {
                    for (ItemStack drop : checkedBlock.getDrops(tool)) {
                        if (!checkedBlock.getType().equals(Material.PACKED_ICE))
                            player.getWorld().dropItemNaturally(checkedBlock.getLocation(), drop);
                    }
                    checkedBlock.setType(Material.AIR, false);
                }
                break;
            case "lucky":
                break;
            case "super_breaker":
                int duration = nbt.getEnchantmentLevel(tool, name) * 20;
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, 99));
                player.sendMessage(
                        api.getColor(api.getRarity(name)) + api.getDisplayName(name) + "§a has been activated!");
                Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(api.getColor(api.getRarity(name)) + api.getDisplayName(name)
                                + "§c has been deactivated.");
                    }
                }, duration);
                break;
            case "vein_miner":
                radius = nbt.getEnchantmentLevel(tool, name);
                if (radius > 10)
                    radius = 10;
                player.playSound(block.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.1F, 1);
                List<Block> veinMinedBlocks = regions.getBlocksInRadius(block, "mine", radius, true);
                for (Block checkedBlock : veinMinedBlocks) {
                    for (ItemStack drop : checkedBlock.getDrops(tool)) {
                        if (!checkedBlock.getType().equals(Material.PACKED_ICE))
                            player.getWorld().dropItemNaturally(checkedBlock.getLocation(), drop);
                    }
                    checkedBlock.setType(Material.AIR, false);
                    block.getWorld().spawnParticle(Particle.REDSTONE,
                            checkedBlock.getLocation().getX() + 0.5, checkedBlock.getLocation().getY() + 0.5,
                            checkedBlock.getLocation().getZ() + 0.5,
                            3, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, 0, 255), 2), true);
                }
                break;
        }
    }

    public void activateArmorEnchant(Player player, Player target, ItemStack armor, String name) {
        switch (name) {
            // common enchants

            // rare enchants

            // epic enchants

            // legendary enchants

        }
    }

    private void playDrillSounds(Location location, Player player) {
        this.index = 3;
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
            @Override
            public void run() {
                if (index == 0) {
                    stopCountdown();
                } else {
                    player.playSound(location, Sound.ITEM_SHIELD_BREAK, 1, 0.1F);
                    index--;
                }
            }
        }, 0L, 4L);
    }

    @EventHandler
    public void onVillagerKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager && event.getEntity().getKiller() != null) {
            double chance = Math.random();
            if (chance <= 0.05) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                        new ItemStack(Material.DIAMOND));
            } else if (chance <= 0.1) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                        new ItemStack(Material.EMERALD));
            } else if (chance <= 0.45) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                        new ItemStack(Material.IRON_INGOT));
            } else if (chance <= 0.8) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                        new ItemStack(Material.GOLD_INGOT));
            } else {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(),
                        new ItemStack(Material.COAL));
            }
        }
    }

    @EventHandler
    public void onTreasureChestBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (regions.checkBlock(block, "mine") && nbt.checkLockedBlock(block)) {
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
                @Override
                public void run() {
                    if (nbt.checkLockedBlock(block)) {
                        if (!block.getType().equals(Material.TRAPPED_CHEST))
                            event.getPlayer().sendMessage("§aThe block you tried to break can be broken now.");
                        nbt.lockBlock(block, false);
                    }
                }
            }, 200);
        }
    }

    public void stopCountdown() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public boolean checkEnchantUpgradability(Player player, ItemStack item, String enchant) { // true = 1 or more levels
        NBTItem nbti = new NBTItem(item);
        if (nbt.getEnchants(item).size() < LevelEnchants.getPlayerMaxEnchants(player).maxEnchants) {
            if (nbti.hasTag(enchant))
                return api.getLevelCap(enchant) > nbti.getInteger(enchant);
            return true;
        } else {
            if (nbti.hasTag(enchant))
                return api.getLevelCap(enchant) > nbti.getInteger(enchant);
            return false;
        }
    }

    public List<String> getPossibleEnchants(Player player, ItemStack item, String rarity) {
        List<String> possibleEnchants = api.getEnchantments(rarity);
        List<String> typeSpecificEnchants = api.getEnchantments(nbt.getCategory(item));
        possibleEnchants.retainAll(typeSpecificEnchants);
        List<String> itemEnchants = nbt.getEnchants(item);
        itemEnchants.removeIf(enchant -> !api.getRarity(enchant).equals(rarity));
        if (nbt.getEnchants(item).size() < LevelEnchants.getPlayerMaxEnchants(player).maxEnchants) { // all enchants - max. level
            // enchants
            if (!itemEnchants.isEmpty()) {
                for (String enchant : itemEnchants)
                    if (!checkEnchantUpgradability(player, item, enchant))
                        possibleEnchants.remove(enchant);
            }
            return possibleEnchants;
        } else { // already existing enchants - max. level enchants
            if (!itemEnchants.isEmpty()) {
                int initialSize = possibleEnchants.size();
                for (String enchant : itemEnchants)
                    if (!checkEnchantUpgradability(player, item, enchant))
                        possibleEnchants.remove(enchant);
                if (possibleEnchants.size() + itemEnchants.size() == initialSize)
                    return null;
                return possibleEnchants;
            }
            return null;
        }
    }
}
