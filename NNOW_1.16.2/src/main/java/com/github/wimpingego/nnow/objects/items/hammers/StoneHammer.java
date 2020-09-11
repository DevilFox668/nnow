package com.github.wimpingego.nnow.objects.items.hammers;

import com.github.wimpingego.nnow.util.config.ModConfigs;

import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class StoneHammer extends HammerBase {

	Double speed = ModConfigs.STONE_TIER_TOOL_SPEED_MODIFIER.get();
	static int use = ModConfigs.STONE_TIER_TOOL_DURABILITY_MODIFIER.get();

	public StoneHammer(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
		super(tier, attackDamageIn, attackSpeedIn, builder.maxDamage(use));
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return super.getDestroySpeed(stack, state) / speed.floatValue();
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.COBBLESTONE;
	}

}
