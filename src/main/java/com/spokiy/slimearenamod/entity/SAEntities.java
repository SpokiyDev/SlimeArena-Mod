package com.spokiy.slimearenamod.entity;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.entity.projectile.SlimeBallEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SAEntities {
    public static final EntityType<SlimeBallEntity> SLIME_BALL = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("slime_ball"), EntityType.Builder.<SlimeBallEntity>create(SlimeBallEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build()
    );

    public static final EntityType<DrivableMinecartEntity> DRIVABLE_MINECART = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("drivable_minecart"), EntityType.Builder.<DrivableMinecartEntity>create(DrivableMinecartEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98F, 0.7F)
                    .passengerAttachments(0.1875F)
                    .maxTrackingRange(8)
                    .build()

    );



    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Entities for " + SlimeArenaMod.MOD_ID);

    }
}
