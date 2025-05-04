package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantsNBT {
    private static EnchantsNBT INSTANCE;

    public static EnchantsNBT getInstance() {
        if (INSTANCE == null)
            INSTANCE = new EnchantsNBT();
        return INSTANCE;
    }

    API api = Main.getAPI();

    public void addEnchantmentLevel(ItemStack item, String name, int level) {
        NBTItem nbti = new NBTItem(item);
        if (getEnchantmentLevel(item, name) + level <= 0) {
            removeEnchantment(item, name);
        } else if (!nbti.hasTag(name)) {
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants") + 1);
            nbti.setInteger(name, level);
            nbti.applyNBT(item);
            setVanillaEnchant(item, name, level);
            setEnchantGlow(item, true);
        } else {
            setEnchantmentLevel(item, name, nbti.getInteger(name) + level);
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
            nbti.setInteger("jbenchants", nbti.getInteger("jbenchants") - 1);
            nbti.removeKey(name);
            nbti.applyNBT(item);
            removeVanillaEnchant(item, name);
            setEnchantGlow(item, false);
        }
    }

    public int getEnchantmentLevel(ItemStack item, String name) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.hasTag(name))
            return nbti.getInteger(name);
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
                if (i == nbti.getInteger("jbenchants"))
                    break;
            }
        }
        return enchants;
    }

    public String getCategory(ItemStack item) {
        String material = item.getType().toString();
        if (material.endsWith("_PICKAXE") || material.endsWith("_SHOVEL"))
            return "tool";
        if (material.endsWith("_AXE"))
            return "axe";
        if (material.endsWith("_SWORD"))
            return "weapon";
        if (material.endsWith("_HELMET"))
            return "helmet";
        if (material.endsWith("_CHESTPLATE"))
            return "chestplate";
        if (material.endsWith("_LEGGINGS"))
            return "leggings";
        if (material.endsWith("_BOOTS"))
            return "boots";
        if (material.equals("FISHING_ROD"))
            return "fishing";
        if (material.equals("BOW"))
            return "bow";
        return null;
    }

    private boolean checkVanillaEnchant(String name) {
        if (Registry.ENCHANTMENT.get(NamespacedKey.minecraft(name)) != null)
            return true;
        return false;
    }

    public void setEnchantGlow(ItemStack item, boolean applyGlow) {
        ItemMeta meta = item.getItemMeta();
        if (applyGlow && !meta.hasEnchant(Enchantment.LURE)) {
            meta.addEnchant(Enchantment.LURE, 0, true);
            meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        } else if (!applyGlow && meta.hasEnchant(Enchantment.LURE)) {
            meta.removeEnchant(Enchantment.LURE);
            meta.removeItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        }
        item.setItemMeta(meta);
    }

    public void setVanillaEnchant(ItemStack item, String enchant, int level) {
        if (!checkVanillaEnchant(enchant))
            return;
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Objects.requireNonNull(Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchant))), level, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        nbti.setBoolean("ench", false);
        nbti.applyNBT(item);
        item.setItemMeta(meta);
    }

    public void removeVanillaEnchant(ItemStack item, String enchant) {
        if (!checkVanillaEnchant(enchant))
            return;
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

    public boolean updateEnchantedItem(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = (meta != null) ? meta.lore() : null;
        if (!nbti.hasTag("jbenchants") && lore != null && item.getType() != Material.NETHER_STAR && item.getType() != Material.SUGAR && item.getType() != Material.GUNPOWDER && item.getType() != Material.PAPER) {
            List<Integer> enchantLoreSlots = EnchantsMeta.getInstance().getEnchantmentLoreSlots(item);
            if (enchantLoreSlots == null)
                return false;
            boolean prefix = (((lore.get(0)).toString().split(" ")).length == 3);
            for (Integer enchantLoreSlot : enchantLoreSlots) {
                String[] loreLine = (lore.get(enchantLoreSlot)).toString().split(" ");
                if (prefix) {
                    if (api.getName(loreLine[1]) != null)
                        setEnchantmentLevel(item, api.getName(loreLine[1]), Integer.parseInt(loreLine[2]));
                    continue;
                }
                if (api.getName(loreLine[0]) != null)
                    setEnchantmentLevel(item, api.getName(loreLine[0]), Integer.parseInt(loreLine[1]));
            }
            nbti.setString("jbenchants", "true");
            nbti.applyNBT(item);
            return true;
        }
        return false;
    }

    public boolean checkTreasureHunter(Block block, Player player) {
        NBTBlock nbtb = new NBTBlock(block);
        if (nbtb.getData().getString("player").isEmpty()) {
            nbtb.getData().setString("player", player.getName());
            lockBlock(block, true);
            return false;
        }
        return Objects.equals(nbtb.getData().getString("player"), player.getName());
    }

    public void lockBlock(Block block, boolean locked) {
        NBTBlock nbtb = new NBTBlock(block);
        if (locked) {
            if (checkLockedBlock(block))
                return;
            nbtb.getData().setBoolean("locked", true);
        } else if (checkLockedBlock(block)) {
            nbtb.getData().removeKey("locked");
        }
    }

    public boolean checkLockedBlock(Block block) {
        NBTBlock nbtb = new NBTBlock(block);
        if (nbtb.getData().hasTag("locked"))
            return nbtb.getData().getBoolean("locked");
        return false;
    }

    public void repairTool(ItemStack tool, int repairValue) {
        NBTItem nbti = new NBTItem(tool);
        if (!nbti.hasTag("Damage"))
            return;
        int newDamage = nbti.getInteger("Damage") - repairValue;
        if (newDamage <= 0) {
            nbti.removeKey("Damage");
        } else {
            nbti.setInteger("Damage", newDamage);
        }
        nbti.applyNBT(tool);
    }
}
