package net.m3tte.tactical_imbuements.procedures;

import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

public class EmptyFlaskImpactProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CRIT, x, (y + 0.2), z, 5, 0.1, 0.1, 0.1, 0.2);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (TacticalImbuementsModParticleTypes.GLASS_SHARD_PARTICLE.get()), x, (y + 1), z, 13, 0.1, 0.1, 0.1, 0.15);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y + 1, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.NEUTRAL, 1, (float) 1.2);
			} else {
				_level.playLocalSound(x, (y + 1), z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.glass.break")), SoundSource.NEUTRAL, 1, (float) 1.2, false);
			}
		}
	}
}
