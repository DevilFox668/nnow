package com.github.wimpingego.nnow.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public class AGBlock extends Block {

	public AGBlock(Properties properties) {
    		super(properties);
	}
	
	@Override
    public boolean canSustainPlant(BlockState state, IBlockReader blockReader, BlockPos pos, Direction direction, IPlantable iPlantable) {
        final BlockPos plantPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        final PlantType plantType = iPlantable.getPlantType(blockReader, plantPos);

        switch (plantType) {
            case Desert: {
                return true;
            }
            case Water: {
                return blockReader.getBlockState(pos).getMaterial() == Material.WATER && blockReader.getBlockState(pos) == getDefaultState();
            }
            case Beach: {
                return ((blockReader.getBlockState(pos.east()).getMaterial() == Material.WATER || blockReader.getBlockState(pos.east()).has(BlockStateProperties.WATERLOGGED))
                        || (blockReader.getBlockState(pos.west()).getMaterial() == Material.WATER || blockReader.getBlockState(pos.west()).has(BlockStateProperties.WATERLOGGED))
                        || (blockReader.getBlockState(pos.north()).getMaterial() == Material.WATER || blockReader.getBlockState(pos.north()).has(BlockStateProperties.WATERLOGGED))
                        || (blockReader.getBlockState(pos.south()).getMaterial() == Material.WATER || blockReader.getBlockState(pos.south()).has(BlockStateProperties.WATERLOGGED))
                        || (blockReader.getBlockState(pos.down()).getMaterial() == Material.WATER || blockReader.getBlockState(pos.down()).has(BlockStateProperties.WATERLOGGED)));
            }
		default:
			break;
        }
        return false;
    }

}
