package nl.tabuu.tempstoragez.api.storage;

import nl.tabuu.tempstoragez.storage.StorageItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IStorage {

    OfflinePlayer getOwner();

    List<StorageItem> getStorageItems();

    ItemStack[] getStorageContents();

    IStorageItem addItem(ItemStack item, long expiresIn, String description);

    IStorageItem addItem(ItemStack item, long expiresIn);

    IStorageItem addItem(ItemStack item, String description);

    void addItem(IStorageItem item);

    void removeItem(ItemStack item);

    void removeItem(IStorageItem item);
}
