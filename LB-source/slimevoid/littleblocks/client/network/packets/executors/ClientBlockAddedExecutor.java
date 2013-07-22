package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class ClientBlockAddedExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks && packet.getCommand().equals(CommandLib.BLOCK_ADDED)) {
			if (world.blockExists(
					packet.xPosition >> 3,
					packet.yPosition >> 3,
					packet.zPosition >> 3)) {
				world.setBlock(
					packet.xPosition >> 3,
					packet.yPosition >> 3,
					packet.zPosition >> 3,
					LBCore.littleChunkID);
			}
			TileEntityLittleChunk tileentitylittleblocks = (TileEntityLittleChunk) world.getBlockTileEntity(
					packet.xPosition >> 3,
					packet.yPosition >> 3,
					packet.zPosition >> 3);
			if (tileentitylittleblocks != null) {
				tileentitylittleblocks.handleBlockAdded(world, entityplayer, (PacketLittleBlocks)packet);
			}
			tileentitylittleblocks.getLittleWorld().markBlockForUpdate(
					packet.xPosition,
					packet.yPosition,
					packet.zPosition,
					false);
		}
	}

}
