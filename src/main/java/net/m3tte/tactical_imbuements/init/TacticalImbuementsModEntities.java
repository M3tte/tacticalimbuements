
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.m3tte.tactical_imbuements.init;

import net.m3tte.tactical_imbuements.entity.*;
import net.m3tte.tactical_imbuements.TacticalImbuementsMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TacticalImbuementsModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TacticalImbuementsMod.MODID);
	public static final RegistryObject<EntityType<EmptyflaskthrowableEntity>> EMPTYFLASKTHROWABLE = register("projectile_emptyflaskthrowable", EntityType.Builder.<EmptyflaskthrowableEntity>of(EmptyflaskthrowableEntity::new, MobCategory.MISC)
			.setCustomClientFactory(EmptyflaskthrowableEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FireflaskthrowableEntity>> FIREFLASKTHROWABLE = register("projectile_fireflaskthrowable", EntityType.Builder.<FireflaskthrowableEntity>of(FireflaskthrowableEntity::new, MobCategory.MISC)
			.setCustomClientFactory(FireflaskthrowableEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));

	public static final RegistryObject<EntityType<VenomFlaskThrowableEntity>> VENOMFLASKTHROWABLE = register("projectile_venomflaskthrowable", EntityType.Builder.<VenomFlaskThrowableEntity>of(VenomFlaskThrowableEntity::new, MobCategory.MISC)
			.setCustomClientFactory(VenomFlaskThrowableEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));

	public static final RegistryObject<EntityType<FreezeFlaskThrowableEntity>> FREEZEFLASKTHROWABLE = register("projectile_freezeflaskthrowable", EntityType.Builder.<FreezeFlaskThrowableEntity>of(FreezeFlaskThrowableEntity::new, MobCategory.MISC)
			.setCustomClientFactory(FreezeFlaskThrowableEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));

	public static final RegistryObject<EntityType<SparkFlaskThrowableEntity>> SPARKFLASKTHROWABLE = register("projectile_sparkflaskthrowable", EntityType.Builder.<SparkFlaskThrowableEntity>of(SparkFlaskThrowableEntity::new, MobCategory.MISC)
			.setCustomClientFactory(SparkFlaskThrowableEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));


	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
	}
}
