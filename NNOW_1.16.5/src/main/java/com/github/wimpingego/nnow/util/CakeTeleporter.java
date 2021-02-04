package com.github.wimpingego.nnow.util;


import java.util.function.Function;

import com.github.wimpingego.nnow.NNOW;
import com.github.wimpingego.nnow.Reference;
import com.github.wimpingego.nnow.util.config.ModConfigs;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.ModList;

public class CakeTeleporter implements ITeleporter {
    protected final ServerWorld world;

    public CakeTeleporter(ServerWorld worldIn) {
        this.world = worldIn;
    }

    @Override
    public Entity placeEntity(Entity newEntity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity entity = repositionEntity.apply(false); //Must be false or we fall on vanilla

        BlockPos dimPos = getDimensionPosition(entity, destWorld.getDimensionKey(), entity.getPosition());
        doSafetyChecks(entity, currentWorld, destWorld, (double)dimPos.getX() + 0.5D, dimPos.getY(), (double)dimPos.getZ() + 0.5D);
        return entity;
    }

    public void doSafetyChecks(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, double x, double y, double z) {
        this.world.playSound(null, x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.25F, this.world.rand.nextFloat() * 0.4F + 0.8F);
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 200, false, false));
        }

        if (entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
        } else {
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, 0.0F);
        }
        entity.setMotion(Vector3d.ZERO);

        if(destWorld.getDimensionKey() == World.THE_NETHER) {
            relocateInNether(entity, x, y, z);
        } else if(destWorld.getDimensionKey() == World.THE_END) {
            endPlacement(entity);
        } else if(destWorld.getDimensionKey() != World.OVERWORLD) {
            customCompat(entity, destWorld);
        }
    }

    @SuppressWarnings("static-access")
	public void addDimensionPosition(Entity entityIn, RegistryKey<World> dim, BlockPos position) {
        CompoundNBT entityData = entityIn.getPersistentData();
        CompoundNBT data = getTag(entityData);
        ResourceLocation dimLocation = dim.getLocation();

        if(dim == World.THE_END) {
            @SuppressWarnings("resource")
			BlockPos spawnPlatform = entityIn.getServer().getWorld(dim).field_241108_a_;
            NNOW.LOGGER.debug("Setting %s's position of %s to: %s", entityIn.getDisplayName().getUnformattedComponentText(), dimLocation, spawnPlatform);
            data.putLong(Reference.MOD_PREFIX + dimLocation, spawnPlatform.toLong());
        } else {
        	NNOW.LOGGER.debug("Setting %s's position of %s to: %s", entityIn.getDisplayName().getUnformattedComponentText(), dimLocation, position);
            data.putLong(Reference.MOD_PREFIX + dimLocation, position.toLong());
        }
        entityData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
    }

    public BlockPos getDimensionPosition(Entity entityIn, RegistryKey<World> dim, BlockPos position) {
        CompoundNBT entityData = entityIn.getPersistentData();
        CompoundNBT data = getTag(entityData);
        ResourceLocation dimLocation = dim.getLocation();

        BlockPos dimPos = position;
        if(data.contains(Reference.MOD_PREFIX + dimLocation)) {
            dimPos = BlockPos.fromLong(data.getLong(Reference.MOD_PREFIX + dimLocation));
            NNOW.LOGGER.debug("Found %s's position of %s to: %s", entityIn.getDisplayName().getUnformattedComponentText(), dimLocation, dimPos);
            return dimPos;
        }

        NNOW.LOGGER.debug("Could not find %'s previous location. Using current location", entityIn.getDisplayName().getUnformattedComponentText());
        return dimPos;
    }

    public boolean hasDimensionPosition(Entity entityIn, RegistryKey<World> dim) {
        CompoundNBT entityData = entityIn.getPersistentData();
        CompoundNBT data = getTag(entityData);

        NNOW.LOGGER.debug("Checking if entity has position stored for : " + dim.getLocation());
        return data.contains(Reference.MOD_PREFIX + dim.getLocation());
    }

    private CompoundNBT getTag(CompoundNBT tag) {
        if(tag == null || !tag.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            return new CompoundNBT();
        }
        return tag.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }

    private void endPlacement(Entity entityIn) {

        int i = MathHelper.floor(entityIn.getPosX());
        int j = MathHelper.floor(entityIn.getPosY()) - 1;
        int k = MathHelper.floor(entityIn.getPosZ());

        for (int j1 = -2; j1 <= 2; ++j1)
        {
            for (int k1 = -2; k1 <= 2; ++k1)
            {
                for (int l1 = -1; l1 < 3; ++l1)
                {
                    int i2 = i + k1 * 1 + j1 * 0;
                    int j2 = j + l1;
                    int k2 = k + k1 * 0 - j1 * 1;
                    boolean flag = l1 < 0;
                    this.world.setBlockState(new BlockPos(i2, j2, k2), flag ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        //TelePastries.logger.debug("after endPlacement");
    }

    private void relocateInNether(Entity entityIn, double x, double y, double z){
        if(ModConfigs.NETHER_CAKE_LOGIC.get()) {
            protectEntity(entityIn, new BlockPos(x, y, z));
        } else {
            if (hasDimensionPosition(entityIn, World.THE_NETHER)) {
                protectEntity(entityIn, new BlockPos(x, y, z));
            } else {
                double moveFactor = 0.125D;
                double d0 = MathHelper.clamp(x * moveFactor, this.world.getWorldBorder().minX() + 16.0D, this.world.getWorldBorder().maxX() - 16.0D);
                double d1 = MathHelper.clamp(z * moveFactor, this.world.getWorldBorder().minZ() + 16.0D, this.world.getWorldBorder().maxZ() - 16.0D);
                @SuppressWarnings("unused")
				double d2 = 8.0D;

                d0 = MathHelper.clamp((int)d0, -29999872, 29999872);
                d1 = MathHelper.clamp((int)d1, -29999872, 29999872);

                double newY = MathHelper.clamp(y, 70, this.world.getHeight() - 10);
                protectEntity(entityIn, new BlockPos(d0, newY, d1));
            }
        }
    }

    private void protectEntity(Entity playerIn, BlockPos position) {
        boolean foundSuitablePlatform = false;
        if (this.world.getDimensionKey() != World.OVERWORLD) {
            for(int j1 = 0; j1 < 5; j1++) {
                BlockPos checkingPos = position.add(0, -(j1), 0);
                /* Check to see if the block is solid. */
                if(this.world.getBlockState(checkingPos).isSolid()) {
                    /* If there are solid blocks within a 3 block radius under you set foundSuitablePlatform to true */
                    foundSuitablePlatform = true;
                    break;
                }
            }

            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if((x == -2 || x == 2) && (z == -2 || z == 2)) {
                        BlockPos testPos = new BlockPos(position.add(x, 3, z));
                        if (!this.world.getBlockState(testPos).isSolid() && this.world.getBlockState(testPos).getMaterial().isLiquid()) {
                            this.world.setBlockState(testPos, Blocks.OBSIDIAN.getDefaultState());
                        }
                    } else {
                        if(this.world.getBlockState(position.add(x, 3, z)).getMaterial().isLiquid()) {
                            this.world.setBlockState(position.add(x, 3, z), Blocks.OBSIDIAN.getDefaultState());
                        }
                        if(!foundSuitablePlatform) {
                            BlockPos testPos = new BlockPos(position.add(x, -2, z));
                            if (!this.world.getBlockState(testPos).isSolid() || this.world.getBlockState(testPos).getMaterial().isLiquid()) {
                                this.world.setBlockState(testPos, Blocks.OBSIDIAN.getDefaultState());
                            }
                        }
                    }
                }
            }
        }

        BlockPos platformPos = new BlockPos(position.add(1, 2, 1));
        for (int y = 1; y <= 3; y++) {
            if (this.world.getBlockState(position.add(0, y, 0)).isSolid() || this.world.getBlockState(position.add(0, y, 0)).getMaterial().isLiquid()) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos testPos = position.add(x, y, z);
                        if (this.world.getBlockState(testPos).isSolid() || this.world.getBlockState(testPos).getMaterial().isLiquid()) {
                            this.world.removeBlock(testPos, false);
                        }
                    }
                }
            }
        }

        playerIn.setLocationAndAngles((double)position.getX() + 0.5D, platformPos.getY(), (double)position.getZ() + 0.5D, 90.0F, 0.0F);
        playerIn.setPositionAndUpdate((double)position.getX() + 0.5D, platformPos.getY(), (double)position.getZ() + 0.5D);
    }

    private void customCompat(Entity entity, ServerWorld destWorld) {
        if(ModList.get().isLoaded("twilightforest")) {
            RegistryKey<World> twilightKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("twilightforest", "twilightforest"));
            if(destWorld.getDimensionKey() == twilightKey) {
                protectEntity(entity, entity.getPosition());
                if(entity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity playerMP = (ServerPlayerEntity)entity;
                    playerMP.func_242111_a(twilightKey, playerMP.getPosition(), playerMP.rotationYaw, true, false);
                }
            }
        }
    }

    public boolean aboveMax(BlockPos pos) {
        boolean flag1 = (world.getDimensionKey() == World.THE_NETHER && !ModConfigs.NETHER_CAKE_LOGIC.get() && (pos.getY() >= 122 || pos.add(0,1,0).getY() >= 122));
        @SuppressWarnings("static-access")
		boolean flag2 = world.isOutsideBuildHeight(pos);
        return flag1 || flag2;
    }
}
