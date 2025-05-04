package de.exo.jbenchants.events;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.exo.jbenchants.enchants.EnchantsHandler;
import de.exo.jbenchants.enchants.EnchantsItems;
import de.exo.jbenchants.enchants.EnchantsMeta;
import de.exo.jbenchants.enchants.EnchantsNBT;
import de.exo.jbenchants.items.crystal.Crystal;
import org.bukkit.event.Listener;

public class ItemMerger implements Listener {
    private static ItemMerger INSTANCE;

    public static ItemMerger getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ItemMerger();
        return INSTANCE;
    }

    API api = Main.getAPI();
    EnchantsMeta lore = EnchantsMeta.getInstance();
    EnchantsItems items = EnchantsItems.getInstance();
    EnchantsNBT nbt = EnchantsNBT.getInstance();
    EnchantsHandler handler = EnchantsHandler.getInstance();
    GUIHandler guiHandler = GUIHandler.getInstance();
    Crystal crystal = Crystal.getInstance();
}
