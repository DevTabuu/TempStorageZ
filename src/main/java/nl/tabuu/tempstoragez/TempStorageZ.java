package nl.tabuu.tempstoragez;

import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tempstoragez.commands.TemporaryStorageCommand;
import nl.tabuu.tempstoragez.storage.StorageManager;

public class TempStorageZ extends TabuuCorePlugin {

    private static TempStorageZ INSTANCE;

    private StorageManager _storageManager;

    @Override
    public void onEnable(){
        INSTANCE = this;

        getConfigurationManager().addConfiguration("config");
        getConfigurationManager().addConfiguration("data");
        getConfigurationManager().addConfiguration("lang");

        _storageManager = new StorageManager();
        _storageManager.reload();

        getCommand("temporarystorage").setExecutor(new TemporaryStorageCommand());

        getInstance().getLogger().info("TempStorageZ is now enabled.");
    }

    @Override
    public void onDisable(){
        _storageManager.save();
        getInstance().getLogger().info("TempStorageZ is now disabled.");
    }

    public StorageManager getStorageManager(){
        return _storageManager;
    }

    public static TempStorageZ getInstance(){
        return INSTANCE;
    }
}
