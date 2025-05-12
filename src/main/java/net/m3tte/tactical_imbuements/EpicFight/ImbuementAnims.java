package net.m3tte.tactical_imbuements.EpicFight;

import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.m3tte.tactical_imbuements.procedures.UseImbueFlasks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.gameasset.Armatures;

import java.util.List;

public class ImbuementAnims {
    public static AnimationAccessor<LongHitAnimation> ZAP;
    public static AnimationAccessor<LongHitAnimation> ZAP_HEAVY;
    public static AnimationAccessor<ActionAnimation> APPLY_IMBUEMENT;
    public static AnimationAccessor<ActionAnimation> THROW_FLASK;
    public static AnimationAccessor<LongHitAnimation> FIRE_BLAST;

    @SuppressWarnings("unchecked")
    public static void registerAnimations(AnimationManager.AnimationBuilder builder) {
        AssetAccessor<? extends Armature> biped = (AssetAccessor<? extends Armature>) Armatures.BIPED;

        ZAP = builder.nextAccessor("biped/zap", acc -> new LongHitAnimation(0.1f, acc, biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(shockParticle()))
        );

        ZAP_HEAVY = builder.nextAccessor("biped/zap_heavy", acc -> new LongHitAnimation(0.1f, acc, biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(shockParticle()))
        );

        APPLY_IMBUEMENT = builder.nextAccessor("biped/apply_imbuement", acc -> new ActionAnimation(0.1f, acc, biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.TICK_EVENTS, List.of(imbuementEngage()))
        );

        THROW_FLASK = builder.nextAccessor("biped/throw_flask", acc -> new ActionAnimation(0.05f, acc, biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, true)
        );

        FIRE_BLAST = builder.nextAccessor("biped/fire_blast", acc -> new LongHitAnimation(0.05f, acc, biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, List.of(fractureParticle()))
        );
    }

    private static boolean hasImbueable(Player p) {
        return !p.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
                || !p.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
    }

    private static InTimeEvent<AnimationEvent.E0> imbuementEngage() {
        return InTimeEvent.create(4.0f, (entitypatch, animation, params) -> {
            if (entitypatch.getOriginal() instanceof Player p && hasImbueable(p)) {
                UseImbueFlasks.calculateImbuementUsage(p.level(), p);
            }
        }, Side.SERVER);
    }

    private static AnimationEvent.SimpleEvent<AnimationEvent.E0> shockParticle() {
        return AnimationEvent.SimpleEvent.create((entitypatch, animation, params) -> {
            LivingEntity entity = entitypatch.getOriginal();
            if (entity.level() instanceof ServerLevel level) {
                level.sendParticles(
                        (SimpleParticleType) TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get(),
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() / 2,
                        entity.getZ(),
                        1, 0.2, 0.4, 0.2, 0
                );
            }
        }, Side.BOTH);
    }

    private static AnimationEvent.SimpleEvent<AnimationEvent.E0> fractureParticle() {
        return AnimationEvent.SimpleEvent.create((entitypatch, animation, params) -> {
            LivingEntity entity = entitypatch.getOriginal();
            if (entity.level() instanceof ServerLevel level) {
                level.sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() / 2,
                        entity.getZ(),
                        20, 0.2, 0.4, 0.2, 0.3
                );
                LevelUtil.circleSlamFracture(entity, level, entity.position().add(new Vec3(0, -1, 0)), 4.0d, false, false, false);
            }
        }, Side.BOTH);
    }
}
