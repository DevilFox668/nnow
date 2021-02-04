package com.github.wimpingego.nnow.objects.blocks.cakes;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockPastryBase extends Block {

    public BlockPastryBase(AbstractBlock.Properties builder) {
        super(builder);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
    }
}
