package com.github.wimpingego.nnow.objects.blocks;
import java.util.List;

import javax.annotation.Nullable;

import com.github.wimpingego.nnow.util.IExplosionResistant;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

/*
  @author: Naxanria
*/
public class SuperObsidian extends Block implements IExplosionResistant
{
  public SuperObsidian() {
      super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(5, 3600000.0F).harvestLevel(2)
    		  .harvestTool(ToolType.PICKAXE));
  }
  
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add((new TranslationTextComponent("item.nnow.witherproof.line1")));
	}
}
