package nl.tabuu.tempstoragez.api;

import nl.tabuu.tempstoragez.TempStorageZ;
import nl.tabuu.tempstoragez.api.storage.IStorage;
import nl.tabuu.tempstoragez.api.storage.IStorageItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class TempStorageAPI {

    private static TempStorageAPI INSTANCE;

    public static TempStorageAPI getInstance(){
        if(INSTANCE == null)
            INSTANCE = new TempStorageAPI();
        return INSTANCE;
    }

    private TempStorageAPI(){ }

    public IStorage getStorage(OfflinePlayer player){
        return TempStorageZ.getInstance().getStorageManager().getStorage(player);
    }

    public IStorageItem storeItem(IStorage storage, ItemStack itemStack, long expiresIn, String description){
        return storage.addItem(itemStack, expiresIn, description);
    }

    public IStorageItem storeItem(IStorage storage, ItemStack itemStack, long expiresIn){
        return storage.addItem(itemStack, expiresIn, "");
    }

    public IStorageItem storeItem(IStorage storage, ItemStack itemStack, String description){
        return storage.addItem(itemStack, 0L, description);
    }

    public IStorageItem storeItem(IStorage storage, ItemStack itemStack){
        return storage.addItem(itemStack, 0L, "");
    }
}
