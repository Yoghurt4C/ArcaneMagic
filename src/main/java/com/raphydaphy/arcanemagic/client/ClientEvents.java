package com.raphydaphy.arcanemagic.client;

import java.awt.Color;
import java.util.Collection;

import org.lwjgl.opengl.GL11;

import com.raphydaphy.arcanemagic.api.ArcaneMagicAPI;
import com.raphydaphy.arcanemagic.api.essence.Essence;
import com.raphydaphy.arcanemagic.api.essence.EssenceStack;
import com.raphydaphy.arcanemagic.api.essence.IEssenceStorage;
import com.raphydaphy.arcanemagic.api.scepter.ScepterRegistry;
import com.raphydaphy.arcanemagic.client.render.GLHelper;
import com.raphydaphy.arcanemagic.client.render.RenderEntityItemFancy;
import com.raphydaphy.arcanemagic.client.render.RenderEntityMagicCircles;
import com.raphydaphy.arcanemagic.common.ArcaneMagic;
import com.raphydaphy.arcanemagic.common.entity.EntityItemFancy;
import com.raphydaphy.arcanemagic.common.entity.EntityMagicCircles;
import com.raphydaphy.arcanemagic.common.init.ModRegistry;
import com.raphydaphy.arcanemagic.common.item.ItemParchment;
import com.raphydaphy.arcanemagic.common.item.ItemScepter;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEvents
{
	@SubscribeEvent
	public static void onRenderHand(RenderSpecificHandEvent ev)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (!player.isSneaking() && ev.getItemStack().getItem() instanceof ItemParchment)
		{

			float f = player.getSwingProgress(ev.getPartialTicks());
			float f1 = player.prevRotationPitch
					+ (player.rotationPitch - player.prevRotationPitch) * ev.getPartialTicks();

			ItemRenderer itemrenderer = Minecraft.getMinecraft().getItemRenderer();

			float prevEquipProgress = ev.getHand() == EnumHand.MAIN_HAND ? itemrenderer.prevEquippedProgressMainHand
					: itemrenderer.prevEquippedProgressOffHand;
			float equipProgress = ev.getHand() == EnumHand.MAIN_HAND ? itemrenderer.equippedProgressMainHand
					: itemrenderer.equippedProgressOffHand;
			float f5 = 1.0F - (prevEquipProgress + (equipProgress - prevEquipProgress) * ev.getPartialTicks());

			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();

			if (ev.getHand() == EnumHand.MAIN_HAND && player.getHeldItemOffhand().isEmpty())
			{
				GLHelper.renderParchmentFirstPerson(f1, f5, f, ev.getItemStack());
			} else
			{
				EnumHandSide enumhandside = ev.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand()
						: player.getPrimaryHand().opposite();
				GLHelper.renderParchmentFirstPersonSide(f5, enumhandside, f, ev.getItemStack());
			}

			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
			ev.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityItemFancy.class, new RenderEntityItemFancy.Factory());
		RenderingRegistry.registerEntityRenderingHandler(EntityMagicCircles.class,
				new RenderEntityMagicCircles.Factory());
		for (Item i : ModRegistry.ITEMS)
			if (i instanceof IHasModel)
				((IHasModel) i).initModels(event);
		for (Block b : ModRegistry.BLOCKS)
			if (b instanceof IHasModel)
				((IHasModel) b).initModels(event);
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent ev)
	{
		World world = Minecraft.getMinecraft().world;
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (world != null && player != null)
		{
			if (player.getHeldItemMainhand().getItem().equals(ModRegistry.MYSTICAL_ILLUMINATOR)
					|| player.getHeldItemOffhand().getItem().equals(ModRegistry.MYSTICAL_ILLUMINATOR))
			{
				ICamera icamera = new Frustum();
				float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
				double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
				double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
				double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
				icamera.setPosition(d0, d1, d2);
				ViewFrustum vf = Minecraft.getMinecraft().renderGlobal.viewFrustum;
				for (int x = -50; x < 50; x++)
				{
					for (int y = -50; y < 50; y++)
					{
						if (y > 0 && y < 256)
						{
							for (int z = -50; z < 50; z++)
							{
								if (world.rand.nextInt(10) == 1)
								{
									BlockPos first = new BlockPos(player.posX + x, player.posY + y, player.posZ + z);
									if (icamera.isBoundingBoxInFrustum(vf.getRenderChunk(first).boundingBox))
									{
										if (world.isBlockLoaded(first))
										{
											Block firstBlock = player.world.getBlockState(first).getBlock();
											if (firstBlock != Blocks.AIR)
											{
												if (ArcaneMagicAPI.canAnalyseBlock(firstBlock))
												{
													world.spawnParticle(EnumParticleTypes.PORTAL,
															first.getX() + 0.4 + (world.rand.nextFloat() / 4),
															first.getY() + 1,
															first.getZ() + 0.4 + (world.rand.nextFloat() / 4), 0, 0, 0);

												}
											}
										}
									}

								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void renderWorldLastEvent(RenderWorldLastEvent ev)
	{

		World world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		GlStateManager.translate(-player.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks()).x,
				-player.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks()).y,
				-player.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks()).z);
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.enableCull();
		GlStateManager.disableAlpha();
		// pre-alpha
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

		boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
		GlStateManager.depthMask(false);

		GlStateManager.pushMatrix();

		for (int x = -10; x < 10; x++)
		{
			for (int y = -10; y < 10; y++)
			{
				for (int z = -10; z < 10; z++)
				{
					BlockPos first = new BlockPos(player.posX + x, player.posY + y, player.posZ + z);
					if (world.isBlockLoaded(first))
					{
						Block firstBlock = player.world.getBlockState(first).getBlock();
						if (firstBlock != Blocks.AIR)
						{
							if (firstBlock == ModRegistry.CRYSTALLIZER)
							{
								for (int x2 = -10; x2 < 10; x2++)
								{
									for (int y2 = -10; y2 < 10; y2++)
									{
										for (int z2 = -10; z2 < 10; z2++)
										{
											BlockPos second = new BlockPos(first.getX() + x2, first.getY() + y2,
													first.getZ() + z2);

											if (world.getBlockState(second)
													.getBlock() == ModRegistry.ESSENCE_CONCENTRATOR)
											{
												Vec3d to = new Vec3d(first.getX() + 0.5, first.getY() + 2.3,
														first.getZ() + 0.5);
												Vec3d from = new Vec3d(second.getX() + 0.5, second.getY() + 2.2,
														second.getZ() + 0.5);
												Vec3d dist = new Vec3d(Math.pow(to.x - from.x, 2),
														Math.pow(to.y - from.y, 2), Math.pow(to.z - from.z, 2));
												Vec3d lineFrom = new Vec3d(from.x, from.y, from.z);
												// sqrt(pow((endA-startA), 2)+pow((endB-startB), 2));
												Color color = Essence
														.getFromBiome(
																world.getBiome(new BlockPos(from.x, from.y, from.z)))
														.getColor();

												int r = color.getRed();
												int g = color.getGreen();
												int b = color.getBlue();

												GL11.glLineWidth(10);
												Tessellator tes = Tessellator.getInstance();
												BufferBuilder vb = tes.getBuffer();

												RenderHelper.disableStandardItemLighting();

												vb.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

												double radius = 0.5;

												for (int deg = 0; deg < 360; deg++)
												{
													double radians = Math.toRadians(deg);
													Vec3d vertex = new Vec3d(from.x + Math.cos(radians) * radius,
															from.y, from.z + Math.sin(radians) * radius);
													Vec3d newDist = new Vec3d(Math.pow(to.x - vertex.x, 2),
															Math.pow(to.y - vertex.y, 2), Math.pow(to.z - vertex.z, 2));
													if (newDist.x <= dist.x && newDist.z <= dist.z)
													{
														dist = newDist;
														lineFrom = vertex;
													}

													vb.pos(vertex.x, vertex.y, vertex.z).color(r, g, b, 0).endVertex();
													;
												}

												tes.draw();

												vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

												vb.pos(lineFrom.x, lineFrom.y, lineFrom.z).color(r, g, b, 1)
														.endVertex();
												vb.pos(to.x, to.y, to.z).color(r, g, b, 0).endVertex();

												tes.draw();

											}
										}
									}
								}
							}
						}

					}
				}

			}
		}

		if (lighting)

		{
			GL11.glEnable(GL11.GL_LIGHTING);
		}

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();

		GlStateManager.popAttrib();
		GlStateManager.popMatrix();

	}

	@SubscribeEvent
	public static void onDrawScreenPost(RenderGameOverlayEvent.Post event)
	{

		Minecraft mc = Minecraft.getMinecraft();
		if (event.getType() == ElementType.ALL)
		{
			EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;

			if ((!player.getHeldItemMainhand().isEmpty()
					&& player.getHeldItemMainhand().getItem().equals(ModRegistry.SCEPTER)))
			{
				ItemScepter.renderHUD(mc, event.getResolution(), player.getHeldItemMainhand());
			}

			else if (!player.getHeldItemOffhand().isEmpty()
					&& player.getHeldItemOffhand().getItem().equals(ModRegistry.SCEPTER))
			{
				ItemScepter.renderHUD(mc, event.getResolution(), player.getHeldItemOffhand());
			}
		}
	}

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event)
	{
		event.getMap().registerSprite(new ResourceLocation(ArcaneMagic.MODID, "misc/ball"));
		ScepterRegistry.getValues().forEach(part ->
		{
			event.getMap().registerSprite(part.getTexture());
		});
		ArcaneMagic.LOGGER.info("Stiched textures!");
	}

	@SubscribeEvent
	public static void renderTooltipPostBackground(RenderTooltipEvent.PostBackground ev)
	{
		if (ev.getStack().getItem() == ModRegistry.SCEPTER)
		{

			ItemStack stack = ev.getStack();
			IEssenceStorage handler = stack.getCapability(IEssenceStorage.CAP, null);

			int y = ev.getY();
			for (int line = 0; line < ev.getLines().size(); line++)
			{
				if (ev.getLines().get(line).equals("\u00A77"))
				{

					break;
				}
				y += 11;
			}
			if (handler != null)
			{
				Collection<EssenceStack> storedEssence = handler.getStored().values();

				if (storedEssence.size() > 0)
				{
					int x = ev.getX();
					int curYCounter = 0;

					for (EssenceStack essence : storedEssence)
					{

						String thisString = essence.getCount() + " "
								+ I18n.format(essence.getEssence().getTranslationName()) + " ";
						ev.getFontRenderer().drawStringWithShadow(thisString, x, y, essence.getEssence().getColorInt());

						x += 70;
						curYCounter++;

						if (curYCounter % 2 == 0)
						{
							y += 10;
							x = ev.getX();
						}
					}

				}
			}
		}
	}
}
