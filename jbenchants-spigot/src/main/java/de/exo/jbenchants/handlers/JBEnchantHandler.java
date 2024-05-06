package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JBEnchantHandler implements JBEnchantData.Handler {

    API api = Main.instance.api;
    JBEnchantLore lore = Main.instance.lore;

    public void procc(Player player, ItemStack tool, List<String> list, Block block) {
        List<String> notify = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (api.check(list.get(i), "active")) {
                if (api.check(list.get(i), "proccable")) {
                    activate(player, tool, list.get(i), block);
                    notify.add(list.get(i));
                }
            }
        }
        notify = lore.sortEnchants(notify);
        notify(player, notify);
    }
    public void notify(Player player, List<String> list) {
        StringBuilder s = new StringBuilder();
        for (int k = 0; k < list.size(); k++) {
            if (k < list.size()-1) {
                s.append(list.get(k)).append("§7, ");
            } else {
                s.append(list.get(k));
            }
        }
        player.sendActionBar(s+" §7procced.");
    }
    public void activate(Player player, ItemStack tool, String name, Block block) {
        switch (name) {
                // common enchants
            case "alchemy":
                break;
                // rare enchants
            case "drill":
                break;
                // epic enchants
            case "haste":
                break;
                // legendary enchants
            case "efficiency":
                break;
        }
    }

    // Items

    @Override
    public boolean updateCrystal(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        List<String> lore = item.getLore();
        if (!nbti.hasTag("crystal") && lore != null) {
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
        if (!nbti.hasTag("dust") && lore != null) {
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
        if (!nbti.hasTag("cleanser") && lore != null) {
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
        if (!nbti.hasTag("scroll") && lore != null) {
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
}
