package com.spokiy.slimearenamod.networking.packet;

import com.spokiy.slimearenamod.SlimeArenaMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record JumpInputPayload(int entityId, boolean jumping) implements CustomPayload {

    public static final CustomPayload.Id<JumpInputPayload> ID =
            new CustomPayload.Id<>(SlimeArenaMod.prefix("jump_input"));

    public static final PacketCodec<RegistryByteBuf, JumpInputPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, JumpInputPayload::entityId,
                    PacketCodecs.BOOL, JumpInputPayload::jumping,
                    JumpInputPayload::new
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}