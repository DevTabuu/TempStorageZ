package nl.tabuu.tempstoragez.storage;

import nl.tabuu.tabuucore.serialization.bytes.Serializer;
import nl.tabuu.tabuucore.util.ItemBuilder;
import nl.tabuu.tempstoragez.api.storage.IStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Storage implements IStorage {
    private UUID _owner;
    private List<StorageItem> _items;

    public Storage(UUID owner, List<StorageItem> items) {
        _owner = owner;
        _items = items;
    }

    public Storage(UUID owner) {
        this(owner, new ArrayList<>());
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(_owner);
    }

    public List<StorageItem> getStorageItems() {
        return _items;
    }

    public ItemStack[] getStorageContents() {
        return _items.stream().map(StorageItem::getItemStack).toArray(ItemStack[]::new);
    }

    public StorageItem addItem(ItemStack item, long expiresIn, String description) {
        long currentTime = System.currentTimeMillis();
        StorageItem storageItem;

        if(expiresIn == 0)
            storageItem = new StorageItem(item, description);
        else
            storageItem = new StorageItem(item, (currentTime + expiresIn), description);

        addItem(storageItem);

        return storageItem;
    }

    public void addItem(StorageItem item) {
        _items.add(item);
    }

    public void removeItem(StorageItem item) {
        _items.remove(item);
    }

    public void removeItem(ItemStack item) {
        ItemStack[] contents = getStorageContents();

        for (int i = 0; i < contents.length; i++) {
            if (contents[i].equals(item)) {
                _items.remove(i);
                break;
            }
        }
    }

    public void updateItems() {
        long currentTime = System.currentTimeMillis();
        _items.removeIf(item -> item.hasExpireDate() && item.getExpireDate() <= currentTime);
    }

    @Override
    public String toString() {
        ItemStack[] items = getStorageItems().stream()
                .map(storageItem -> {
                    ItemStack itemStack = storageItem.getItemStack();
                    ItemBuilder builder = new ItemBuilder(itemStack);
                    builder.getNBTTagCompound().setLong("TemporaryStorageExpireDate", storageItem.getExpireDate());
                    builder.getNBTTagCompound().setString("TemporaryStorageDescription", storageItem.getDescription());
                    return builder.build();
                })
                .toArray(ItemStack[]::new);

        byte[] contentBytes = Serializer.ITEMSTACK.serialize(items);
        return Base64.getEncoder().encodeToString(contentBytes);
    }

    public static Storage fromString(UUID owner, String string) {
        byte[] contentBytes = Base64.getDecoder().decode(string.getBytes());
        ItemStack[] items = Serializer.ITEMSTACK.deserialize(contentBytes);

        List<StorageItem> storageItems = Arrays.stream(items)
                .map(item -> {
                    ItemBuilder builder = new ItemBuilder(item);
                    long expireDate = builder.getNBTTagCompound().getLong("TemporaryStorageExpireDate");
                    builder.getNBTTagCompound().removeKey("TemporaryStorageExpireDate");

                    String description = builder.getNBTTagCompound().getString("TemporaryStorageDescription");
                    builder.getNBTTagCompound().removeKey("TemporaryStorageDescription");
                    return new StorageItem(builder.build(), expireDate, description);
                })
                .collect(Collectors.toList());

        return new Storage(owner, storageItems);
    }
}