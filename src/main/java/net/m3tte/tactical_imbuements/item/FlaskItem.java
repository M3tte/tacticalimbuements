
package net.m3tte.tactical_imbuements.item;

import com.mojang.logging.LogUtils;
import net.m3tte.tactical_imbuements.procedures.UseEmptyFlaskProcedure;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class FlaskItem extends Item {
	public FlaskItem() {
		super(new Item.Properties().stacksTo(4).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
		if (!pLevel.isClientSide) {
			UseEmptyFlaskProcedure.execute(pPlayer, itemstack);
		}
		return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		LogUtils.getLogger().info("{}", context.getLevel());
		super.useOn(context);
		UseEmptyFlaskProcedure.execute(context.getPlayer(), context.getItemInHand());
		return InteractionResult.SUCCESS;
	}
}
