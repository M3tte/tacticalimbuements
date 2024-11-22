
package net.m3tte.tactical_imbuements.item;

import net.m3tte.tactical_imbuements.definitions.ImbuementDefinitions;
import net.m3tte.tactical_imbuements.procedures.UseImbueFlasks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class FlameFlaskItem extends Item {
	public FlameFlaskItem() {
		super(new Item.Properties().stacksTo(4).rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("[On Use] ").withStyle(ChatFormatting.YELLOW).append(Component.literal("Applies perpetual burn to your weapon for 30 seconds.").withStyle(ChatFormatting.GRAY)));
		list.add(Component.literal("[On Impact] ").withStyle(ChatFormatting.YELLOW).append(Component.literal("Create a fire explosion").withStyle(ChatFormatting.GRAY)));
		list.add(Component.literal("[Perpetual Burn] ").withStyle(ChatFormatting.DARK_RED).append(Component.literal("Light enemies on fire. If you run out of stamina while blocking create an explosion, pushing back enemies.").withStyle(ChatFormatting.RED)));
		list.add(Component.literal("A well oiled machine never stops, and neither will you. Unleashing a never ending crimson flame inside your soul, the execution of your relentless slashes shall never falter.").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);

		UseImbueFlasks.useImbuementFlask(world, ImbuementDefinitions.FLAMEID, entity, hand);
		return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		UseImbueFlasks.useImbuementFlask(context.getLevel(), ImbuementDefinitions.FLAMEID, context.getPlayer(), context.getHand());
		return InteractionResult.SUCCESS;
	}
}
