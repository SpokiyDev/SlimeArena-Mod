package com.spokiy.slimearenamod.networking.packet;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ChangeYawPayload() implements CustomPayload {
    public static final CustomPayload.Id<ChangeYawPayload> ID =
            new CustomPayload.Id<>(SlimeArenaMod.prefix("keypress"));
    public static final PacketCodec<RegistryByteBuf, ChangeYawPayload> CODEC =
            PacketCodec.unit(new ChangeYawPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
