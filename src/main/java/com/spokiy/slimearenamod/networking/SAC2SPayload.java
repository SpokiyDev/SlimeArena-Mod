package com.spokiy.slimearenamod.networking;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record SAC2SPayload(BlockPos blockPos) implements CustomPayload {
    public static final CustomPayload.Id<SAC2SPayload> ID =
            new CustomPayload.Id<>(SlimeArenaMod.prefix("keypress"));
    public static final PacketCodec<RegistryByteBuf, SAC2SPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, SAC2SPayload::blockPos,
                    SAC2SPayload::new
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}