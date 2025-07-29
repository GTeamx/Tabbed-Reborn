package cloud.gteam.tabbedReborn.tablist;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import com.google.common.base.Preconditions;
import cloud.gteam.tabbedReborn.Tabbed;
import cloud.gteam.tabbedReborn.item.TabItem;
import cloud.gteam.tabbedReborn.util.Packets;
import cloud.gteam.tabbedReborn.util.Skin;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

/**
 * A simple implementation of a custom tab list that supports batch updates.
 */
public class SimpleTabList extends TitledTabList implements CustomTabList {

    public static int MAXIMUM_ITEMS = 4 * 20; // client maximum is 4x20 (4 columns, 20 rows)

    protected final Tabbed tabbed;
    protected final Map<Integer,TabItem> items;
    private final int maxItems;
    private final int minColumnWidth;
    private final int maxColumnWidth;

    private boolean batchEnabled;
    private final Map<Integer,TabItem> clientItems;

    private static final Map<Skin, Map<Integer, UserProfile>> PROFILE_INDEX_CACHE = new HashMap<>();

    public SimpleTabList(final Tabbed tabbed, final Player player, final int maxItems, final int minColumnWidth, final int maxColumnWidth) {

        super(player);

        Preconditions.checkArgument(maxItems <= MAXIMUM_ITEMS, "maxItems cannot exceed client maximum of " + MAXIMUM_ITEMS);
        Preconditions.checkArgument(minColumnWidth <= maxColumnWidth || maxColumnWidth < 0, "minColumnWidth cannot be greater than maxColumnWidth");

        this.tabbed = tabbed;
        this.maxItems = maxItems < 0 ? MAXIMUM_ITEMS : maxItems;
        this.minColumnWidth = minColumnWidth;
        this.maxColumnWidth = maxColumnWidth;
        this.clientItems = new HashMap<>();
        this.items = new HashMap<>();

    }

    public int getMaxItems() {
        return maxItems;
    }

    @Override
    public SimpleTabList enable() {
        super.enable();
        return this;
    }

    @Override
    public SimpleTabList disable() {
        super.disable();
        return this;
    }

    /**
     * Sends the batch update to the player and resets the batch.
     */
    public void batchUpdate() {
        update(this.clientItems, this.items, true);
        this.clientItems.clear();
        this.clientItems.putAll(this.items);
    }

    /**
     * Reset the existing batch.
     */
    public void batchReset() {
        this.items.clear();
        this.items.putAll(this.clientItems);
    }

    /**
     * Enable batch processing of tab items. Modifications to the tab list
     * will not be sent to the client until {@link #batchUpdate()} is called.
     * @param batchEnabled
     */
    public void setBatchEnabled(final boolean batchEnabled) {

        if (this.batchEnabled == batchEnabled) return;

        this.batchEnabled = batchEnabled;
        this.clientItems.clear();

        if (this.batchEnabled) this.clientItems.putAll(this.items);

    }

    public void add(final TabItem item) {
        set(getNextIndex(), item);
    }

    public void add(final int index, final TabItem item) {

        validateIndex(index);

        final Map<Integer, TabItem> current = new HashMap<>(this.items);

        final Map<Integer,TabItem> map = new HashMap<>();
        for (int i = index; i < getMaxItems(); i++) {

            if (!contains(i)) break;

            final TabItem move = get(i);
            map.put(i + 1, move);

        }

        map.put(index, item);
        update(current, map);

    }

    public TabItem set(final int index, final TabItem item) {
        final Map<Integer,TabItem> items = new HashMap<>(1);
        items.put(index, item);
        return set(items).get(index);
    }

    public Map<Integer,TabItem> set(final Map<Integer,TabItem> items) {

        for (final Entry<Integer,TabItem> entry : items.entrySet()) validateIndex(entry.getKey());

        final Map<Integer, TabItem> oldItems = new HashMap<>(this.items);
        update(oldItems, items);

        return oldItems;

    }

    public TabItem remove(final int index) {

        validateIndex(index);

        final TabItem removed = this.items.remove(index);
        update(index, removed, null);

        return removed;

    }

    public <T extends TabItem> T remove(final T item) {

        final Iterator<Entry<Integer,TabItem>> iterator = this.items.entrySet().iterator();

        while (iterator.hasNext()) {

            final Entry<Integer,TabItem> entry = iterator.next();

            if (entry.getValue().equals(item)) remove(entry.getKey());

        }

        return item;

    }

    public boolean contains(final int index) {
        validateIndex(index);
        return this.items.containsKey(index);
    }

    public TabItem get(final int index) {
        validateIndex(index);
        return this.items.get(index);
    }

    public void update() {
        update(this.items, this.items);
    }

    public void update(final int index) {
        final Map<Integer,TabItem> map = new HashMap<>();
        map.put(index, get(index));
        update(index, get(index), get(index));
    }

    public int getNextIndex() {

        for (int index = 0; index < getMaxItems(); index++) {

            if (!contains(index)) return index;

        }

        // tablist is full
        return -1;

    }

    protected void update(final int index, final TabItem oldItem, final TabItem newItem) {

        final Map<Integer,TabItem> oldItems = new HashMap<>(1);
        oldItems.put(index, oldItem);

        final Map<Integer,TabItem> newItems = new HashMap<>(1);
        newItems.put(index, newItem);

        update(oldItems, newItems);

    }

    protected void update(final Map<Integer,TabItem> oldItems, final Map<Integer,TabItem> items) {
        update(oldItems, items, false);
    }

    private void validateIndex(final int index) {
        Preconditions.checkArgument(index > 0 || index < getMaxItems(), "index not in allowed range");
    }

    private boolean put(final int index, final TabItem item) {

        if (index < 0 || index >= getMaxItems()) return false;

        if (item == null) {
            this.items.remove(index);
            return true;
        }

        this.items.put(index, item);
        return true;

    }

    private final Map<Integer,TabItem> putAll(final Map<Integer,TabItem> items) {

        final HashMap<Integer,TabItem> result = new HashMap<>(items.size());

        for (final Entry<Integer,TabItem> entry : items.entrySet())
            if (put(entry.getKey(), entry.getValue())) result.put(entry.getKey(), entry.getValue());

        return result;

    }

    private void update(final Map<Integer,TabItem> oldItems, final Map<Integer,TabItem> items, final boolean isBatch) {

        if (this.batchEnabled && !isBatch) {
            this.items.putAll(items);
            return;
        }

        final Map<Integer,TabItem> newItems = putAll(items);

        Packets.send(this.player, getUpdate(oldItems, newItems));

    }

    private List<PacketWrapper<?>> getUpdate(final Map<Integer,TabItem> oldItems, final Map<Integer,TabItem> newItems) {

        final List<PacketWrapper<?>> removePlayer = new ArrayList<>();
        final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> addPlayer = new ArrayList<>();
        final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> displayChanged = new ArrayList<>();
        final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> pingUpdated = new ArrayList<>();

        for (final Entry<Integer, TabItem> entry : newItems.entrySet()) {

            final int index = entry.getKey();
            final TabItem oldItem = oldItems.get(index);
            final TabItem newItem = entry.getValue();

            if (newItem == null && oldItem != null) { // TabItem has been removed.
                removePlayer.add(getPlayerInfoData(index, oldItem));
                continue;
            }

            final boolean skinChanged = oldItem == null || newItem.updateSkin() || !newItem.getSkin().equals(oldItem.getSkin());
            final boolean textChanged = oldItem == null || newItem.updateText() || !newItem.getText().equals(oldItem.getText());
            final boolean pingChanged = oldItem == null || newItem.updatePing() || oldItem.getPing() != newItem.getPing();

            if (skinChanged) {

                if (oldItem != null) removePlayer.add(getPlayerInfoData(index, oldItem));

                addPlayer.add(getPlayerInfoUpdateData(index, newItem));

            } else if (pingChanged) pingUpdated.add(getPlayerInfoUpdateData(index, newItem));

            if (textChanged) displayChanged.add(getPlayerInfoUpdateData(index, newItem));

        }

        final List<PacketWrapper<?>> result = new ArrayList<>(4);

        if (removePlayer != null || addPlayer.size() > 0) {

            if (removePlayer != null) result.addAll(removePlayer);

            result.add(Packets.getPacketUpdate(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, addPlayer));

        }
        if (displayChanged.size() > 0) result.add(Packets.getPacketUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME, displayChanged));
        if (pingUpdated.size() > 0) result.add(Packets.getPacketUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY, pingUpdated));

        return result;

    }

    private WrapperPlayServerPlayerInfoRemove getPlayerInfoData(final int index, final TabItem item) {
        final UserProfile profile = getGameProfile(index, item);
        return getPlayerInfoData(profile, item.getPing(), item.getText());
    }

    private WrapperPlayServerPlayerInfoRemove getPlayerInfoData(final UserProfile profile, final int ping, String displayName) {

        if (displayName != null) {
            // min width
            while (displayName.length() < this.minColumnWidth) displayName += " ";

            // max width
            if (this.maxColumnWidth > 0)
                while (displayName.length() > this.maxColumnWidth) displayName = displayName.substring(0, displayName.length() - 1);

        }

        return new WrapperPlayServerPlayerInfoRemove(profile.getUUID());

    }

    private WrapperPlayServerPlayerInfoUpdate.PlayerInfo getPlayerInfoUpdateData(final int index, final TabItem item) {
        final UserProfile profile = getGameProfile(index, item);
        return getPlayerInfoUpdateData(profile, item.getPing(), item.getText());
    }

    private WrapperPlayServerPlayerInfoUpdate.PlayerInfo getPlayerInfoUpdateData(final UserProfile profile, final int ping, String displayName) {

        if (displayName != null) {
            // min width
            while (displayName.length() < this.minColumnWidth) displayName += " ";

            // max width
            if (this.maxColumnWidth > 0)
                while (displayName.length() > this.maxColumnWidth) displayName = displayName.substring(0, displayName.length() - 1);

        }

        return new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(profile, true, ping, GameMode.ADVENTURE, displayName == null ? null : AdventureSerializer.fromLegacyFormat(displayName), null);

    }

    private UserProfile getGameProfile(final int index, final TabItem item) {

        final Skin skin = item.getSkin();

        if (!PROFILE_INDEX_CACHE.containsKey(skin)) // Cached by skins, so if you change the skins a lot, it still works while being efficient.
            PROFILE_INDEX_CACHE.put(skin, new HashMap<>());

        final Map<Integer, UserProfile> indexCache = PROFILE_INDEX_CACHE.get(skin);

        if (!indexCache.containsKey(index)) { // Profile is not cached, generate and cache one.

            final String name = String.format("%03d", index) + "|UpdateMC"; // Starts with 00 so they are sorted in alphabetical order and appear in the right order.
            final UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

            final UserProfile profile = new UserProfile(uuid, name); // Create a profile to cache by skin and index.
            final List<TextureProperty> listTextureProperty = new ArrayList<>();
            listTextureProperty.add(item.getSkin().getProperty());
            profile.setTextureProperties(listTextureProperty);
            indexCache.put(index, profile); // Cache the profile.

        }

        return indexCache.get(index);

    }

    public boolean getBatchEnabled() {
        return batchEnabled;
    }

}
