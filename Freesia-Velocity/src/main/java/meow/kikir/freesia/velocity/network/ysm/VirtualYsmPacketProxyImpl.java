package meow.kikir.freesia.velocity.network.ysm;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.velocitypowered.api.proxy.Player;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import meow.kikir.freesia.velocity.Freesia;
import meow.kikir.freesia.velocity.network.mc.NbtRemapper;
import meow.kikir.freesia.velocity.network.mc.impl.StandardNbtRemapperImpl;
import meow.kikir.freesia.velocity.utils.FriendlyByteBuf;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class VirtualYsmPacketProxyImpl implements YsmPacketProxy {
    private final UUID virtualPlayerUUID;
    private final NbtRemapper nbtRemapper = new StandardNbtRemapperImpl();
    private volatile NBTCompound lastYsmEntityStatus = null;

    public VirtualYsmPacketProxyImpl(UUID virtualPlayerUUID) {
        this.virtualPlayerUUID = virtualPlayerUUID;
    }

    @Override
    public ProxyComputeResult processS2C(Key channelKey, ByteBuf copiedPacketData) {
        return null;
    }

    @Override
    public ProxyComputeResult processC2S(Key channelKey, ByteBuf copiedPacketData) {
        return null;
    }

    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    public void setEntityDataRaw(NBTCompound data) {
        this.lastYsmEntityStatus = data;
    }

    @Override
    public void refreshToOthers() {
        Freesia.tracker.getCanSee(this.virtualPlayerUUID).whenComplete((beingWatched, exception) -> { // Async tracker check request to backend server
            if (beingWatched != null) { // Actually there is impossible to be null
                for (UUID targetUUID : beingWatched) {
                    final Optional<Player> targetNullable = Freesia.PROXY_SERVER.getPlayer(targetUUID);

                    if (targetNullable.isPresent()) { // Skip send to NPCs
                        final Player target = targetNullable.get();

                        if (!Freesia.mapperManager.isPlayerInstalledYsm(target)) { // Skip if target is not ysm-installed
                            continue;
                        }

                        this.sendEntityStateTo(target); // Sync to target
                    }
                }
            }
        });
    }

    @Override
    public NBTCompound getCurrentEntityState() {
        return this.lastYsmEntityStatus;
    }

    @Override
    public void sendEntityStateTo(@NotNull Player target) {
        final int currentEntityId = Freesia.mapperManager.getVirtualPlayerEntityId(this.virtualPlayerUUID); // Get current entity id on the server of the player

        final NBTCompound lastEntityStatusTemp = this.lastYsmEntityStatus; // Copy the value instead of the reference

        if (lastEntityStatusTemp == null || currentEntityId == -1) { // If no data got or player is not in the backend server currently
            return;
        }

        try {
            final Object targetChannel = PacketEvents.getAPI().getProtocolManager().getChannel(target.getUniqueId()); // Get the netty channel of the player

            if (targetChannel == null) {
                return;
            }

            final ClientVersion clientVersion = PacketEvents.getAPI().getProtocolManager().getClientVersion(targetChannel); // Get the client version of the player

            final int targetProtocolVer = clientVersion.getProtocolVersion(); // Protocol version(Used for nbt remappers)
            final FriendlyByteBuf wrappedPacketData = new FriendlyByteBuf(Unpooled.buffer());

            wrappedPacketData.writeByte(4);
            wrappedPacketData.writeVarInt(currentEntityId);
            wrappedPacketData.writeBytes(this.nbtRemapper.shouldRemap(targetProtocolVer) ? this.nbtRemapper.remapToMasterVer(lastEntityStatusTemp) : this.nbtRemapper.remapToWorkerVer(lastEntityStatusTemp)); // Remap nbt if needed

            this.sendPluginMessageTo(target, YsmMapperPayloadManager.YSM_CHANNEL_KEY_VELOCITY, wrappedPacketData);
        } catch (Exception e) {
            Freesia.LOGGER.error("Error in encoding nbt or sending packet!", e);
        }
    }
}
