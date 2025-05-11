package net.m3tte.tactical_imbuements.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.client.particle.AbstractTrailParticle;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

@Mixin(value = AbstractTrailParticle.class, remap = false)
public interface AbstractTrailParticleAccessor {
    @Accessor("owner")
    EntityPatch getOwner();

    @Accessor("trailInfo")
    TrailInfo getTrailInfo();
}
