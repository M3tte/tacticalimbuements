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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.entity.eventlistener.PlayerEvent;

import static yesman.epicfight.api.animation.property.AnimationEvent.*;

public class ImbuementAnims {


    public static StaticAnimation ZAP;
    public static StaticAnimation ZAP_HEAVY;

    public static StaticAnimation APPLY_IMBUEMENT;

    public static StaticAnimation THROW_FLASK;

    public static StaticAnimation FIRE_BLAST;
    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put("tactical_imbuements", ImbuementAnims::build);
    }


    private static void build() {

        HumanoidArmature biped = Armatures.BIPED;

        ZAP = (new LongHitAnimation(0.1f, "biped/zap", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.EVENTS, shockParticle());

        ZAP_HEAVY = (new LongHitAnimation(0.1f, "biped/zap_long", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.EVENTS, shockParticle());

        APPLY_IMBUEMENT = (new ActionAnimation(0.1f, "biped/apply_imbuement", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, imbuementEngage());

        THROW_FLASK = (new ActionAnimation(0.05f, "biped/throw", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, true);

        FIRE_BLAST = (new LongHitAnimation(0.05f, "biped/fire_blast", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.AFFECT_SPEED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, fractureParticle());






    }


    private static boolean hasImbueable(Player p) {
        return (!(p.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && (p.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())));
    }

    private static TimeStampedEvent[] imbuementEngage() {
        TimeStampedEvent[] arr = new TimeStampedEvent[1];


        arr[0] = TimeStampedEvent.create(4, (entitypatch, animation, params) -> {

            if (entitypatch.getOriginal() instanceof Player p && hasImbueable(p)) {
                UseImbueFlasks.calculateImbuementUsage(p.level(), p);
            }

        }, Side.SERVER);

        return arr;
    }

    private static TimeStampedEvent[] shockParticle() {
        TimeStampedEvent[] arr = new TimeStampedEvent[1];


        arr[0] = TimeStampedEvent.create(4, (entitypatch, animation, params) -> {
            LivingEntity entity = entitypatch.getOriginal();
            if (entitypatch.getOriginal().level() instanceof ServerLevel _level)
                _level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get()), (entity.getX()), (entity.getY() + entity.getBbHeight() / 2), (entity.getZ()), 1, 0.2, 0.4, 0.2, 0);

        }, Side.BOTH);

        return arr;
    }

    private static AnimationEvent[] fractureParticle() {
        AnimationEvent[] arr = new AnimationEvent[1];


        arr[0] = AnimationEvent.create((entitypatch, animation, params) -> {
            if (entitypatch.getOriginal().level() instanceof ServerLevel _level)
                _level.sendParticles((SimpleParticleType) (ParticleTypes.LARGE_SMOKE), (entitypatch.getOriginal().getX()), (entitypatch.getOriginal().getY() + entitypatch.getOriginal().getBbHeight() / 2), (entitypatch.getOriginal().getZ()), 20, 0.2, 0.4, 0.2, 0.3);


            LevelUtil.circleSlamFracture(entitypatch.getOriginal(), entitypatch.getOriginal().level(), entitypatch.getOriginal().position().add(new Vec3(0, -1, 0)), 4.0d, false, false, false);
        }, Side.BOTH);

        return arr;
    }


}
