package net.m3tte.tactical_imbuements.procedures;

import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Random;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class TickProcedure {




	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		LivingEntity living = event.getEntity();
		execute(event, living.level(), living.getX(), living.getY(), living.getZ(), living);
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}


	public static int defaultMaxTime = 600;

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {


		if (!(entity instanceof LivingEntity)) {
			System.out.println(entity.getClass());
		}





		if (!(entity instanceof LivingEntity))
			return;

		LivingEntity living = (LivingEntity) entity;
		//System.out.println(living.getMainHandItem().getOrCreateTag().getString("imbueType") + (living.getMainHandItem().getOrCreateTag().getDouble("imbueCounter") - entity.tickCount));
		oncePerHand((hand) -> {
			ItemStack handItem = living.getItemInHand(hand);

			if (!handItem.is(ItemTags.create(new ResourceLocation("tactical_imbuements:applicable"))))
				return;

			double timeout = 0;

			Entity clplayer = entity;
			/*if (world.isClientSide() && clplayer instanceof Player) {
				clplayer = Minecraft.getInstance().player;
			}*/

			timeout += handItem.getOrCreateTag().getDouble("imbueCounter") - clplayer.tickCount;

			if (timeout > (defaultMaxTime + 60) && !world.isClientSide())
				clearImbuement(living, hand);

			if (handItem.getTag() == null)
				return;

			String imbueType = handItem.getTag().getString("imbueType");

			if (timeout > 1 && timeout < (defaultMaxTime + 60)) {
				if (imbueType.length() > 2)
					particleImbueEffectCheck(living, hand, imbueType);
			} else if ((timeout > 0 || imbueType.length() > 2) && !world.isClientSide()) {
				clearImbuement(living, hand);
			}



		});


	}


	private static void oncePerHand(Consumer<InteractionHand> c) {
		c.accept(InteractionHand.MAIN_HAND);
		c.accept(InteractionHand.OFF_HAND);
	}


	private static void clearImbuement(LivingEntity entity, InteractionHand hand) {
		System.out.println("Clearing imbuement on, clientside?"+entity.level().isClientSide());
		ItemStack item = entity.getItemInHand(hand);

		item.removeTagKey("imbueCounter");
		item.removeTagKey("maxImbueTime");
		item.removeTagKey("imbueType");
	}


	private static LinkedList<Vec3> offsetCalculator(ItemStack item) {

		int length = 3;

		if (item.is(ItemTags.create(new ResourceLocation("tactical_imbuements:length_medium")))) {
			length = 4;
		}

		if (item.is(ItemTags.create(new ResourceLocation("tactical_imbuements:length_long")))) {
			length = 5;
		}

		LinkedList<Vec3> offsets = new LinkedList<>();

		for (int i = 1; i < length + 1; i++) {
			offsets.add(new Vec3(0, -0.02 * i, -0.3 * i));
		}
		//Vec3[] offsets = {new Vec3(0, -0.05, -0.95), new Vec3(0, -0.05, -0.65), new Vec3(0, -0.05, -0.4)};

		return offsets;
	}


	private static void doArmatureParticle(Joint j, LivingEntityPatch<?> entityPatch, SimpleParticleType particle, float amount, Vec3 particleSpeed, LinkedList<Vec3> offsets) {

//		Pose currentPose = entityPatch.getArmature().getCurrentPose();
		Level l = entityPatch.getOriginal().level();
		RandomSource r = l.random;
		for (float i = 1; i <= 9; i += 2f) {
			Pose middlePose = entityPatch.getAnimator().getPose((i + r.nextInt(3) - 1) / 10);
			Vec3 posMid = entityPatch.getOriginal().getPosition((i + r.nextInt(3) - 1) / 10);

			OpenMatrix4f middleModelTf = OpenMatrix4f.createTranslation((float)posMid.x, (float)posMid.y, (float)posMid.z)
					.mulBack(OpenMatrix4f.createRotatorDeg(180.0F, Vec3f.Y_AXIS)
							.mulBack(entityPatch.getModelMatrix((float) (i + r.nextInt(3) - 1) / 10F)));

			OpenMatrix4f middleJointTf = entityPatch.getArmature().getBindedTransformFor(middlePose, j).mulFront(middleModelTf);



			for (Vec3 modifier : offsets) {
				if (r.nextInt((int)(100 / amount)) < 40) {
					modifier = modifier.add(r.nextFloat() * 0.2f - 0.1, r.nextFloat() * 0.2f - 0.1, r.nextFloat() * 0.2f - 0.1);
					Vec3 particlePos = OpenMatrix4f.transform(middleJointTf, modifier);
					l.addParticle(particle, particlePos.x, particlePos.y, particlePos.z, particleSpeed.x, particleSpeed.y, particleSpeed.z);
				}

			}
		}
	}

	private static void particleImbueEffectCheck(LivingEntity entity, InteractionHand hand, String imbueType) {

		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

		if (entitypatch == null)
			return;


		if (entitypatch.getValidItemInHand(hand) == null)
			return;

		Joint toolL = entitypatch.getArmature().searchJointByName("Tool_L");
		Joint toolR = entitypatch.getArmature().searchJointByName("Tool_R");

		if (hand == InteractionHand.MAIN_HAND) {
			if (toolR != null && imbueType.length() > 2)
				doArmatureParticle(toolR, entitypatch, trailParticleGetter(imbueType), particleAmount((imbueType)), particleSpeed(imbueType), offsetCalculator(entity.getItemInHand(hand)));
		}
		else
			if (toolL != null && imbueType.length() > 2)
				doArmatureParticle(toolL, entitypatch, trailParticleGetter(imbueType), particleAmount((imbueType)), particleSpeed(imbueType), offsetCalculator(entity.getItemInHand(hand)));
	}

	private static SimpleParticleType trailParticleGetter(String type) {

		switch (type) {
			case ImbuementDefinitions.FLAMEID -> {
				return ImbuementDefinitions.FLAME.getEmitParticle();
			}
			case ImbuementDefinitions.VENOMID -> {
				return ImbuementDefinitions.VENOM.getEmitParticle();
			}
			case ImbuementDefinitions.SPARKID -> {
				return ImbuementDefinitions.SPARK.getEmitParticle();
			}
			case ImbuementDefinitions.FREEZEID -> {
				return ImbuementDefinitions.FREEZE.getEmitParticle();
			}
		}

		return null;
	}

	private static Vec3 particleSpeed(String type) {
		Random r = new Random();
		switch (type) {
			case ImbuementDefinitions.SPARKID -> {
				return new Vec3(0.02 * inverter(r),0.02 * inverter(r),0.02 * inverter(r));
			}
		}


		return new Vec3(0,0,0);
	}

	private static float inverter(Random r) {
		return (r.nextFloat() * 2) - 1;
	}

	private static float particleAmount(String type) {
		switch (type) {
			case ImbuementDefinitions.VENOMID, ImbuementDefinitions.FREEZEID -> {
				return 0.3f;
			}
            case ImbuementDefinitions.SPARKID -> {
				return 0.6f;
			}
		}
		return 1;
	}
}
