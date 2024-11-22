package net.m3tte.tactical_imbuements.mixin;

import net.m3tte.tactical_imbuements.EpicFight.ImbuementAnims;
import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.m3tte.tactical_imbuements.procedures.UseImbueFlasks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(GuardSkill.class)
public class ParryingSkillMixin {
    @Inject(at = @At(value = "HEAD"), method = "dealEvent", cancellable = true, remap=false)
    public void guardInject(PlayerPatch<?> playerpatch, HurtEvent.Pre event, boolean advanced, CallbackInfo cbk) {

        Player player = playerpatch.getOriginal();
        Level level = player.level();
        LinkedList<String> imbuements = UseImbueFlasks.getImbuements(player);
        if (event.isParried()) {
            if (imbuements.contains(ImbuementDefinitions.SPARKID) && event.getDamageSource().getDirectEntity() != null) {
                EntityPatch<?> attackerpatch = event.getDamageSource().getDirectEntity().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

                if (attackerpatch instanceof LivingEntityPatch<?> livingPatch) {
                    if (livingPatch.getArmature() instanceof HumanoidArmature && !(level.isClientSide())) {
                        livingPatch.playAnimationSynchronized(ImbuementAnims.ZAP, 0);

                        if (!level.isClientSide()) {
                            level.playSound(null, BlockPos.containing(player.getX(), player.getY() + 1, player.getZ()), SoundEvents.THORNS_HIT, SoundSource.NEUTRAL, 1, (float) 1.2);
                            ((ServerLevel) level).sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get()), (attackerpatch.getOriginal().getX()), (attackerpatch.getOriginal().getY() + 1), (attackerpatch.getOriginal().getZ()), 10, 0.2, 0.4, 0.2, 0.2);
                        }
                    }
                }
            }

            if (imbuements.contains(ImbuementDefinitions.VENOMID) && event.getDamageSource().getDirectEntity() != null) {

                if (event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker) {


                    LivingEntityPatch<?> attackerpatch = (LivingEntityPatch<?>) attacker.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

                    RandomSource r = attacker.getRandom();
                    oncePerHand((hand) -> {
                        ItemStack i = attacker.getItemInHand(hand);
                        if (i != null) {
                            i.hurt((int)(i.getMaxDamage() * 0.01f + 5), r, null);

                            if (i.getDamageValue() > i.getMaxDamage()) {
                                if (!level.isClientSide()) {
                                    level.playSound(null, BlockPos.containing(attacker.getX(), attacker.getY() + 1, attacker.getZ()), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 1, (float) 1);
                                    level.playSound(null, BlockPos.containing(attacker.getX(), attacker.getY() + 1, attacker.getZ()), SoundEvents.AXE_WAX_OFF, SoundSource.NEUTRAL, 1, (float) 1);
                                    ((ServerLevel) level).sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), (attackerpatch.getOriginal().getX()), (attackerpatch.getOriginal().getY() + 1), (attackerpatch.getOriginal().getZ()), 10, 0.2, 0.4, 0.2, 0);
                                }
                                i.shrink(1);
                            }

                            if (!level.isClientSide()) {
                                level.playSound(null, BlockPos.containing(attacker.getX(), attacker.getY() + 1, attacker.getZ()), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 1, (float) 1.6);
                                ((ServerLevel) level).sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), (attackerpatch.getOriginal().getX()), (attackerpatch.getOriginal().getY() + 1), (attackerpatch.getOriginal().getZ()), 10, 0.2, 0.4, 0.2, 0.2);
                            }
                        }
                    });

                }
            }
        }
        if (imbuements.contains(ImbuementDefinitions.FLAMEID)) {
            if (!playerpatch.hasStamina(0.0f) && !event.isParried()) {
                oncePerHand((hand) -> {

                    ItemStack item = player.getItemInHand(hand);

                    if (item.getTag() == null)
                        return;

                    if (item.getOrCreateTag().getString("imbueType").equals(ImbuementDefinitions.FLAMEID)) {
                        item.getOrCreateTag().putDouble("imbueCounter", player.tickCount);
                        if (player instanceof ServerPlayer serverPlayer)
                            item.hurt(60, serverPlayer.getRandom(), serverPlayer);
                    }
                });

                flameKnockdown(playerpatch.getOriginal(), playerpatch);



            }
        }
    }
    private static void oncePerHand(Consumer<InteractionHand> c) {
            c.accept(InteractionHand.MAIN_HAND);
            c.accept(InteractionHand.OFF_HAND);
    }
    private void flameKnockdown(LivingEntity ignore, LivingEntityPatch<?> patch) {
        Level level = ignore.level();
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            serverLevel.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FLAME_PARTICLE.get()), (ignore.getX()), (ignore.getY() + 1), (ignore.getZ()), 50, 0.2, 0.4, 0.2, 1);
            serverLevel.sendParticles((ParticleTypes.FLAME), (ignore.getX()), (ignore.getY() + 1), (ignore.getZ()), 30, 0.2, 0.4, 0.2, 1);
            serverLevel.playSound(null, BlockPos.containing(ignore.getX(), ignore.getY() + 1, ignore.getZ()), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.NEUTRAL, 1, (float) 1.2);
            patch.playAnimationSynchronized(ImbuementAnims.FIRE_BLAST, 0);

            List<Entity> _entfound = serverLevel.getEntitiesOfClass(Entity.class, new AABB(ignore.position(), ignore.position()).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(ignore.position()))).collect(Collectors.toList());
            for (Entity entityiterator : _entfound) {
                if (entityiterator instanceof LivingEntity living) {
                    LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) living.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

                    if (entitypatch != null && !(entitypatch.equals(patch))) {
                        living.hurt(living.damageSources().explosion(ignore, ignore), 6);

                        if (entitypatch.getArmature() instanceof HumanoidArmature && !(level.isClientSide())) {
                            entitypatch.knockBackEntity(ignore.position(), 3);
                            entitypatch.applyStun(StunType.KNOCKDOWN, 5);
                        }
                    }
                }
            }


        }

    }
}
