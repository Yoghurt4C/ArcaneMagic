package com.raphydaphy.arcanemagic.common;

import org.apache.logging.log4j.Logger;

import com.raphydaphy.arcanemagic.api.ArcaneMagicAPI;
import com.raphydaphy.arcanemagic.api.essence.Essence;
import com.raphydaphy.arcanemagic.api.essence.Essence.EssenceSubscriber;
import com.raphydaphy.arcanemagic.api.notebook.CategoryRegistry;
import com.raphydaphy.arcanemagic.client.proxy.GuiProxy;
import com.raphydaphy.arcanemagic.common.capabilities.Capabilities;
import com.raphydaphy.arcanemagic.common.handler.ArcaneMagicPacketHandler;
import com.raphydaphy.arcanemagic.common.init.ArcaneMagicCreativeTab;
import com.raphydaphy.arcanemagic.common.init.ModEntities;
import com.raphydaphy.arcanemagic.common.init.ModRegistry;
import com.raphydaphy.arcanemagic.common.notebook.NotebookCategories;
import com.raphydaphy.arcanemagic.common.proxy.CommonProxy;
import com.raphydaphy.arcanemagic.common.util.RecipeHelper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = ArcaneMagic.MODID, name = ArcaneMagic.MODNAME, version = ArcaneMagic.VERSION, useMetadata = true)
public class ArcaneMagic
{
	public static final String MODID = "arcanemagic";
	public static final String MODNAME = "Arcane Magic";
	public static final String VERSION = "0.1";

	@SidedProxy(clientSide = "com.raphydaphy.arcanemagic.client.proxy.ClientProxy", serverSide = "com.raphydaphy.arcanemagic.server.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static ArcaneMagic instance;

	public static Logger LOGGER;

	public static final ArcaneMagicCreativeTab creativeTab = new ArcaneMagicCreativeTab();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		LOGGER = event.getModLog();
		ModEntities.init();
		ArcaneMagicPacketHandler.registerMessages(ArcaneMagic.MODID);
		proxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ModRegistry());
		MinecraftForge.EVENT_BUS.register(new EssenceSubscriber());
		ModRegistry.registerTiles();
		Capabilities.register();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.post(
				new Register<Essence>(new ResourceLocation(ArcaneMagic.MODID, "essence_registry"), Essence.REGISTRY));
		NetworkRegistry.INSTANCE.registerGuiHandler(ArcaneMagic.instance, new GuiProxy());
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		NotebookCategories.register();

		Block s = Blocks.STONE;
		RecipeHelper.addElementalShaped(Items.DYE, Essence.HORIZON, 25, s, s, s, null, null, null, s, s, s);
		Block g = Blocks.GLOWSTONE;
		RecipeHelper.addElementalShaped(Items.QUARTZ, Essence.PEACE, 10, g, null, g, null, g, null, g, null, g);
		RecipeHelper.addElementalShapeless(ModRegistry.ANCIENT_PARCHMENT, Essence.CHAOS, 150, "plankWood", "plankWood",
				"cobblestone", g, s);
		RecipeHelper.addElementalShapeless(ModRegistry.ESSENCE, null, 0, "plankWood", "plankWood", g, s);
		
		ArcaneMagicAPI.registerForAnalysis(Blocks.GOLD_ORE, NotebookCategories.NATURAL_HARMONY);
		ArcaneMagicAPI.registerForAnalysis(Blocks.DIAMOND_ORE, NotebookCategories.CRYSTALLIZATION);
		ArcaneMagicAPI.registerForAnalysis(Blocks.DOUBLE_PLANT, NotebookCategories.AFTER_LIFE);
		ArcaneMagicAPI.registerForAnalysis(Blocks.RED_FLOWER, NotebookCategories.AFTER_LIFE);
		ArcaneMagicAPI.registerForAnalysis(Blocks.YELLOW_FLOWER, NotebookCategories.AFTER_LIFE);
		ArcaneMagicAPI.registerForAnalysis(Blocks.RED_MUSHROOM, NotebookCategories.AFTER_LIFE);
		ArcaneMagicAPI.registerForAnalysis(Blocks.BROWN_MUSHROOM, NotebookCategories.AFTER_LIFE);
		ArcaneMagicAPI.registerForAnalysis(Blocks.REDSTONE_ORE, NotebookCategories.MAGICAL_INSIGHTS);
		proxy.postInit(event);
	}

	@Mod.EventHandler
	public void complete(FMLLoadCompleteEvent e)
	{
		CategoryRegistry.sortCategories();
	}
}
