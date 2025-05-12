package net.m3tte.tactical_imbuements.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

@Mixin(value = AnimationTrailParticle.class, remap = false)
public interface AnimationTrailParticleAccessor {
    @Accessor("animation")
    AssetAccessor<? extends StaticAnimation> getAnimation();
}
