package nl.tabuu.tempstoragez.storage;

import nl.tabuu.tabuucore.configuration.IConfiguration;
import nl.tabuu.tempstoragez.TempStorageZ;
import nl.tabuu.tempstoragez.api.TempStorageAPI;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.UUID;

public class StorageManager {

    private IConfiguration _data;
    private HashMap<UUID, Storage> _storage;

    public StorageManager(){
        _data = TempStorageZ.getInstance().getConfigurationManager().getConfiguration("data");
        _storage = new HashMap<>();

        reload();
    }

    public Storage getStorage(OfflinePlayer player){
        UUID uuid = player.getUniqueId();

        if(!_storage.containsKey(uuid))
            _storage.put(uuid, new Storage(uuid));

        return _storage.get(uuid);
    }

    public void reload(){
        _storage.clear();

        if(!_data.contains("StorageData"))
            return;

        for(String uuidString : _data.getConfigurationSection("StorageData").getKeys(false)){
            UUID uuid = UUID.fromString(uuidString);
            Storage storage = Storage.fromString(uuid, _data.getString("StorageData." + uuidString));

            _storage.put(uuid, storage);
        }
    }

    public void save(){
        for(Storage storage : _storage.values()){
            UUID uuid = storage.getOwner().getUniqueId();
            _data.set("StorageData." + uuid.toString(), storage.toString());
        }

        _data.save();
    }

}
