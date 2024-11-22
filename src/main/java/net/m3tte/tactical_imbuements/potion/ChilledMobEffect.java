
package net.m3tte.tactical_imbuements.potion;

import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;

public class ChilledMobEffect extends MobEffect {
    public ChilledMobEffect() {
        super(MobEffectCategory.HARMFUL, -1);
    }

    @Override
    public String getDescriptionId() {
        return "effect.tactical_imbuements.chilled";
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (level instanceof ServerLevel svr) {
            svr.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.FREEZE_PARTICLE.get()), (entity.getX()), (entity.getY() + entity.getBbHeight()/2), (entity.getZ()), 1, 0.2, 0.4, 0.2, 0.01);
        }

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
