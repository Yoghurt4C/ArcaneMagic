package com.raphydaphy.arcanemagic.client.particle;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.api.essence.Essence;
import com.raphydaphy.arcanemagic.api.essence.EssenceStack;
import com.raphydaphy.arcanemagic.capabilities.EssenceStorage;
import com.raphydaphy.arcanemagic.handler.ArcaneMagicPacketHandler;
import com.raphydaphy.arcanemagic.network.PacketEssenceTransfer;
import com.raphydaphy.arcanemagic.tileentity.TileEntityEssenceStorage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleEssence extends Particle
{
	private Vec3d travelPos;
	private final Vec3d startPos;
	private int speedDivisor = 30;
	private Essence essence;

	public ParticleEssence(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn, Essence essence, Vec3d travelPos)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.motionX = this.motionX * 0.009999999776482582D + xSpeedIn;
		this.motionY = this.motionY * 0.009999999776482582D + ySpeedIn;
		this.motionZ = this.motionZ * 0.009999999776482582D + zSpeedIn;
		this.posX += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
		this.posY += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
		this.posZ += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
		startPos = new Vec3d(xCoordIn, yCoordIn, zCoordIn);
		this.setRBGColorF(essence.getColorRGB().getX(), essence.getColorRGB().getY(), essence.getColorRGB().getZ());
		this.essence = essence;
		this.particleAlpha = 1f;
		this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
		this.travelPos = travelPos;

		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
				.getAtlasSprite(new ResourceLocation(ArcaneMagic.MODID, "misc/ball").toString());
		this.setParticleTexture(sprite);
	}

	@Override
	public int getFXLayer()
	{
		return 1;
	}

	@Override
	public void move(double x, double y, double z)
	{
		this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		this.resetPositionToBB();
	}

	@Override
	public int getBrightnessForRender(float p_189214_1_)
	{
		return 100;
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		TileEntity hit = world.getTileEntity(new BlockPos((int) Math.floor(travelPos.x), (int) Math.floor(travelPos.y),
				(int) Math.floor(travelPos.z)));

		if (travelPos.x <= this.posX + 0.1 && travelPos.x >= this.posX - 0.1 && travelPos.y <= this.posY + 0.1
				&& travelPos.y >= this.posY - 0.1 && travelPos.z <= this.posZ + 0.1 && travelPos.z >= this.posZ - 0.1)
		{

			if (hit != null && hit instanceof TileEntityEssenceStorage)
			{
				
				EssenceStack willTransfer = new EssenceStack(this.essence, 1);
				hit.getCapability(EssenceStorage.CAP, null).store(willTransfer, false);
				ArcaneMagicPacketHandler.INSTANCE.sendToServer(new PacketEssenceTransfer(hit.getPos(), willTransfer));
			}
			this.setExpired();
		}
		if (hit == null)
		{
			this.particleAlpha -= 0.01;
			speedDivisor = 10;
			this.travelPos = this.startPos;
		}
		this.move(this.motionX, this.motionY, this.motionZ);

		this.motionX = (travelPos.x - this.posX) / (speedDivisor + rand.nextDouble());
		this.motionY = (travelPos.y - this.posY) / (speedDivisor + rand.nextDouble());
		this.motionZ = (travelPos.z - this.posZ) / (speedDivisor + rand.nextDouble());

		if (this.onGround)
		{
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
	}
}
