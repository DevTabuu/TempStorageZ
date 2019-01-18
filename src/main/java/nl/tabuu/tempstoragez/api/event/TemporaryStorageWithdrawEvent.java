package nl.tabuu.tempstoragez.api.event;

import nl.tabuu.tempstoragez.api.storage.IStorage;
import nl.tabuu.tempstoragez.api.storage.IStorageItem;
import org.bukkit.entity.Player;

public class TemporaryStorageWithdrawEvent extends TemporaryStorageInteractEvent {

    private IStorageItem _item;

    public TemporaryStorageWithdrawEvent(Player player, IStorage storage, IStorageItem item) {
        super(player, storage);
        _item = item;
    }

    public IStorageItem getStorageItem(){
        return _item;
    }

}
