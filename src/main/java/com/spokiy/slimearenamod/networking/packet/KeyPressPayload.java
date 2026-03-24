package com.spokiy.slimearenamod.networking.packet;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record KeyPressPayload(BlockPos blockPos) implements CustomPayload {
    public static final CustomPayload.Id<KeyPressPayload> ID =
            new CustomPayload.Id<>(SlimeArenaMod.prefix("keypress"));
    public static final PacketCodec<RegistryByteBuf, KeyPressPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, KeyPressPayload::blockPos,
                    KeyPressPayload::new
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}