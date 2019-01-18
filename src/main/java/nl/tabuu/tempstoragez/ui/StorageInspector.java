package nl.tabuu.tempstoragez.ui;

import nl.tabuu.tabuucore.inventory.InventorySize;
import nl.tabuu.tabuucore.inventory.ui.InventoryFormUI;
import nl.tabuu.tabuucore.inventory.ui.element.Button;
import nl.tabuu.tabuucore.inventory.ui.element.style.Style;
import nl.tabuu.tabuucore.inventory.ui.graphics.brush.CheckerBrush;
import nl.tabuu.tabuucore.inventory.ui.graphics.brush.IBrush;
import nl.tabuu.tabuucore.serialization.string.Serializer;
import nl.tabuu.tabuucore.util.Dictionary;
import nl.tabuu.tabuucore.util.ItemBuilder;
import nl.tabuu.tabuucore.util.ItemList;
import nl.tabuu.tabuucore.util.vector.Vector2f;
import nl.tabuu.tempstoragez.TempStorageZ;
import nl.tabuu.tempstoragez.api.event.TemporaryStorageWithdrawEvent;
import nl.tabuu.tempstoragez.storage.Storage;
import nl.tabuu.tempstoragez.storage.StorageItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StorageInspector extends InventoryFormUI {

    private Storage _storage;
    private int _page;
    private Dictionary _local;
    private List<Integer> _animationTasks;

    public StorageInspector(Storage storage) {
        super("Temporary Storage", InventorySize.DOUBLE_CHEST);
        _storage = storage;
        _storage.updateItems();
        _page = 0;
        _animationTasks = new ArrayList<>();
        _local = TempStorageZ.getInstance().getConfigurationManager().getConfiguration("lang").getDictionary("");
    }


    public StorageInspector(OfflinePlayer player) {
        this(TempStorageZ.getInstance().getStorageManager().getStorage(player));
    }

    @Override
    protected void draw() {
        unregisterTasks();
        setTile(_local.translate("UI_TITLE", "{PAGE}", (getPage() + 1) + "", "{PAGE_MAX}",(getMaxPage() + 1) + ""));
        reload();

        ItemBuilder
                yellow = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName(" "),
                purple = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName(" "),
                barrier = new ItemBuilder(Material.BARRIER).setDisplayName(" "),
                map = new ItemBuilder(Material.FILLED_MAP).setDisplayName(" "),
                lime = new ItemBuilder(Material.LIME_DYE).setDisplayName(" "),
                empty = new ItemBuilder(Material.AIR);

        IBrush
                border = new CheckerBrush(yellow.build(), purple.build());

        Style
                exitButtonStyle = new Style(barrier.setDisplayName(_local.translate("UI_EXIT")).build(), empty.build()),
                nextButtonStyle = new Style(map.setDisplayName(_local.translate("UI_NEXT")).build(), empty.build()),
                previousButtonStyle = new Style(map.setDisplayName(_local.translate("UI_PREVIOUS")).build(), empty.build()),
                collectButtonStyle = new Style(lime.setDisplayName(_local.translate("UI_COLLECT_ALL")).build(), empty.build());

        Button
                exitButton = new Button(exitButtonStyle, this::onExitButtonClick),
                nextButton = new Button(nextButtonStyle, this::onNextButtonClick),
                previousButton = new Button(previousButtonStyle, this::onPreviousButtonClick),
                collectButton = new Button(collectButtonStyle, this::onCollectButtonClick);


        setBrush(border);
        drawRectangle(new Vector2f(0, 0), new Vector2f(8, 5));

        setElement(new Vector2f(4, 5), exitButton);
        setElement(new Vector2f(5, 5), nextButton);
        setElement(new Vector2f(3, 5), previousButton);
        setElement(new Vector2f(6, 5), collectButton);

        int startIndex = _page * 28;
        int stopIndex = startIndex + 28;

        for(int index = startIndex; index < stopIndex; index++){
            int slot = index % 28;
            int x = slot % 7;
            int y = slot / 7;
            Vector2f position = new Vector2f(x, y).add(new Vector2f(1, 1));

            if(_storage.getStorageItems().size() <= index){
                setElement(position, new Button(new Style(Material.AIR, Material.AIR)));
                continue;
            }

            StorageItem item = _storage.getStorageItems().get(index);

            _animationTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TempStorageZ.getInstance(), () -> {
                String remainingTimeFormat = _local.translate("ITEM_EXPIRE_DATE_NONE");
                if(item.hasExpireDate()){
                    long remainingTime = item.getExpireDate() - System.currentTimeMillis();
                    if(remainingTime >= 0)
                        remainingTimeFormat = Serializer.TIME.serialize(remainingTime);
                    else
                        remainingTimeFormat = _local.translate("ITEM_EXPIRE_DATE_EXPIRED");
                }

                String remainingTimeString = _local.translate("UI_ITEM_EXPIRE_DATE", "{TIME_FORMAT}", remainingTimeFormat);
                String description = _local.translate("UI_ITEM_DESCRIPTION", "{DESCRIPTION}", item.getDescription());

                ItemStack displayItem = new ItemBuilder(item.getItemStack())
                        .addLore(remainingTimeString, description)
                        .build();

                Style itemButtonStyle = new Style(displayItem, empty.build());
                Button itemButton = new Button(itemButtonStyle, (p) -> onItemButtonClick(p, item));

                setElement(position, itemButton);
                super.draw();
            }, 0L, 20L));
        }

        super.draw();
    }

    @Override
    public void onClose(Player player) {
        if(getInventory().getViewers().size() <= 1)
            unregisterTasks();

        super.onClose(player);
    }

    private void unregisterTasks(){
        _animationTasks.forEach(id -> Bukkit.getScheduler().cancelTask(id));
    }

    private int getPage(){
        return _page;
    }

    private int getMaxPage(){
        return ((_storage.getStorageItems().size() - 1) / 28);
    }

    private boolean hasNextPage(){
        return getMaxPage() > _page;
    }

    private boolean hasPreviousPage(){
        return getPage() > 0;
    }

    private void previousPage(){
        if(hasPreviousPage()){
            _page--;
            draw();
        }
    }

    private void nextPage(){
        if(hasNextPage()){
            _page++;
            draw();
        }
    }

    private void onExitButtonClick(Player player){
        close(player);
    }

    private void onNextButtonClick(Player player){
        nextPage();
    }

    private void onPreviousButtonClick(Player player){
        previousPage();
    }

    private void onItemButtonClick(Player player, StorageItem item){
        if(withdrawItem(player, item))
            _storage.removeItem(item);

        _storage.updateItems();
        draw();
    }

    private void onCollectButtonClick(Player player){
        _storage.getStorageItems().removeIf(item -> withdrawItem(player, item));
        _storage.updateItems();
        draw();
    }

    private boolean withdrawItem(Player player, StorageItem item){
        ItemList itemList = new ItemList();
        ItemStack itemStack = item.getItemStack();

        itemList.addAll(player.getInventory().getStorageContents());
        boolean canFit = itemList.clone().stackAll(itemStack.clone()).isEmpty();

        if(canFit && (!item.hasExpireDate() || (item.getExpireDate() - System.currentTimeMillis() > 0))){
            TemporaryStorageWithdrawEvent withdrawEvent = new TemporaryStorageWithdrawEvent(player, _storage, item);
            Bukkit.getServer().getPluginManager().callEvent(withdrawEvent);

            if(!withdrawEvent.isCancelled()){
                player.getInventory().addItem(itemStack.clone());
                return true;
            }
        }

        return false;
    }
}
