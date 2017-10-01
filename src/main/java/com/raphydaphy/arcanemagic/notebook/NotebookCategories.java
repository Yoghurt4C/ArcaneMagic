package com.raphydaphy.arcanemagic.notebook;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.api.ArcaneMagicAPI;
import com.raphydaphy.arcanemagic.api.notebook.NotebookCategory;
import com.raphydaphy.arcanemagic.notebook.category.CategoryAncientRelics;
import com.raphydaphy.arcanemagic.notebook.category.CategoryBasicLinguistics;
import com.raphydaphy.arcanemagic.notebook.category.CategoryCrystallization;
import com.raphydaphy.arcanemagic.notebook.category.CategoryElementalParticles;
import com.raphydaphy.arcanemagic.notebook.category.CategoryEssenceCollection;
import com.raphydaphy.arcanemagic.notebook.category.CategoryManipulatingMagic;
import com.raphydaphy.arcanemagic.notebook.category.CategoryMysticalEnergy;
import com.raphydaphy.arcanemagic.notebook.category.CategoryNaturalHarmony;

public class NotebookCategories
{
	public static final NotebookCategory ANCIENT_RELICS = new CategoryAncientRelics().setRegistryName(ArcaneMagic.MODID, "ancient_relics");
	public static final NotebookCategory BASIC_LINGUISTICS = new CategoryBasicLinguistics().setRegistryName(ArcaneMagic.MODID, "basic_linguistics");
	public static final NotebookCategory ELEMENTAL_PARTICLES = new CategoryElementalParticles().setRegistryName(ArcaneMagic.MODID, "elemental_particles");
	public static final NotebookCategory MYSTICAL_ENERGY = new CategoryMysticalEnergy().setRegistryName(ArcaneMagic.MODID, "mystical_energy");
	public static final NotebookCategory ESSENCE_COLLECTION = new CategoryEssenceCollection().setRegistryName(ArcaneMagic.MODID, "essence_collection");
	public static final NotebookCategory CRYSTALLIZATION = new CategoryCrystallization().setRegistryName(ArcaneMagic.MODID, "crystallization");
	public static final NotebookCategory NATURAL_HARMONY = new CategoryNaturalHarmony().setRegistryName(ArcaneMagic.MODID, "natural_harmony");
	public static final NotebookCategory MANIPULATING_MAGIC = new CategoryManipulatingMagic().setRegistryName(ArcaneMagic.MODID, "manipulating_magic");	
	
	private static boolean done = false;

	public static void register()
	{
		if (done)
			return;
		done = true;

		ArcaneMagicAPI.registerCategory(ANCIENT_RELICS);
		ArcaneMagicAPI.registerCategory(BASIC_LINGUISTICS);
		ArcaneMagicAPI.registerCategory(ELEMENTAL_PARTICLES);
		ArcaneMagicAPI.registerCategory(MYSTICAL_ENERGY);
		ArcaneMagicAPI.registerCategory(ESSENCE_COLLECTION);
		ArcaneMagicAPI.registerCategory(CRYSTALLIZATION);
		ArcaneMagicAPI.registerCategory(NATURAL_HARMONY);
		ArcaneMagicAPI.registerCategory(MANIPULATING_MAGIC);
	}

}