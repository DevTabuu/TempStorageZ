package nl.tabuu.tempstoragez.commands;

import nl.tabuu.tabuucore.command.*;
import nl.tabuu.tempstoragez.TempStorageZ;
import nl.tabuu.tempstoragez.storage.Storage;
import nl.tabuu.tempstoragez.ui.StorageInspector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class TemporaryStorageCommand extends Command {

    public TemporaryStorageCommand() {
        super(Bukkit.getPluginCommand("temporarystorage"));

        addSubCommand("store", new TemporaryStorageStoreCommand(this));

        setRequiredSenderType(SenderType.PLAYER);
        setArgumentConverter(new OrderedArgumentConverter().setSequence(ArgumentType.OFFLINE_PLAYER));
    }

    @Override
    protected CommandResult onCommand(CommandSender commandSender, List<Optional<?>> list) {
        Player player = (Player) commandSender;
        OfflinePlayer target = player;

        if(list.get(0).isPresent() && commandSender.hasPermission("tempstoragez.open.other"))
            target = (OfflinePlayer) list.get(0).get();

        Storage storage = TempStorageZ.getInstance().getStorageManager().getStorage(target);
        new StorageInspector(storage).open(player);

        return CommandResult.SUCCESS;
    }

    class TemporaryStorageStoreCommand extends Command {

        protected TemporaryStorageStoreCommand(Command parent) {
            super("temporarystorage store", parent);

            setRequiredSenderType(SenderType.PLAYER);
            setArgumentConverter(new OrderedArgumentConverter().setSequence(ArgumentType.TIME, ArgumentType.STRING, ArgumentType.OFFLINE_PLAYER));
        }

        @Override
        protected CommandResult onCommand(CommandSender commandSender, List<Optional<?>> list) {
            Player player = (Player) commandSender;
            OfflinePlayer target = player;
            String description = "";
            ItemStack item = player.getItemInHand();
            long expireIn = 0L;

            if(list.get(0).isPresent())
                expireIn = (Long) list.get(0).get();

            if(list.get(1).isPresent())
                description = (String) list.get(1).get();

            if(list.get(2).isPresent() && player.hasPermission("tempstoragez.store.other"))
                target = (OfflinePlayer) list.get(2).get();

            player.setItemInHand(null);

            if(item != null && !item.getType().equals(Material.AIR))
                TempStorageZ.getInstance().getStorageManager().getStorage(target).addItem(item, expireIn, description);

            return CommandResult.SUCCESS;
        }
    }
}
