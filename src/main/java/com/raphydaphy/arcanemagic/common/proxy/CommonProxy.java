package com.raphydaphy.arcanemagic.common.proxy;

import com.raphydaphy.arcanemagic.api.essence.Essence;
import com.raphydaphy.arcanemagic.api.essence.EssenceStack;
import com.raphydaphy.arcanemagic.api.notebook.NotebookCategory;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
	}

	public void init(FMLInitializationEvent event)
	{
	}

	public void postInit(FMLPostInitializationEvent event)
	{
	}

	public void spawnEssenceParticles(World world, Vec3d pos, Vec3d speed, Essence essence, Vec3d travelPos,
			boolean isCosmetic)
	{

	}

	public void sendEssenceSafe(EssenceStack essence, Vec3d from, Vec3d to, boolean spawnParticles)
	{

	}

	public void addCategoryUnlockToast(NotebookCategory category)
	{

	}

	public abstract String translate(String key, Object... args);
}