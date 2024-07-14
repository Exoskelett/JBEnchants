package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JBEnchantNBT implements JBEnchantData.NBT {

    API api = Main.instance.api;

    public void addEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (getEnchantmentLevel(item, name)+level <= 0) {
            removeEnchantment(item, name);
        } else if (!nbti.hasTag(name)) {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants")+1);
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
            setVanillaEnchant(item, name, level);
            setEnchantGlow(item, true);
        } else  {
            setEnchantmentLevel(item, name, nbti.getInteger(name)+level);
        }
    }
    public void setEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (level <= 0) {
            removeEnchantment(item, name);
        } else if (nbti.hasTag(name)) {
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
            setVanillaEnchant(item, name, level);
            setEnchantGlow(item, true);
        } else {
            addEnchantmentLevel(item, name, level);
        }
    }
    public void removeEnchantment(ItemStack item, String name) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name)) {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants")-1);
            nbti.removeKey(name);
            nbti.applyNBT(item);
            removeVanillaEnchant(item, name);
            setEnchantGlow(item, false);
        }
    }
    public int getEnchantmentLevel(ItemStack item, String name) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name)) {
            return nbti.getInteger(name);
        }
        return 0;
    }
    public List<String> getEnchants(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> enchants = new ArrayList<>();
        if (nbti.hasTag("jbenchants")) {
            int i = 0;
            for (int k = 0; k < api.getEnchantments().size(); k++) {
                if (nbti.hasTag(api.getEnchantments().get(k))) {
                    enchants.add(api.getEnchantments().get(k));
                    i++;
                }
                if (i == nbti.getInteger("jbenchants")) break;
            }
            return enchants;
        } else
            return null;
    }
    public boolean checkLevelCap(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        int cap = api.getLevelCap(name);
        if (nbti.hasTag(name)) {
            return cap - nbti.getInteger(name) >= level;
        } else return level <= cap;
    }
    public String getCategory(ItemStack item) {
        String material = item.getType().toString();
        if (material.endsWith("_PICKAXE") || material.endsWith("_SHOVEL")) {
            return "tool";
        } else if (material.endsWith("_AXE")) {
            return "axe";
        }else if (material.endsWith("_SWORD")) {
            return "weapon";
        } else if (material.endsWith("_HELMET")) {
            return "helmet";
        } else if (material.endsWith("_CHESTPLATE")) {
            return "chestplate";
        } else if (material.endsWith("_LEGGINGS")) {
            return "leggings";
        } else if (material.endsWith("_BOOTS")) {
            return "boots";
        }else if (material.equals("FISHING_ROD")) {
            return "fishing";
        } else if (material.equals("BOW")) {
            return "bow";
        } else
            return null;
    }

    public void setUsedItemChance(Player player, String item_used, int chance) {
        NBTEntity entity = new NBTEntity(player);
        if (chance > 0) {
            entity.getPersistentDataContainer().setInteger("used_"+item_used.toLowerCase(), chance);
        } else
            entity.getPersistentDataContainer().removeKey("used_"+item_used.toLowerCase());
    }

    public int getUsedItemChance(Player player, String item_used) {
        NBTEntity entity = new NBTEntity(player);
        if (entity.getPersistentDataContainer().hasTag("used_"+item_used.toLowerCase())) {
            return entity.getPersistentDataContainer().getInteger("used_"+item_used.toLowerCase());
        }
        return -1;
    }

    public void addPlayerCrystals(Player player, String rarity, int amount) {
        NBTEntity entity = new NBTEntity(player);
        entity.getPersistentDataContainer().setInteger(rarity, entity.getPersistentDataContainer().getInteger(rarity)+amount);
    }

    public int getPlayerCrystals(Player player, String rarity) {
        NBTEntity entity = new NBTEntity(player);
        return entity.getPersistentDataContainer().getInteger(rarity);
    }

    // Handling vanilla enchantments

    private boolean checkVanillaEnchant(String name) {
        if (Registry.ENCHANTMENT.get(NamespacedKey.minecraft(name)) != null) {
            return true;
        } else
            return false;
    }

    @Override
    public void setEnchantGlow(ItemStack item, boolean applyGlow) {
        ItemMeta meta = item.getItemMeta();
        if (applyGlow && !meta.hasEnchant(Enchantment.LURE)) {
            meta.addEnchant(Enchantment.LURE, 0, true);
        } else if (!applyGlow && meta.hasEnchant(Enchantment.LURE)) {
            meta.removeEnchant(Enchantment.LURE);
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    @Override
    public void setVanillaEnchant(ItemStack item, String enchant, int level) {
        if (!checkVanillaEnchant(enchant)) return;
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Objects.requireNonNull(Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchant))), level, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        nbti.setBoolean("ench", false);
        nbti.applyNBT(item);
        item.setItemMeta(meta);
    }

    @Override
    public void removeVanillaEnchant(ItemStack item, String enchant) {
        if (!checkVanillaEnchant(enchant)) return;
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        meta.removeEnchant(Objects.requireNonNull(Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchant))));
        if (meta.getEnchants().isEmpty()) {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            nbti.removeKey("ench");
            nbti.applyNBT(item);
        }
        item.setItemMeta(meta);
    }

    // Update Items

    @Override
    public boolean updateEnchantedItem(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> lore = item.getLore();
        if (!nbti.hasTag("jbenchants") && lore != null && item.getType() != Material.NETHER_STAR && item.getType() != Material.SUGAR && item.getType() != Material.GUNPOWDER && item.getType() != Material.PAPER) {
            List<Integer> enchantLoreSlots = new JBEnchantLore().getEnchantmentLoreSlots(item);
            if (enchantLoreSlots == null) return false;
            boolean prefix = (lore.get(0).split(" ").length == 3);
            for (Integer enchantLoreSlot : enchantLoreSlots) {  // add enchants
                String[] loreLine = lore.get(enchantLoreSlot).split(" ");
                if (prefix) {
                    if (api.getName(loreLine[1]) != null)
                        setEnchantmentLevel(item, api.getName(loreLine[1]), Integer.parseInt(loreLine[2]));
                } else {
                    if (api.getName(loreLine[0]) != null)
                        setEnchantmentLevel(item, api.getName(loreLine[0]), Integer.parseInt(loreLine[1]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCrystal(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> lore = item.getLore();
        if (!nbti.hasTag("crystal") && lore != null && item.getType().equals(Material.NETHER_STAR)) {
            if (Objects.equals(lore.get(3), api.getItemLore("crystal")[3])) {
                String rarity = lore.get(0).split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("crystal", rarity);
                String chance = lore.get(1).split(" ")[2].substring(2).replace("%", "");
                nbti.setString("chance", chance);
                nbti.applyNBT(item);
                return true;
            } else if (Objects.equals(lore.get(2), api.getItemLore("mysteryCrystal")[2])) {
                String rarity = lore.get(0).split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("crystal", rarity);
                String chance = lore.get(6).split(" ")[1].substring(2).replace("%", "");
                switch (chance.split("-").length) {
                    case 1:
                        nbti.setString("chance", "0-100");
                        break;
                    case 2:
                        nbti.setString("chance", chance);
                        break;
                }
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateDust(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> lore = item.getLore();
        if (!nbti.hasTag("dust") && lore.size() > 2) {
            if (Objects.equals(lore.get(2), api.getItemLore("dust")[2])) {
                String rarity = lore.get(0).split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("dust", rarity);
                String chance = lore.get(1).split(" ")[3].substring(2).replace("%", "");
                nbti.setString("chance", chance);
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateCleanser(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> lore = item.getLore();
        if (!nbti.hasTag("cleanser") && lore.size() > 2) {
            if (Objects.equals(lore.get(2), api.getItemLore("cleanser")[2])) {
                int chance = Integer.parseInt(lore.get(0).split(" ")[2].substring(2).replace("%", ""));
                nbti.setInteger("chance", chance);
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateScroll(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> lore = item.getLore();
        if (!nbti.hasTag("scroll") && lore.size() > 3) {
            if (Objects.equals(lore.get(3), api.getItemLore("scroll")[3])) {
                String rarity = lore.get(0).split(" ")[1].substring(2).substring(0, 1).toUpperCase();
                nbti.setString("scroll", rarity);
                String chance = lore.get(1).split(" ")[2].substring(2).replace("%", "");
                nbti.setString("chance", chance);
                nbti.applyNBT(item);
                return true;
            }
        }
        return false;
    }

    // Enchantment Effects

    public boolean checkTreasureHunter(Block block, Player player) {
        // If block does not have NBT set, set to player.
        // Return "true" if block belongs to player, false in any other case.
        NBTBlock nbtb = new NBTBlock(block);
        if (nbtb.getData().getString("player").isEmpty()) {
            nbtb.getData().setString("player", player.getName());
            lockBlock(block, true);
            return false;
        } else {
            return Objects.equals(nbtb.getData().getString("player"), player.getName());
        }
    }

    public void lockBlock(Block block, boolean locked) {
        // locks/unlocks a block for Players to break
        NBTBlock nbtb = new NBTBlock(block);
        if (locked) {
            if (checkLockedBlock(block)) return;
            nbtb.getData().setBoolean("locked", true);
        } else {
            if (checkLockedBlock(block)) nbtb.getData().removeKey("locked");
        }
    }

    public boolean checkLockedBlock(Block block) {
        // checks whether a block is locked
        NBTBlock nbtb = new NBTBlock(block);
        if (nbtb.getData().hasTag("locked")) {
            return nbtb.getData().getBoolean("locked");
        } else
            return false;
    }

    public void repairTool(ItemStack tool, int repairValue) {
        NBTItem nbti = new NBTItem(tool);
        if (!nbti.hasTag("Damage")) return;
        int newDamage = nbti.getInteger("Damage")-repairValue;
        if (newDamage <= 0) {
            nbti.removeKey("Damage");
        } else
            nbti.setInteger("Damage", newDamage);
        nbti.applyNBT(tool);
    }
}
