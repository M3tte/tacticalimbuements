package net.m3tte.tactical_imbuements.procedures;

import net.m3tte.tactical_imbuements.EpicFight.ImbuementAnims;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsMobEffects;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Comparator;

public class FlaskImpact {
	public static void fireImpact(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			_level.sendParticles(ParticleTypes.CRIT, x, (y + 0.2), z, 5, 0.1, 0.1, 0.1, 0.2);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.GLASS_SHARD_PARTICLE.get()), x, (y + 1), z, 13, 0.1, 0.1, 0.1, 0.25);
			_level.sendParticles(ParticleTypes.FLAME, x, (y + 0.2), z, 20, 0.1, 0.1, 0.1, 0.2);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FLAME_PARTICLE.get()), x, (y + 1), z, 30, 0.1, 0.1, 0.1, 0.2);
			_level.sendParticles(ParticleTypes.LAVA, x, (y + 0.2), z, 6, 0.1, 0.1, 0.1, 0.2);
		}

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y + 1, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.NEUTRAL, 1, 1);
				_level.playSound(null, BlockPos.containing(x, y + 1, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
			}
		}
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
			for (Entity entityiterator : _entfound) {
				entityiterator.setSecondsOnFire(15);
				entityiterator.hurt(entityiterator.damageSources().onFire(), 4);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FLAME_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 10, 0.2, 0.4, 0.2, 0.2);
			}
		}
	}

	public static void venomImpact(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			_level.sendParticles(ParticleTypes.CRIT, x, (y + 0.2), z, 5, 0.1, 0.1, 0.1, 0.2);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.GLASS_SHARD_PARTICLE.get()), x, (y + 0.2), z, 13, 0.1, 0.1, 0.1, 0.25);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), x, (y + 0.2), z, 50, 0.1, 0.1, 0.1, 0.2);
		}

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y + 0.2, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.NEUTRAL, 1, 1);
				_level.playSound(null, BlockPos.containing(x, y + 0.2, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
			}
		}
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof LivingEntity livingEntity) {
					livingEntity.hurt(livingEntity.damageSources().magic(), 2);
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 10, 0.2, 0.4, 0.2, 0.2);
					livingEntity.addEffect(new MobEffectInstance(TacticalImbuementsMobEffects.DECAYING.get(), 200, 1 ));
					livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 1 ));
				}

			}
		}
	}

	public static void tryIncreaseFreeze(int potencyMod, float amount, LivingEntity victim) {

		Level level = victim.level();
		if (victim.hasEffect(TacticalImbuementsMobEffects.CHILLED.get())) {

			int potency = victim.getEffect(TacticalImbuementsMobEffects.CHILLED.get()).getAmplifier() + potencyMod;
			if (potency <= 8) {
				victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int)(amount * 9), (int)((potency + 1) / 3)));
				victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,  (int)(amount * 9), (int)((potency + 1) / 3)));
				victim.addEffect(new MobEffectInstance(TacticalImbuementsMobEffects.CHILLED.get(),  (int)(amount * 9), potency));
				if (!level.isClientSide())
					level.playSound(null, BlockPos.containing(victim.getX(), victim.getY(), victim.getZ()), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1, 1);

			} else {
				victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int)(amount * 8),3));
				victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int)(amount * 8),3));
				victim.addEffect(new MobEffectInstance(TacticalImbuementsMobEffects.CHILLED.get(), (int)(amount * 8),8));
			}
		} else {

			level.playSound(null, BlockPos.containing(victim.getX(), victim.getY(), victim.getZ()), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 0.3f, 1.3f);
			victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, (int)(amount * 8), 0));
			victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int)(amount * 8), 0));
			victim.addEffect(new MobEffectInstance(TacticalImbuementsMobEffects.CHILLED.get(), (int)(amount * 8), 0), victim);
			System.out.println(victim.getEffect(TacticalImbuementsMobEffects.CHILLED.get()).getDuration());

		}
	}

	public static void freezeImpact(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			_level.sendParticles(ParticleTypes.CRIT, x, (y + 0.2), z, 5, 0.1, 0.1, 0.1, 0.2);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.GLASS_SHARD_PARTICLE.get()), x, (y + 0.2), z, 13, 0.1, 0.1, 0.1, 0.25);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FREEZE_PARTICLE.get()), x, (y + 0.2), z, 50, 0.1, 0.1, 0.1, 0.2);
		}

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y + 0.2, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.NEUTRAL, 1, 1);
				_level.playSound(null, BlockPos.containing(x, y + 0.2, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
			}
		}
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof LivingEntity livingEntity) {
					livingEntity.hurt(livingEntity.damageSources().freeze(), 2);
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FREEZE_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 10, 0.2, 0.4, 0.2, 0.2);
					tryIncreaseFreeze(3, 50, livingEntity);
				}

			}
		}
	}

	public static void sparkImpact(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level) {
			_level.sendParticles(ParticleTypes.CRIT, x, (y + 0.2), z, 5, 0.1, 0.1, 0.1, 0.2);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.GLASS_SHARD_PARTICLE.get()), x, (y + 0.2), z, 13, 0.1, 0.1, 0.1, 0.25);
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get()), x, (y + 0.2), z, 50, 0.1, 0.1, 0.1, 0.2);
		}

		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y + 0.2, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.NEUTRAL, 1, 1);
				_level.playSound(null, BlockPos.containing(x, y + 0.2, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
			}
		}
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).collect(Collectors.toList());
			for (Entity entityiterator : _entfound) {
				System.out.println("Entity: "+entityiterator);
				if (entityiterator instanceof LivingEntity living) {
					System.out.println("LivingEntity: "+entityiterator);
					living.hurt(living.damageSources().lightningBolt(), 2);

					EntityPatch<?> entitypatch = living.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

					if (entitypatch instanceof LivingEntityPatch<?> livingPatch) {
						if (livingPatch.getArmature() instanceof HumanoidArmature && !(entityiterator.level().isClientSide())) {
							livingPatch.playAnimationSynchronized(ImbuementAnims.ZAP_HEAVY, 0);
						}
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get()), (entityiterator.getX()), (entityiterator.getY() + 1), (entityiterator.getZ()), 10, 0.2, 0.4, 0.2, 0.2);
				}






            }
		}
	}
}
