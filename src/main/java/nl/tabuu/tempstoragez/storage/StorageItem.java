package nl.tabuu.tempstoragez.storage;

import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tempstoragez.TempStorageZ;
import nl.tabuu.tempstoragez.api.storage.IStorageItem;
import org.bukkit.inventory.ItemStack;

public class StorageItem implements IStorageItem {
    private ItemStack _itemStack;
    private long _expireDate;
    private String _description;

    private Dictionary _local;

    public StorageItem(ItemStack itemStack, long expireDate, String description){
        _itemStack = itemStack.clone();
        _expireDate = expireDate;
        _description = description;

        if(_expireDate == 0){
            long defaultExpireTime = TempStorageZ.getInstance().getConfigurationManager().getConfiguration("config").getTime("DefaultExpireTime");
            if(defaultExpireTime > 0)
                _expireDate = System.currentTimeMillis() + defaultExpireTime;
        }

        _local = TempStorageZ.getInstance().getConfigurationManager().getConfiguration("lang").getDictionary("");
    }

    public StorageItem(ItemStack itemStack, long expireDate){
        this(itemStack, expireDate, "");
    }

    public StorageItem(ItemStack itemStack, String description){
        this(itemStack, 0L, description);
    }

    public StorageItem(ItemStack itemStack){
        this(itemStack, 0L);
    }

    @Override
    public ItemStack getItemStack(){
        return _itemStack;
    }

    @Override
    public boolean hasExpireDate() {
        return getExpireDate() > 0;
    }

    public long getExpireDate(){
        return _expireDate;
    }

    @Override
    public boolean hasDescription() {
        return _description.length() > 0;
    }

    @Override
    public String getDescription() {
        if(!hasDescription())
            return _local.translate("ITEM_DESCRIPTION_NONE");
        return _description;
    }
}
