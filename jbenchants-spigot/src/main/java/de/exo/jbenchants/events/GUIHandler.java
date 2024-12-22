package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsItems;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.enchants.EnchantsRegions;
import de.exo.jbenchants.items.crystal.Crystal;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHandler implements Listener {

    private static GUIHandler INSTANCE;

    private GUIHandler() {
    }

    public static GUIHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new GUIHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsItems items = EnchantsItems.getInstance();
    EnchantsMeta lore = EnchantsMeta.getInstance();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsRegions regions = EnchantsRegions.getInstance();
    Crystal crystal = Crystal.getInstance();

//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        ItemStack item = event.getCurrentItem();
//        Player player = (Player) event.getWhoClicked();
//        if (item != null) {
//            switch (event.getView().getOriginalTitle()) {
//                case "§8Enchants":
//                    event.setCancelled(true);
//                    switch (item.getType()) {
//                        case WHITE_DYE -> {
//                            ItemMeta meta = item.getItemMeta();
//                            meta.displayName(Component.text("§fCommon Enchants"));
//                            item.setItemMeta(meta);
//                            player.openInventory(getEnchantsInventory("Common"));
//                        }
//                        case ORANGE_DYE -> {
//                            ItemMeta meta = item.getItemMeta();
//                            meta.displayName(Component.text("§6Rare Enchants"));
//                            item.setItemMeta(meta);
//                            player.openInventory(getEnchantsInventory("Rare"));
//                        }
//                        case LIGHT_BLUE_DYE -> {
//                            ItemMeta meta = item.getItemMeta();
//                            meta.displayName(Component.text("§bEpic Enchants"));
//                            item.setItemMeta(meta);
//                            player.openInventory(getEnchantsInventory("Epic"));
//                        }
//                        case PURPLE_DYE -> {
//                            ItemMeta meta = item.getItemMeta();
//                            meta.displayName(Component.text("§5Legendary Enchants"));
//                            item.setItemMeta(meta);
//                            player.openInventory(getEnchantsInventory("Legendary"));
//                        }
//                        case IRON_PICKAXE -> player.openInventory(getEnchantsInventory("Tool"));
//                        case IRON_CHESTPLATE -> player.openInventory(getEnchantsInventory("Armor"));
//                        case IRON_SWORD -> player.openInventory(getEnchantsInventory("Weapon"));
//                    }
//                    break;
//                case "§8Common Enchants", "§8Rare Enchants", "§8Epic Enchants", "§8Legendary Enchants",
//                        "§8Tool Enchants", "§8Armor Enchants", "§8Weapon Enchants":
//                    event.setCancelled(true);
//                    if (event.getCurrentItem().getType().equals(Material.RED_BED)) {
//                        player.openInventory(getEnchantsInventory());
//                    }
//                    break;
//                case "§8Decrypting...":
//                    event.setCancelled(true);
//                    break;
//                case "§8Cleanser §7§o(Click to remove)":
//                    event.setCancelled(true);
//                    if (event.getClickedInventory() == player.getInventory())
//                        return;
//                    ItemStack enchItem = event.getClickedInventory().getItem(17);
//                    List<Component> loreComponents = enchItem.lore();
//                    if (loreComponents != null && event.getSlot() < loreComponents.size()) {
//                        String enchantName = loreComponents.get(event.getSlot()).toString().substring(2);
//                        for (int i = 0; i < player.getInventory().getSize(); i++) {
//                            ItemStack count = player.getInventory().getItem(i);
//                            if (count != null && count.equals(enchItem)) {
//                                nbt.removeEnchantment(enchItem, enchantName);
//                                lore.updateLore(enchItem);
//                                nbt.setUsedItemChance(player, "cleanser", -1);
//                                player.getInventory().setItem(i, enchItem);
//                                player.getOpenInventory().close();
//                                return;
//                            }
//                        }
//                        player.getOpenInventory().close();
//                        player.sendMessage("§cSomething went wrong, please report this incident to staff!");
//                    }
//                    break;
//                case "§8Crystals":
//                    event.setCancelled(true);
//                    if (event.getClickedInventory() == player.getInventory()
//                            || event.getCurrentItem().getType() != Material.NETHER_STAR)
//                        return;
//                    String rarity = event.getCurrentItem().getItemMeta().displayName().toString().split(" ")[0]
//                            .substring(2).toLowerCase();
//                    player = (Player) event.getWhoClicked();
//                    if (nbt.getPlayerCrystals(player, rarity) > 0) {
//                        if (player.getInventory().firstEmpty() != -1) {
//                            nbt.addPlayerCrystals(player, rarity, -1);
//                            player.getInventory().addItem(crystal.getCrystal(rarity));
//                            player.openInventory(new CrystalsCommand().getCrystalsInventory(player));
//                        } else
//                            player.sendMessage("§cYou don't have enough space in your inventory.");
//                    }
//            }
//        }
//    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        switch (event.getView().getOriginalTitle()) {
//            case "§8Cleanser §7§o(Click to remove)":
//                if (nbt.getUsedItemChance(player, "cleanser") != "-1") {
//                    player.getInventory()
//                            .addItem(items.getCleanser(Integer.parseInt(nbt.getUsedItemChance(player, "cleanser"))));
//                    nbt.setUsedItemChance(player, "cleanser", -1);
//                }
//                break;
            case "§8Treasure Hunter":
                InventoryHolder holder = event.getInventory().getHolder();
                if (holder instanceof Chest && regions.checkBlock(((Chest) holder).getBlock(), "mine")) {
                    ((Chest) holder).getBlock().setType(Material.AIR);
                }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest && regions.checkBlock(((Chest) holder).getBlock(), "mine")
                && !nbt.checkTreasureHunter(((Chest) holder).getBlock(), player)) {
            event.setCancelled(true);
        } else if (holder instanceof Chest && regions.checkBlock(((Chest) holder).getBlock(), "mine")) {
            Inventory chestInventory = event.getInventory();
            player.getWorld().getBlockAt(event.getInventory().getLocation()).setType(Material.AIR);
            event.setCancelled(true);
            player.openInventory(chestInventory);
        }
    }
}
