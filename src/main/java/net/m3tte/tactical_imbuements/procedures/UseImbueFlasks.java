package net.m3tte.tactical_imbuements.procedures;

import net.m3tte.tactical_imbuements.EpicFight.ImbuementAnims;
import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.m3tte.tactical_imbuements.entity.*;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModEntities;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.LinkedList;

public class UseImbueFlasks {

	private static void throwFlask(AbstractArrow arrow, Player player, Level world) {
		//player.getCooldowns().addCooldown(itemStack.getItem(), 20);
		//itemStack.shrink(1);

		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		entitypatch.playAnimationSynchronized(ImbuementAnims.THROW_FLASK, 0);


		if (!world.isClientSide()) {
			world.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1, 1);
		}
		Projectile _entityToSpawn = new Object() {
			public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {;
				arrow.setOwner(shooter);
				arrow.setBaseDamage(damage);
				arrow.setKnockback(knockback);
				arrow.setSilent(true);
				return arrow;
			}
		}.getArrow(world, player, 8, 1);
		_entityToSpawn.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
		_entityToSpawn.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, 1, (float) 0.1);
		world.addFreshEntity(_entityToSpawn);

	}

	private static void applyImbuement(Level world, Player player, String imbueType, int time, String applySound, ItemStack potion, ItemStack weapon) {

		weapon.getOrCreateTag().putString("imbueType", imbueType);
		Player clplayer = player;
		/*if (world.isClientSide()) {
			clplayer = Minecraft.getInstance().player;
		}*/

		weapon.getOrCreateTag().putDouble("imbueCounter", clplayer.tickCount + TickProcedure.defaultMaxTime);

		weapon.getOrCreateTag().putDouble("maxImbueTime", TickProcedure.defaultMaxTime);
		potion.shrink(1);
		player.addItem(TacticalImbuementsModItems.FLASK.get().getDefaultInstance());
		world.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(applySound)), SoundSource.NEUTRAL, 1, 1);
	}

	public static void useImbuementFlask(Level world, String flaskType, Player player, InteractionHand useHand) {

		ItemStack itemStack = player.getItemInHand(useHand);
		InteractionHand weaponHand;
		if (useHand == InteractionHand.OFF_HAND)
			weaponHand = InteractionHand.MAIN_HAND;
		else
			weaponHand = InteractionHand.OFF_HAND;
		ItemStack weaponItem = player.getItemInHand(weaponHand);

		if (weaponItem.isEmpty() && !world.isClientSide()) {
			itemStack.shrink(1);
			player.getCooldowns().addCooldown(itemStack.getItem(), 40);
			AbstractArrow flask = null;
			switch (flaskType) {
				default:
				case ImbuementDefinitions.FLAMEID: flask = new FireflaskthrowableEntity(TacticalImbuementsModEntities.FIREFLASKTHROWABLE.get(), world); break;
				case ImbuementDefinitions.VENOMID: flask = new VenomFlaskThrowableEntity(TacticalImbuementsModEntities.VENOMFLASKTHROWABLE.get(), world); break;
				case ImbuementDefinitions.FREEZEID: flask = new FreezeFlaskThrowableEntity(TacticalImbuementsModEntities.FREEZEFLASKTHROWABLE.get(), world); break;
				case ImbuementDefinitions.SPARKID: flask = new SparkFlaskThrowableEntity(TacticalImbuementsModEntities.SPARKFLASKTHROWABLE.get(), world); break;
			}
			throwFlask(flask, player, world);
		} else if (weaponItem.is(ItemTags.create(new ResourceLocation("tactical_imbuements:applicable")))) {

			player.getCooldowns().addCooldown(itemStack.getItem(), 200);
			LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			entitypatch.playAnimationSynchronized(ImbuementAnims.APPLY_IMBUEMENT, 0);
		}

	}

	public static void calculateImbuementUsage(Level world, Player player) {

		InteractionHand weaponHand;
		InteractionHand flaskHand;
		if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.create(new ResourceLocation("tactical_imbuements:applicable")))) {
			weaponHand = InteractionHand.MAIN_HAND;
			flaskHand = InteractionHand.OFF_HAND;
		} else if (player.getItemInHand(InteractionHand.OFF_HAND).is(ItemTags.create(new ResourceLocation("tactical_imbuements:applicable")))) {
			weaponHand = InteractionHand.OFF_HAND;
			flaskHand = InteractionHand.MAIN_HAND;
		} else {
			return;
		}
		ItemStack weaponItem = player.getItemInHand(weaponHand);
		ItemStack itemStack = player.getItemInHand(flaskHand);
		ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(itemStack.getItem());

		if (resourceLocation != null) {
			switch (resourceLocation.getPath()) {
				default:
				case "flask_of_flame":
					applyImbuement(world, player, ImbuementDefinitions.FLAMEID, TickProcedure.defaultMaxTime, "item.firecharge.use", itemStack, weaponItem);
					break;
				case "flask_of_venom":
					applyImbuement(world, player, ImbuementDefinitions.VENOMID, TickProcedure.defaultMaxTime, "item.firecharge.use", itemStack, weaponItem);
					break;
				case "flask_of_ice":
					applyImbuement(world, player, ImbuementDefinitions.FREEZEID, TickProcedure.defaultMaxTime, "item.firecharge.use", itemStack, weaponItem);
					break;
				case "flask_of_sparks":
					applyImbuement(world, player, ImbuementDefinitions.SPARKID, TickProcedure.defaultMaxTime, "item.firecharge.use", itemStack, weaponItem);
					break;
			}
		}
	}

	public static LinkedList<String> getImbuements(LivingEntity entity) {
		LinkedList<String> imbuements = new LinkedList<>();

		if (entity != null) {
			imbuements.add(entity.getMainHandItem().getOrCreateTag().getString("imbueType"));
			imbuements.add(entity.getOffhandItem().getOrCreateTag().getString("imbueType"));
		}

		return imbuements;
	}
}
