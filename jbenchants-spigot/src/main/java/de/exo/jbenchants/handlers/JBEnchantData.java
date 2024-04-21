package de.exo.jbenchants.handlers;

import de.tr7zw.nbtapi.wrapper.NBTProxy;
import de.tr7zw.nbtapi.wrapper.ProxyList;
import org.bukkit.inventory.ItemStack;

public interface JBEnchantData extends NBTProxy {

    public ProxyList<JBEnchant> getJBEnchants(ItemStack item);

    public interface JBEnchants extends NBTProxy {
        public void setName(String name);
        public String getName();
        public void addLevel(int level);
        public void removeLevel(int level);
        public void setLevel(int level);
        public int getLevel();
        public String toString();
    }
}
