
package net.m3tte.tactical_imbuements.potion;

import net.m3tte.tactical_imbuements.init.TacticalImbuementsMobEffects;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.core.jmx.Server;
import org.lwjgl.system.CallbackI;

import java.util.Random;

public class DecayingMobEffect extends MobEffect {
    public DecayingMobEffect() {
        super(MobEffectCategory.HARMFUL, -1);
    }

    @Override
    public String getDescriptionId() {
        return "effect.tactical_imbuements.decaying";
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        Level level = entity.level();
        RandomSource r = entity.getRandom();
        ServerPlayer pl = null;
        if (entity instanceof ServerPlayer _pl) {
            pl = _pl;
        }

        if (entity.getEffect(TacticalImbuementsMobEffects.DECAYING.get()).getDuration() % 2 == 1) {
            for (ItemStack i : entity.getArmorSlots()) {
                i.hurt(amplifier + 1, r, pl);
                if (i.getDamageValue() > i.getMaxDamage()) {
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(entity.getX(), entity.getY() + entity.getBbHeight()/2, entity.getZ()), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 1, (float) 1);
                        level.playSound(null, BlockPos.containing(entity.getX(), entity.getY() + entity.getBbHeight()/2, entity.getZ()), SoundEvents.AXE_WAX_OFF, SoundSource.NEUTRAL, 1, (float) 1);
                        ((ServerLevel) level).sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), (entity.getX()), (entity.getY() + 1), (entity.getZ()), 10, 0.2, 0.4, 0.2, 0);
                    }
                    i.shrink(1);
                }
                if (amplifier == 0) {
                    if (level instanceof ServerLevel svr)
                        svr.sendParticles(ParticleTypes.CRIT, (entity.getX()), (entity.getY() + entity.getBbHeight()/2), (entity.getZ()), 1, 0.3, 0.5, 0.3, 0.01);
                } else {
                    if (level instanceof ServerLevel svr)
                        svr.sendParticles(ParticleTypes.ENCHANTED_HIT, (entity.getX()), (entity.getY() + entity.getBbHeight()/2), (entity.getZ()), 1, 0.3, 0.5, 0.3, 0.01);
                }

            }
        }

        if (level instanceof ServerLevel svr) {
            svr.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get()), (entity.getX()), (entity.getY() + entity.getBbHeight()/2), (entity.getZ()), 1, 0.2, 0.4, 0.2, 0.01);
        }

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
