package net.m3tte.tactical_imbuements.procedures;

import net.m3tte.tactical_imbuements.EpicFight.ImbuementAnims;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModItems;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModEntities;
import net.m3tte.tactical_imbuements.entity.EmptyflaskthrowableEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class UseEmptyFlaskProcedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity living) {

			if (living.getMainHandItem().getItem().equals(TacticalImbuementsModItems.FLASK.get())) {
				if (entity instanceof Player _player)
					_player.getCooldowns().addCooldown(itemstack.getItem(), 20);
				LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) living.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				entitypatch.playAnimationSynchronized(ImbuementAnims.THROW_FLASK, 0);

				Entity shooter = entity;
				Level level = shooter.level();
				if (!level.isClientSide()) {
					(itemstack).shrink(1);
					Projectile entityToSpawn = new EmptyflaskthrowableEntity(TacticalImbuementsModEntities.EMPTYFLASKTHROWABLE.get(), level);
					entityToSpawn.setOwner(shooter);
//					entityToSpawn.setBaseDamage(8);
//					entityToSpawn.setKnockback(1);
					entityToSpawn.setSilent(true);
					entityToSpawn.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
					entityToSpawn.shoot(shooter.getLookAngle().x, shooter.getLookAngle().y, shooter.getLookAngle().z, 1, (float) 0.1);
					level.addFreshEntity(entityToSpawn);
				}
			}
		}

	}
}
