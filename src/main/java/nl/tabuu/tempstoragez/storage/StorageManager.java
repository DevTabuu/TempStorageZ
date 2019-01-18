package nl.tabuu.tempstoragez.storage;

import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tempstoragez.TempStorageZ;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public class StorageManager {

    private IConfiguration _data;
    private HashMap<UUID, Storage> _storages;

    public StorageManager(){
        _data = TempStorageZ.getInstance().getConfigurationManager().getConfiguration("data");
        _storages = new HashMap<>();

        load();
    }

    public Storage getStorage(OfflinePlayer player){
        UUID uuid = player.getUniqueId();

        if(!_storages.containsKey(uuid))
            _storages.put(uuid, new Storage(uuid));

        return _storages.get(uuid);
    }

    public void load(){
        _storages.clear();

        if(!_data.getKeys(false).contains("StorageData"))
            return;

        for(String uuidString : _data.getConfigurationSection("StorageData").getKeys(false)){
            UUID uuid = UUID.fromString(uuidString);
            Storage storage = Storage.fromString(uuid, _data.getString("StorageData." + uuidString));

            _storages.put(uuid, storage);
        }
    }

    public void save(){
        for(Storage storage : _storages.values()){
            UUID uuid = storage.getOwner().getUniqueId();
            _data.set("StorageData." + uuid.toString(), storage.toString());
        }

        _data.save();
    }

}
