package de.exo.jbenchants.enchants.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ReinforcedEnchant implements Listener {

    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("d1337b45-5e07-4b89-b48f-7fbbc54e1ec4");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            checkReinforcedArmor(event.getPlayer());
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && (event.getSlotType() == InventoryType.SlotType.ARMOR
                || event.getSlot() == 40 || event.getSlot() == event.getWhoClicked().getInventory().getHeldItemSlot())) {
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> checkReinforcedArmor((Player) event.getWhoClicked()), 1);
        }
    }

    @EventHandler
    public void onHotbarSwitch(PlayerItemHeldEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            Player player = event.getPlayer();
            ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
            try {
                if (new NBTItem(oldItem).hasTag("reinforced")) checkReinforcedArmor(player);
            } catch (NullPointerException ignored) {}
            try {
                if (new NBTItem(newItem).hasTag("reinforced")) checkReinforcedArmor(player);
            } catch (NullPointerException ignored) {}
        }, 1);
    }

    @EventHandler
    public void onOffHandSwitch(PlayerSwapHandItemsEvent event) {
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            try {
                if (event.getOffHandItem() != null && new NBTItem(event.getOffHandItem()).hasTag("reinforced")) checkReinforcedArmor(event.getPlayer());
            } catch (NullPointerException ignored) {}
            try {
                if (event.getMainHandItem() != null && new NBTItem(event.getMainHandItem()).hasTag("reinforced")) checkReinforcedArmor(event.getPlayer());
            } catch (NullPointerException ignored) {}
        }, 1);
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorChangeEvent event) {
        try {
            if (new NBTItem(event.getNewItem()).hasTag("reinforced")) checkReinforcedArmor(event.getPlayer());
        } catch (NullPointerException ignored) {}
        try {
            if (new NBTItem(event.getOldItem()).hasTag("reinforced")) checkReinforcedArmor(event.getPlayer());
        } catch (NullPointerException ignored) {}
    }

    private void checkReinforcedArmor(Player player) {
        double extraHearts = 0.0;
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                NBTItem item = new NBTItem(armor);
                if (item.hasTag("reinforced")) extraHearts += item.getInteger("reinforced") * 4;
            }
        }
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && offHandItem.getType() != Material.AIR) {
            NBTItem item = new NBTItem(offHandItem);
            if (item.hasTag("reinforced")) extraHearts += item.getInteger("reinforced") * 4;
        }
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (mainHandItem != null && mainHandItem.getType() != Material.AIR) {
            NBTItem item = new NBTItem(mainHandItem);
            if (item.hasTag("reinforced")) extraHearts += item.getInteger("reinforced") * 4;
        }

        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null) return;
        healthAttribute.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(HEALTH_MODIFIER_UUID))
                .forEach(healthAttribute::removeModifier);


        if (extraHearts > 0) {
            AttributeModifier modifier = new AttributeModifier(HEALTH_MODIFIER_UUID, "Extra Hearts", extraHearts, AttributeModifier.Operation.ADD_NUMBER);
            healthAttribute.addModifier(modifier);
        }
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
    }

}
