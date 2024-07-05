package gg.earthme.cyanidin.cyanidin.network.mc.impl;

import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTLimiter;
import com.github.retrooper.packetevents.protocol.nbt.serializer.DefaultNBTSerializer;
import gg.earthme.cyanidin.cyanidin.network.mc.NbtRemapper;
import gg.earthme.cyanidin.cyanidin.utils.FriendlyByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StandardNbtRemapperImpl implements NbtRemapper {
    private final DefaultNBTSerializer serializer = new DefaultNBTSerializer();

    @Override
    public boolean shouldRemap(int pid) {
        return pid < 764; //1.20.2
    }

    @Override
    public byte[] remapToMasterVer(NBTCompound nbt) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);

        this.serializer.serializeTag(dos, nbt, true); //1.20.2
        dos.flush();

        return bos.toByteArray();
    }

    @Override
    public byte[] remapToWorkerVer(NBTCompound nbt) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);

        this.serializer.serializeTag(dos, nbt, false);
        dos.flush();

        return bos.toByteArray();
    }

    @Override
    public NBTCompound readBound(FriendlyByteBuf data) throws IOException {
        return (NBTCompound) this.serializer.deserializeTag(new NBTLimiter(data), new ByteBufInputStream(data), false); //1.21
    }
}
