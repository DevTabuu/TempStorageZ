package nl.tabuu.tempstoragez;

import nl.tabuu.tabuucore.plugin.TabuuCorePlugin;
import nl.tabuu.tempstoragez.commands.TemporaryStorageCommand;
import nl.tabuu.tempstoragez.storage.StorageManager;

public class TempStorageZ extends TabuuCorePlugin {

    private static TempStorageZ _instance;

    private StorageManager _storageManager;

    @Override
    public void onEnable(){
        _instance = this;

        getConfigurationManager().addConfiguration("config");
        getConfigurationManager().addConfiguration("data");
        getConfigurationManager().addConfiguration("lang");

        this.getCommand("temporarystorage").setExecutor(new TemporaryStorageCommand());

        _storageManager = new StorageManager();

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
        return _instance;
    }
}
