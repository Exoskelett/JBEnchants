package de.exo.jbenchants.enchants;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArmorEnchantsHandler {
    private static ArmorEnchantsHandler INSTANCE;

    public static ArmorEnchantsHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ArmorEnchantsHandler();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsMeta meta = EnchantsMeta.getInstance();
    EnchantsHandler enchantsHandler = EnchantsHandler.getInstance();

    public void proccArmorEnchant(Player player, Player attacker, ItemStack armor, List<String> list) {
        List<String> notify = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean[] enchantAttributes = api.check(list.get(i), "active", "proccable", "notify");
            if (enchantAttributes[0] && enchantAttributes[1]) {
                if (enchantAttributes[2])
                    notify.add(list.get(i));
                activateArmorEnchant(player, attacker, armor, list.get(i));
            }
        }
        notify = meta.sortEnchants(notify);
        enchantsHandler.notify(player, notify);
    }

    public void activateArmorEnchant(Player player, Player attacker, ItemStack armor, String name) {
        Objects.requireNonNull(name);
    }
}
