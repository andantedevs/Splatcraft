package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.network.PacketInkLandParticles;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.particles.SplatCraftParticleSpawner;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import com.cibernet.splatcraft.world.save.SplatCraftGamerules;
import com.cibernet.splatcraft.world.save.SplatCraftPlayerData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockInkedStairs extends BlockStairs implements IInked
{
	private boolean isGlittery;
	
	public BlockInkedStairs(String registryName, boolean isGlittery)
	{
		super(SplatCraftBlocks.inkedBlock.getDefaultState());
		setUnlocalizedName("inkedStairs");
		setRegistryName(registryName);
		setTickRandomly(true);
		BlockInkColor.blocks.add(this);
		
		if(isGlittery)
			setLightLevel(0.4f);
		this.isGlittery = isGlittery;
	}
	
	public BlockInkedStairs()
	{
		this("inked_stairs", false);
	}
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote)
			return true;
		
		int color = InkColors.INK_BLACK.getColor();
		
		if(world.getTileEntity(pos) instanceof TileEntityColor)
			color = ((TileEntityColor) world.getTileEntity(pos)).getColor();
		
		SplatCraftParticleSpawner.spawnInkParticle(entity.posX + ((double)entity.world.rand.nextFloat() - 0.5D) * (double)entity.width, entity.getEntityBoundingBox().minY + 0.1D,
				entity.posZ + ((double)entity.world.rand.nextFloat() - 0.5D) * (double)entity.width, -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, color, 1.5f);
		
		return true;
	}
	
	@Override
	public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos pos, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
	{
		int color = InkColors.INK_BLACK.getColor();
		
		if(worldObj.getTileEntity(pos) instanceof TileEntityColor)
			color = ((TileEntityColor) worldObj.getTileEntity(pos)).getColor();
		
		SplatCraftPacketHandler.instance.sendToAllAround(new PacketInkLandParticles(color, numberOfParticles, entity.posY, entity), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 1024.0D));
		return true;
	}
	
	
	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getMapColor(state, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		InkColors color = InkColors.getByColor(te.getColor());
		if(color == null)
			return super.getMapColor(state, worldIn, pos);
		else return color.getMapColor();
		
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		if(world.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) world.getTileEntity(pos);
			IBlockState savedState = te.getSavedState();
			if(savedState.getBlock() == this)
				return super.getPickBlock(state, target, world, pos, player);
			return savedState.getBlock().getPickBlock(savedState, target, world, pos, player);
		}
		return super.getPickBlock(state, target, world, pos, player);
	}
	
	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getBlockHardness(blockState, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		
		if(te.getSavedState().getBlock() instanceof BlockInkedSlab)
			return blockHardness;
		
		return te.getSavedState().getBlock().getBlockHardness(te.getSavedState(), worldIn, pos);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		return te.getSavedState().getBlock().getPlayerRelativeBlockHardness(te.getSavedState(), player, worldIn, pos);
	}
	
	@Override
	public float getExplosionResistance(World worldIn, BlockPos pos, @Nullable Entity exploder, Explosion explosion)
	{
		
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getExplosionResistance(worldIn, pos, exploder, explosion);
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		try {return te.getSavedState().getBlock().getExplosionResistance(worldIn, pos, exploder, explosion); }
		catch(Exception e) { return 0; }
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			IBlockState savedState = te.getSavedState();
			if(savedState.getBlock() == this)
				super.dropBlockAsItemWithChance(worldIn, pos, savedState, chance, fortune);
			savedState.getBlock().dropBlockAsItemWithChance(worldIn, pos, savedState, chance, fortune);
		}
		else super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
	}
	
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		
		if(te instanceof TileEntityInkedBlock)
		{
			state = ((TileEntityInkedBlock) te).getSavedState();
			Block savedBlock = state.getBlock();
			
			player.addStat(StatList.getBlockStats(this));
			player.addExhaustion(0.005F);
			
			
			if(savedBlock.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0)
			{
				java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
				ItemStack itemstack = SplatCraftUtils.getSilkTouchDropFromBlock(savedBlock, state);
				
				if(!itemstack.isEmpty())
				{
					items.add(itemstack);
				}
				
				net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
				for(ItemStack item : items)
				{
					spawnAsEntity(worldIn, pos, item);
				}
			} else
			{
				harvesters.set(player);
				int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
				savedBlock.dropBlockAsItem(worldIn, pos, state, i);
				harvesters.set(null);
			}
		}
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if(SplatCraftGamerules.getGameruleValue("inkDecay"))
			clearInk(worldIn, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(worldIn.getBlockState(fromPos).getMaterial().equals(Material.WATER))
			clearInk(worldIn, pos);
	}
	
	@Override
	public boolean clearInk(World worldIn, BlockPos pos)
	{
		IBlockState state = worldIn.getBlockState(pos);
		if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			if(te.getSavedState().getBlock() instanceof BlockStairs)
				worldIn.setBlockState(pos, te.getSavedState().withProperty(HALF, state.getValue(HALF)).withProperty(SHAPE, state.getValue(SHAPE)).withProperty(FACING, state.getValue(FACING)), 3);
		} else worldIn.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
		
		return true;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return isGlittery ? BlockRenderLayer.TRANSLUCENT : BlockRenderLayer.SOLID;
	}
	
	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityInkedBlock();
	}
	
	@Override
	public boolean canInk()
	{
		return true;
	}
	
	@Override
	public boolean canDamage()
	{
		return true;
	}
	
	@Override
	public boolean canSwim()
	{
		return true;
	}
	
	@Override
	public boolean canClimb()
	{
		return true;
	}
	
	@Override
	public boolean countsTowardsScore()
	{
		return true;
	}
}
