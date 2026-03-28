package com.spokiy.slimearenamod.world.entity;

import com.spokiy.slimearenamod.SlimeArenaMod;
import com.spokiy.slimearenamod.world.entity.projectile.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class SAEntities {
    public static final EntityType<SlimeBallEntity> SLIME_BALL = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("slime_ball"), EntityType.Builder.<SlimeBallEntity>create(SlimeBallEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build()
    );

    public static final EntityType<ThrownSpawnerEntity> THROWN_SPAWNER = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("thrown_spawner"), EntityType.Builder.<ThrownSpawnerEntity>create(ThrownSpawnerEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98F, 0.98F).maxTrackingRange(10).trackingTickInterval(1).build()
    );
    public static final EntityType<ThrownHeavyCore> THROWN_HEAVY_CORE = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("thrown_heavy_core"), EntityType.Builder.<ThrownHeavyCore>create(ThrownHeavyCore::new, SpawnGroup.MISC)
                    .dimensions(0.48F, 0.48F).maxTrackingRange(10).trackingTickInterval(1).build()
    );
    public static final EntityType<ThrownPumpkinEntity> THROWN_PUMPKIN = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("thrown_pumpkin"), EntityType.Builder.<ThrownPumpkinEntity>create(ThrownPumpkinEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98F, 0.98F).maxTrackingRange(10).trackingTickInterval(1).build()
    );

    public static final EntityType<DrivableMinecartEntity> DRIVABLE_MINECART = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("drivable_minecart"), EntityType.Builder.<DrivableMinecartEntity>create(DrivableMinecartEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8).build()

    );


    public static final EntityType<SlimeTrapEntity> SLIME_TRAP = Registry.register(Registries.ENTITY_TYPE,
            SlimeArenaMod.prefix("slime_trap"), EntityType.Builder.<SlimeTrapEntity>create(SlimeTrapEntity::new, SpawnGroup.MISC)
                    .dimensions(1.58F, 0.125F).maxTrackingRange(10).trackingTickInterval(1).build()
    );




    public static void register() {
        SlimeArenaMod.LOGGER.info("Registering Mod Entities for " + SlimeArenaMod.MOD_ID);

    }
}
