
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.m3tte.tactical_imbuements.init;

import net.m3tte.tactical_imbuements.client.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TacticalImbuementsModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		Minecraft.getInstance().particleEngine.register((SimpleParticleType) TacticalImbuementsModParticleTypes.GLASS_SHARD_PARTICLE.get(), GlassshardparticleParticle::provider);
		Minecraft.getInstance().particleEngine.register((SimpleParticleType) TacticalImbuementsModParticleTypes.FLAME_PARTICLE.get(), FlameparticleParticle::provider);
		Minecraft.getInstance().particleEngine.register((SimpleParticleType) TacticalImbuementsModParticleTypes.VENOM_PARTICLE.get(), VenomParticle::provider);
		Minecraft.getInstance().particleEngine.register((SimpleParticleType) TacticalImbuementsModParticleTypes.SPARK_PARTICLE.get(), SparkParticle::provider);
		Minecraft.getInstance().particleEngine.register((SimpleParticleType) TacticalImbuementsModParticleTypes.FREEZE_PARTICLE.get(), FreezeParticle::provider);
	}
}
