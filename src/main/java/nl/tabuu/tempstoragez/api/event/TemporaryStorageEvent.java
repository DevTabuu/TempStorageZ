package nl.tabuu.tempstoragez.api.event;

import nl.tabuu.tempstoragez.api.storage.IStorage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TemporaryStorageEvent extends Event {

    private IStorage _storage;

    public TemporaryStorageEvent(IStorage storage){
        _storage = storage;
    }

    public IStorage getStorage(){
        return _storage;
    }

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
