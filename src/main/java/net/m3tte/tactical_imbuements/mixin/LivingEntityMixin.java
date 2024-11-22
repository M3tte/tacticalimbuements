package net.m3tte.tactical_imbuements.mixin;

import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsMobEffects;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.m3tte.tactical_imbuements.procedures.FlaskImpact;
import net.m3tte.tactical_imbuements.procedures.UseImbueFlasks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    @ModifyVariable(at = @At(value = "HEAD"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0)
    public float modifyDamageMixin(float amount, DamageSource source) {

        if (source.getEntity() == source.getDirectEntity()) {

            if (!(source.getEntity() instanceof LivingEntity attacker) || !((Object) this instanceof LivingEntity victim))
                return amount;

            LinkedList<String> imbuements = UseImbueFlasks.getImbuements(attacker);

            if (imbuements.contains(ImbuementDefinitions.SPARKID)) {

                Level level = victim.level();
                victim.setHealth(victim.getHealth() - amount * 0.2f);

                if (!level.isClientSide())
                    level.playSound(null, BlockPos.containing(victim.getX(), victim.getY(), victim.getZ()), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1, 1);
                else
                    level.playLocalSound(victim.getX(), victim.getY(), victim.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 1, 1, false);

                if (level instanceof ServerLevel serverLevel)
                    serverLevel.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get()), victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 7, 0.2, 0.2, 0.2, 0.25);

                return amount - amount * 0.2f;
            }

        }

        return amount;
    }


    @Inject(at = @At(value = "TAIL"), method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", cancellable = true)
    public void hurtMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cbk) {

        if (source.getEntity() == source.getDirectEntity()) {
            if (source.getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) source.getEntity();
                LivingEntity victim = ((LivingEntity)(Object)this);
                Level level = victim.level();
                LinkedList<String> imbuements = UseImbueFlasks.getImbuements(attacker);

                if (imbuements.contains(ImbuementDefinitions.FLAMEID)) {
                    if (!victim.isOnFire())
                        if (!level.isClientSide())
                            level.playSound(null, BlockPos.containing(victim.getX(), victim.getY(), victim.getZ()), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1, 1);
                        else
                            level.playLocalSound(victim.getX(), victim.getY(), victim.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1, 1, false);

                    victim.setSecondsOnFire((int)amount / 2);
                    if (level instanceof ServerLevel serverLevel)
                        serverLevel.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FLAME_PARTICLE.get()), victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 7, 0.2, 0.2, 0.2, 0.25);
                }

                if (imbuements.contains(ImbuementDefinitions.VENOMID)) {
                    if (!victim.hasEffect(MobEffects.POISON))
                        if (!level.isClientSide())
                            level.playSound(null, BlockPos.containing(victim.getX(), victim.getY(), victim.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1);
                        else
                            level.playLocalSound(victim.getX(), victim.getY(), victim.getZ(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.firecharge.use")), SoundSource.NEUTRAL, 1, 1, false);

                    victim.addEffect(new MobEffectInstance(MobEffects.POISON, (int)(amount * 12), 1));

                    if (victim.hasEffect(TacticalImbuementsMobEffects.DECAYING.get())) {
                        if (victim.getEffect(TacticalImbuementsMobEffects.DECAYING.get()).getAmplifier() > 0) {
                            victim.addEffect(new MobEffectInstance(TacticalImbuementsMobEffects.DECAYING.get(), 20, 0));
                        }
                    } else {
                        victim.addEffect(new MobEffectInstance(TacticalImbuementsMobEffects.DECAYING.get(), 20, 0));
                    }


                    if (level instanceof ServerLevel serverLevel)
                        serverLevel.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), victim.getX(), victim.getY() + victim.getBbHeight() / 2, victim.getZ(), 7, 0.2, 0.2, 0.2, 0.25);
                }

                if (imbuements.contains(ImbuementDefinitions.FREEZEID) && !level.isClientSide) {
                    FlaskImpact.tryIncreaseFreeze(1, amount, victim);
                }
            }
        }
    }



}
