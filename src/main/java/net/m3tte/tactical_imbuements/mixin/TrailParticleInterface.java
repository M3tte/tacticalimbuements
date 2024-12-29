package net.m3tte.tactical_imbuements.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.client.particle.TrailParticle;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.SortedMap;
@OnlyIn(Dist.CLIENT)
@Mixin(value = TrailParticle.class, remap = false)
public interface TrailParticleInterface {

    @Accessor("joint")
    Joint getJoint();

    @Accessor("trailInfo")
    TrailInfo getTrailInfo();

    @Accessor("animation")
    StaticAnimation getAnimation();

    @Accessor("entitypatch")
    LivingEntityPatch<?> getEntitypatch();

}
