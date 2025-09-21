package cloud.gteam.tabbedReborn.tablist;

import cloud.gteam.tabbedReborn.Tabbed;
import cloud.gteam.tabbedReborn.item.PlayerTabItem;
import cloud.gteam.tabbedReborn.item.TabItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An implementation of SimpleTabList that behaves like vanilla Minecraft.
 */
public final class DefaultTabList extends SimpleTabList implements Listener {

    private final Map<Player,String> names = new HashMap<>();

    private int taskId;

    public DefaultTabList(final Tabbed tabbed, final Player player, final int maxItems) {
        super(tabbed, player, maxItems, -1, -1);
    }

    @Override
    public DefaultTabList enable() {

        super.enable();
        this.tabbed.getPlugin().getServer().getPluginManager().registerEvents(this, this.tabbed.getPlugin());

        for (final Player target : Bukkit.getOnlinePlayers()) addPlayer(target);

        // Because there is no PlayerListNameUpdateEvent in Bukkit
        this.taskId = this.tabbed.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(this.tabbed.getPlugin(), () -> {

            for (final Player target : Bukkit.getOnlinePlayers()) {

                if (!names.containsKey(target)) continue;

                final String prevName = names.get(target);
                final String currName = target.getPlayerListName();

                if (prevName.equals(currName)) continue;

                final int index = getTabItemIndex(target);
                update(index);
                names.put(target, currName);

            }

        }, 0, 5);

        return this;

    }

    @Override
    public DefaultTabList disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        this.tabbed.getPlugin().getServer().getScheduler().cancelTask(this.taskId);
        return this;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(final PlayerQuitEvent event) {
        remove(getTabItemIndex(event.getPlayer()));
    }

    private void addPlayer(final Player player) {
        add(getInsertLocation(player), new PlayerTabItem(player));
        this.names.put(player, player.getPlayerListName());
    }

    private int getTabItemIndex(final Player player) {
        for (final Entry<Integer,TabItem> item : this.items.entrySet()) {

            // items will always be players in this case, cast is safe
            final PlayerTabItem tabItem = (PlayerTabItem) item.getValue();
            if (tabItem.getPlayer().equals(player)) return item.getKey();

        }

        return -1;

    }

    private int getInsertLocation(final Player player) {

        for (final Entry<Integer,TabItem> item : this.items.entrySet()) {

            // items will always be players in this case, cast is safe
            final PlayerTabItem tabItem = (PlayerTabItem) item.getValue();

            if (player.getName().compareTo(tabItem.getPlayer().getName()) < 0) return item.getKey();

        }

        return getNextIndex();

    }

}
