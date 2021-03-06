package com.cibernet.splatcraft.commands;

import com.cibernet.splatcraft.blocks.IInked;
import com.cibernet.splatcraft.items.ItemRemote;
import com.cibernet.splatcraft.items.ItemTurfScanner;
import com.cibernet.splatcraft.network.PacketSendColorScores;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityColor;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.*;

public class CommandTurfWar extends CommandBase
{
	
	@Override
	public String getName()
	{
		return "turfwar";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.turfWar.usage";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		System.out.println("E");
		
		if (args.length < 6)
		{
			throw new WrongUsageException("commands.turfWar.usage", new Object[0]);
		}
		BlockPos blockpos = parseBlockPos(sender, args, 0, false);
		BlockPos blockpos1 = parseBlockPos(sender, args, 3, false);
		World world = sender.getEntityWorld();
		
		boolean multiLayered = false;
		
		if(args.length > 6)
			multiLayered = args[6].equals("true");
		
		if(sender.getCommandSenderEntity() == null || sender.getCommandSenderEntity() instanceof EntityPlayerMP)
		{
			ItemRemote.RemoteResult result = ItemTurfScanner.scanTurf(world, blockpos, blockpos1, null, -1, multiLayered ? 1 : 0, (EntityPlayerMP) sender.getCommandSenderEntity());
			
			if(!result.wasSuccessful())
				throw new CommandException(result.getOutput().getUnformattedComponentText());
		}
		
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
	{
		if (args.length > 0 && args.length <= 3)
		{
			return getTabCompletionCoordinate(args, 0, targetPos);
		}
		else if (args.length > 3 && args.length <= 6)
		{
			return getTabCompletionCoordinate(args, 3, targetPos);
		}
		
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 1;
	}
	
	/**
	 * Finds the highest block on the x and z coordinate that is solid or liquid, and returns its y coord.
	 */
	private static final List<Block> allowed = Arrays.asList(Blocks.CARPET, Blocks.SNOW_LAYER);
	public static BlockPos getTopSolidOrLiquidBlock(BlockPos pos, World world, int maxY)
	{
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		BlockPos blockpos;
		BlockPos blockpos1;
		
		for (blockpos = new BlockPos(pos.getX(), Math.min(chunk.getTopFilledSegment() + 16, maxY), pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
		{
			blockpos1 = blockpos.down();
			IBlockState state = chunk.getBlockState(blockpos1);
			
			if (allowed.contains(state.getBlock()) || !SplatCraftUtils.canInkPassthrough(world, blockpos1) ||
					(state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, world, blockpos1) && !state.getBlock().isFoliage(world, blockpos1)))
				break;
			
		}
		
		return blockpos;
	}
}
