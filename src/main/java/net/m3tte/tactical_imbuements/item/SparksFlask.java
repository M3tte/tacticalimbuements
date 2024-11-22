
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

public class SparksFlask extends Item {
	public SparksFlask() {
		super(new Properties().stacksTo(4).rarity(Rarity.UNCOMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("[On Use] ").withStyle(ChatFormatting.YELLOW).append(Component.literal("Applies electricity to your weapon for 30 seconds.").withStyle(ChatFormatting.GRAY)));
		list.add(Component.literal("[On Impact] ").withStyle(ChatFormatting.YELLOW).append(Component.literal("Zap nearby enemies").withStyle(ChatFormatting.GRAY)));
		list.add(Component.literal("[Sparks] ").withStyle(ChatFormatting.DARK_AQUA).append(Component.literal("Parries stun, part of damage bypasses armor.").withStyle(ChatFormatting.AQUA)));
		list.add(Component.literal("The dance of lightning is one not many individuals can claim to survive. None shall prevail against your lightning fast strikes.").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);

		UseImbueFlasks.useImbuementFlask(world, ImbuementDefinitions.SPARKID, entity, hand);
		return ar;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		UseImbueFlasks.useImbuementFlask(context.getLevel(), ImbuementDefinitions.SPARKID, context.getPlayer(), context.getHand());
		return InteractionResult.SUCCESS;
	}
}
