package com.raphydaphy.arcanemagic.item;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.client.particle.ParticleRenderer;
import com.raphydaphy.arcanemagic.client.particle.ParticleSource;
import com.raphydaphy.arcanemagic.client.particle.ParticleUtil;
import com.raphydaphy.arcanemagic.core.common.RecipeManagerMixin;
import com.raphydaphy.arcanemagic.init.ArcaneMagicConstants;
import com.raphydaphy.arcanemagic.init.ModRegistry;
import com.raphydaphy.arcanemagic.init.ModSounds;
import com.raphydaphy.arcanemagic.network.ArcaneMagicPacketHandler;
import com.raphydaphy.arcanemagic.network.ProgressionUpdateToastPacket;
import com.raphydaphy.arcanemagic.notebook.NotebookSectionRegistry;
import com.raphydaphy.arcanemagic.util.ArcaneMagicUtils;
import com.raphydaphy.arcanemagic.util.DataHolder;
import com.raphydaphy.multiblockapi.MultiBlock;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class DeconstructionStaffItem extends Item
{
	public DeconstructionStaffItem()
	{
		super(new Item.Settings().itemGroup(ArcaneMagic.GROUP).stackSize(1));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx)
	{
		BlockPos pos = ctx.getBlockPos();
		Block block = ctx.getWorld().getBlockState(pos).getBlock();
		if (block == Blocks.SOUL_SAND)
		{
			ctx.getWorld().playSound(ctx.getPlayer(), pos, ModSounds.DECONSTRUCT, SoundCategory.BLOCK, 0.5f, 1);

			DataHolder dataPlayer = (DataHolder)ctx.getPlayer();
			if (ctx.getWorld().isClient)
			{
				doParticles(ctx.getWorld(), pos);
			} else if (dataPlayer != null)
			{
				if (!dataPlayer.getAdditionalData().getBoolean(ArcaneMagicConstants.DECONSTRUCTED_SOUL_SAND_KEY))
				{
					ItemEntity entity = new ItemEntity(ctx.getWorld(), pos.getX() + ArcaneMagic.RANDOM.nextFloat(), pos.getY() + ArcaneMagic.RANDOM.nextFloat(), pos.getZ() + ArcaneMagic.RANDOM.nextFloat(), new ItemStack(ModRegistry.RELIC));
					ctx.getWorld().spawnEntity(entity);

					//TODO: Relic notebook section
					dataPlayer.getAdditionalData().putBoolean(ArcaneMagicConstants.DECONSTRUCTED_SOUL_SAND_KEY, true);
					dataPlayer.markAdditionalDataDirty();
				}
			}
			ctx.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
			return ActionResult.SUCCESS;
		}
		if (!(block instanceof MultiBlock))
		{
			Map<Identifier, Recipe<CraftingInventory>> craftingRecipes =  ((RecipeManagerMixin)ctx.getWorld().getRecipeManager()).getRecipes(RecipeType.CRAFTING);
			for (Map.Entry<Identifier, Recipe<CraftingInventory>> entry : craftingRecipes.entrySet())
			{
				Recipe<CraftingInventory> craftingRecipe = entry.getValue();
				if (craftingRecipe.getOutput().getItem() == block.getItem())
				{
					ctx.getWorld().playSound(ctx.getPlayer(), pos, ModSounds.DECONSTRUCT, SoundCategory.BLOCK, 0.5f, 1);
					if (ctx.getWorld().isClient)
					{
						doParticles(ctx.getWorld(), pos);
						return ActionResult.SUCCESS;
					}

					Map<ItemProvider, Integer> items = new HashMap<>();
					for (Ingredient ingredient : craftingRecipe.getPreviewInputs())
					{
						ItemStack[] stacks = ingredient.getStackArray();
						if (stacks.length > 0)
						{
							ItemProvider provider = stacks[0].getItem();
							if (!items.containsKey(stacks[0].getItem()))
							{
								items.put(provider, 1);
							} else
							{
								items.put(provider, items.get(provider) + 1);
							}
						}
					}

					for (Map.Entry<ItemProvider, Integer> itemPair : items.entrySet())
					{
						ItemStack item = new ItemStack(itemPair.getKey(), itemPair.getValue());
						int output = craftingRecipe.getOutput().getAmount();
						if (output > 1)
						{
							if (item.getAmount() >= output * 2)
							{
								item.setAmount(item.getAmount() / output);
							} else
							{
								if (ArcaneMagic.RANDOM.nextInt(2) == 0)
								{
									item.setAmount(1);
								} else
								{
									item.setAmount(0);
								}
							}
						}

						if (item.getAmount() > 1)
						{
							item.setAmount((int)Math.ceil(item.getAmount() / 2f));
						}

						if (item.getAmount() > 0)
						{
							ItemEntity entity = new ItemEntity(ctx.getWorld(), pos.getX() + ArcaneMagic.RANDOM.nextFloat(), pos.getY() + ArcaneMagic.RANDOM.nextFloat(), pos.getZ() + ArcaneMagic.RANDOM.nextFloat(), item);
							ctx.getWorld().spawnEntity(entity);
						}
					}

					ctx.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}

	private void doParticles(World world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		int color = MinecraftClient.getInstance().getBlockColorMap().method_1691(state, world, pos);
		if (state.getBlock() instanceof FallingBlock)
		{
			color = ((FallingBlock) state.getBlock()).getColor(state);
		}

		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;

		float inverseSpread = 100;
		Random rand = ArcaneMagic.RANDOM;

		ParticleRenderer.INSTANCE.addSource(new ParticleSource((ticks) ->
		{
			for (int i = 0; i < 3; i++)
			{
				System.out.println(0.1f + rand.nextFloat() * 0.8f);
				ParticleUtil.spawnGlowParticle(world,
						pos.getX() + 0.1f + rand.nextFloat() * 0.8f, pos.getY() + 0.1f + rand.nextFloat() * 0.8f, pos.getZ() +  0.1f+ rand.nextFloat() * 0.8f,
						(float) rand.nextGaussian() / inverseSpread, (float) rand.nextGaussian() / inverseSpread, (float) rand.nextGaussian() / inverseSpread, r, g, b, 1, true, 0.3f, 50);
			}
		}, 3));
	}

	@Override
	public void onCrafted(ItemStack stack, World world, PlayerEntity player)
	{
		if (!world.isClient && player != null)
		{
			if (!((DataHolder) player).getAdditionalData().getBoolean(ArcaneMagicConstants.CRAFTED_DECONSTRUCTION_STAFF_KEY))
			{
				ArcaneMagicPacketHandler.sendToClient(new ProgressionUpdateToastPacket(false), (ServerPlayerEntity) player);
				((DataHolder) player).getAdditionalData().putBoolean(ArcaneMagicConstants.CRAFTED_DECONSTRUCTION_STAFF_KEY, true);
				ArcaneMagicUtils.updateNotebookSection(world, (DataHolder)player, NotebookSectionRegistry.DECONSTRUCTION.getID().toString(), false);
				((DataHolder) player).markAdditionalDataDirty();
			}
		}
	}
}