package com.raphydaphy.arcanemagic.structure;

import com.raphydaphy.arcanemagic.util.ArcaneMagicResources;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IglooPieces;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class WizardHutPieces
{
	private static final ResourceLocation WIZARD_HUT = new ResourceLocation(ArcaneMagicResources.MOD_ID, "wizard_hut");


	public static void addPieces(List<StructurePiece> pieces, IWorld world, TemplateManager manager, BlockPos pos)
	{
		pieces.add(new WizardHutPieces.Piece(world, manager, pos));
	}


	public static class Piece extends TemplateStructurePiece
	{
		public Piece()
		{
			System.out.println("HELLO IT IS ME I AM A EMPT PIECE I HAVE NO FEELINGS SO FEEL FREE TO INSULT ME");
		}

		public Piece(IWorld world, TemplateManager manager, BlockPos pos)
		{
			this.templatePosition = pos;
			initPiece(world, manager);
		}

		@Override
		protected void handleDataMarker(String s, BlockPos blockPos, IWorld iWorld, Random random, MutableBoundingBox mutableBoundingBox)
		{

		}

		private void initPiece(IWorld world, TemplateManager manager)
		{
			System.out.println("Wizard hut at " + templatePosition);
			Template template = manager.func_200220_a(WIZARD_HUT);

			PlacementSettings settings = (new PlacementSettings());
			this.setup(template, this.templatePosition, settings);
		}

		@Override
		public boolean addComponentParts(IWorld world, Random rand, MutableBoundingBox bounds, ChunkPos chunkPos)
		{
			int worldSurface = world.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, templatePosition.getX(), templatePosition.getZ());
			BlockPos tempPosition = this.templatePosition;
			this.templatePosition = this.templatePosition.add(0, worldSurface - 90 - 1, 0);
			boolean superAddParts = super.addComponentParts(world, rand, bounds, chunkPos);
			this.templatePosition = tempPosition;
			return superAddParts;
		}
	}
}
