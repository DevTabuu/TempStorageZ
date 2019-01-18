package nl.tabuu.tempstoragez.api.storage;

import org.bukkit.inventory.ItemStack;

public interface IStorageItem {

    ItemStack getItemStack();

    boolean hasExpireDate();

    long getExpireDate();

    boolean hasDescription();

    String getDescription();
}
