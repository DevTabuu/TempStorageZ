package nl.tabuu.tempstoragez.api.event;

import nl.tabuu.tempstoragez.api.storage.IStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class TemporaryStorageInteractEvent extends TemporaryStorageEvent implements Cancellable {

    private boolean _cancelled;
    private Player _player;

    public TemporaryStorageInteractEvent(Player player, IStorage storage){
        super(storage);
        _player = player;
    }

    public Player getPlayer(){
        return _player;
    }

    @Override
    public boolean isCancelled() {
        return _cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        _cancelled = cancelled;
    }
}
