package cloud.gteam.tabbedReborn.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Some generic-ish packet utils.
 */
public class Packets {

    /**
     * Creates a PLAYER_INFO packet from the params.
     * @param action
     * @param data
     * @return
     */
    public static WrapperPlayServerPlayerInfo getPacket(final WrapperPlayServerPlayerInfo.Action action, final WrapperPlayServerPlayerInfo.PlayerData data) {
        return getPacket(action, Collections.singletonList(data));
    }

    /**
     * Creates a PLAYER_INFO packet from the params.
     * @param action
     * @param data
     * @return
     */
    public static WrapperPlayServerPlayerInfo getPacket(final WrapperPlayServerPlayerInfo.Action action, final List<WrapperPlayServerPlayerInfo.PlayerData> data) {
        return new WrapperPlayServerPlayerInfo(action, data);
    }

    public static WrapperPlayServerPlayerInfoUpdate getPacket(final WrapperPlayServerPlayerInfoUpdate.Action action, final WrapperPlayServerPlayerInfoUpdate.PlayerInfo data) {
        return getPacket(action, Collections.singletonList(data));
    }

    public static WrapperPlayServerPlayerInfoUpdate getPacket(final WrapperPlayServerPlayerInfoUpdate.Action action, final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> data) {
        return new WrapperPlayServerPlayerInfoUpdate(action, data);
    }

    public static WrapperPlayServerPlayerInfoUpdate getPacketUpdate(final WrapperPlayServerPlayerInfoUpdate.Action action, final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> data) {
        return new WrapperPlayServerPlayerInfoUpdate(EnumSet.of(
                WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE

        ), data);
    }

    /**
     * Sends a list of PacketEvents packets to a player.
     * @param player
     * @param packets
     * @return
     */
    public static void send(final Player player, final List<PacketWrapper<?>> packets) {
        for (final PacketWrapper<?> packet : packets) PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }

    public static void sendUpdate(final Player player, final List<WrapperPlayServerPlayerInfoUpdate> packets) {
        for (WrapperPlayServerPlayerInfoUpdate packet : packets) PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
