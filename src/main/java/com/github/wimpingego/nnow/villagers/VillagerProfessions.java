package com.github.wimpingego.nnow.villagers;


import com.github.wimpingego.nnow.NNOW;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: CAS_ual_TY
 */

@EventBusSubscriber(modid = NNOW.MOD_ID, bus = Bus.MOD)
@ObjectHolder(NNOW.MOD_ID)
public class VillagerProfessions
{
    public static final VillagerProfession OLD_DERPY = null;
    public static final VillagerProfession BANKER = null;
    public static final VillagerProfession BEEKEEPER = null;
    public static final VillagerProfession MONSTER_TRAPPER = null;
    public static final VillagerProfession END_TRADER= null;
    
    @SubscribeEvent
    public static void registerVillagerProfessions(Register<VillagerProfession> event)
    {
        IForgeRegistry<VillagerProfession> registry = event.getRegistry();
        
        registry.register(VillagerUtil.villagerProfession("old_derpy", PointOfInterestTypes.OLD_DERPY, ImmutableSet.of(), ImmutableSet.of(), null).setRegistryName(NNOW.MOD_ID, "old_derpy"));
        registry.register(VillagerUtil.villagerProfession("banker", PointOfInterestTypes.BANKER, ImmutableSet.of(), ImmutableSet.of(), null).setRegistryName(NNOW.MOD_ID, "banker"));
        registry.register(VillagerUtil.villagerProfession("beekeeper", PointOfInterestTypes.BEEKEEPER, ImmutableSet.of(), ImmutableSet.of(), null).setRegistryName(NNOW.MOD_ID, "beekeeper"));
        registry.register(VillagerUtil.villagerProfession("monster_trapper", PointOfInterestTypes.MONSTER_TRAPPER, ImmutableSet.of(), ImmutableSet.of(), null).setRegistryName(NNOW.MOD_ID, "monster_trapper"));
        registry.register(VillagerUtil.villagerProfession("end_trader", PointOfInterestTypes.END_TRADER, ImmutableSet.of(), ImmutableSet.of(), null).setRegistryName(NNOW.MOD_ID, "end_trader"));
    }
}
