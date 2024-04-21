package de.exo.jbenchants.handlers;

import de.exo.jbenchants.API;
import de.exo.jbenchants.Main;
import de.tr7zw.nbtapi.wrapper.ProxyList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class JBEnchant implements JBEnchantData, JBEnchantData.JBEnchants {

    @Override
    public ProxyList<JBEnchant> getJBEnchants(ItemStack item) {
        ProxyList<JBEnchant> enchants = new ProxyList<JBEnchant>() {
            private JBEnchant[] enchants = new JBEnchant[api.getEnchantments().size()];
            private int size = 0;

            @Override
            public JBEnchant addCompound() {
                enchants[size] = new JBEnchant("TBA", -1);
                size++;
                return enchants[size];
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isEmpty() {
                if (size == 0) return true;
                return false;
            }

            @Override
            public JBEnchant get(int i) {
                if (i <= size)
                    return enchants[i];
                return null;
            }

            @Override
            public void remove(int i) {
                if (i < 0 || i >= enchants.length)
                    throw new RuntimeException("Invalid list position!");
                if (i <= size) {
                    for (int index = i; index < size; index++)
                        enchants[index] = enchants[index + 1];
                    enchants[size] = null;
                    size--;
                }
            }

            @NotNull
            @Override
            public Iterator<JBEnchant> iterator() {
                Iterator<JBEnchant> iterator = new Iterator<JBEnchant>() {
                    private int index = 0;
                    @Override
                    public boolean hasNext() {
                        if (enchants[index+1] != null)
                            return true;
                        return false;
                    }
                    @Override
                    public JBEnchant next() {
                        enchants[index] = enchants[index + 1];
                        return enchants[index];
                    }
                };
                return iterator;
            }

            public boolean contains(JBEnchant enchant) {
                while (iterator().hasNext()) {
                    if (iterator().next() == enchant) {
                        return true;
                    }
                }
                return false;
            }
            public JBEnchant getEnchant(String name) {
                while (iterator().hasNext()) {
                    JBEnchant next = iterator().next();
                    if (Objects.equals(next.getName(), name)) {
                        return iterator().next();
                    }
                }
                return null;
            }
        };
        return enchants;
    }

    API api = Main.instance.api;
    private String name;
    private int level;

    public JBEnchant(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void addLevel(int level) {
        this.level += level;
    }
    @Override
    public void removeLevel(int level) {
        this.level -= level;
    }
    @Override
    public void setLevel(int level) {
        this.level = level;
    }
    @Override
    public int getLevel() {
        return level;
    }
    @Override
    public String toString() {
        return api.getName(getName()) + " - " + getLevel();
    }
}
