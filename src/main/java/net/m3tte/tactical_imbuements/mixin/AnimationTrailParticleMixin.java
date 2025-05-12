package net.m3tte.tactical_imbuements.mixin;

import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationTrailParticle.class, remap = false)
public class AnimationTrailParticleMixin {
    @Inject(
            method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/Joint;Lyesman/epicfight/api/asset/AssetAccessor;Lyesman/epicfight/api/client/animation/property/TrailInfo;)V",
            at = @At("TAIL")
    )
    private void injectTrailColor(
            ClientLevel level,
            LivingEntityPatch<?> entitypatch,
            Joint joint,
            AssetAccessor<? extends StaticAnimation> animation,
            TrailInfo trailInfo,
            CallbackInfo ci
    ) {
        ItemStack item = entitypatch.getValidItemInHand(trailInfo.hand());
        if (item.getTag() == null) return;

        String type = item.getOrCreateTag().getString("imbueType");
        AnimationTrailParticle particle = (AnimationTrailParticle)(Object)this;

        switch (type) {
            case ImbuementDefinitions.FLAMEID -> particle.setColor(
                    ImbuementDefinitions.FLAME.getrCol(),
                    ImbuementDefinitions.FLAME.getgCol(),
                    ImbuementDefinitions.FLAME.getbCol()
            );
            case ImbuementDefinitions.VENOMID -> particle.setColor(
                    ImbuementDefinitions.VENOM.getrCol(),
                    ImbuementDefinitions.VENOM.getgCol(),
                    ImbuementDefinitions.VENOM.getbCol()
            );
            case ImbuementDefinitions.FREEZEID -> particle.setColor(
                    ImbuementDefinitions.FREEZE.getrCol(),
                    ImbuementDefinitions.FREEZE.getgCol(),
                    ImbuementDefinitions.FREEZE.getbCol()
            );
            case ImbuementDefinitions.SPARKID -> particle.setColor(
                    ImbuementDefinitions.SPARK.getrCol(),
                    ImbuementDefinitions.SPARK.getgCol(),
                    ImbuementDefinitions.SPARK.getbCol()
            );
        }
    }
}
