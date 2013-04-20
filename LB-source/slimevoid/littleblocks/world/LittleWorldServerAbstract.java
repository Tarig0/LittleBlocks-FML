package slimevoid.littleblocks.world;

import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.common.FMLCommonHandler;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import slimevoid.littleblocks.world.events.LittleBlockEvent;
import slimevoid.littleblocks.world.events.LittleBlockEventList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;

public class LittleWorldServerAbstract extends WorldServer implements ILittleWorld {
	
	private World realWorld;
	private LittleBlockEventList[] blockEventCache = new LittleBlockEventList[] {
			new LittleBlockEventList((LittleBlockEvent) null),
			new LittleBlockEventList((LittleBlockEvent) null) };
    private int blockEventCacheIndex = 0;
	
	public LittleWorldServerAbstract(World world, int dimensionId) {
		/*
		MinecraftServer par1MinecraftServer,
		ISaveHandler par2ISaveHandler,
		String par3Str,
		int par4,
		WorldSettings par5WorldSettings,
		Profiler par6Profiler,
		ILogAgent par7ILogAgent
		*/
		super(FMLCommonHandler.instance().getMinecraftServerInstance(),
				world.getSaveHandler(),
				"LittleWorldServer",
				dimensionId,
				new WorldSettings(
				world.getWorldInfo().getSeed(),
				world.getWorldInfo().getGameType(),
				world.getWorldInfo().isMapFeaturesEnabled(),
				world.getWorldInfo().isHardcoreModeEnabled(),
				world.getWorldInfo().getTerrainType()), null, null);
		this.realWorld = world;
	}

	@Override
	public void tick() {
        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);
        this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
		this.tickUpdates(false);
		this.tickBlocksAndAmbiance();
		this.sendAndApplyBlockEvents();
	}
	
	@Override
    protected void createSpawnPosition(WorldSettings par1WorldSettings) {
		
	}
	
	@Override
    protected void tickBlocksAndAmbiance() {
		this.setActivePlayerChunksAndCheckLight();
        Iterator activeTiles = this.activeChunkSet.iterator();
        while (activeTiles.hasNext()) {
        	TileEntityLittleBlocks tile = (TileEntityLittleBlocks)activeTiles.next();
        	if (tile != null) {
        		int[][][] blocks = tile.getContents();
        		for (int x = 0; x < tile.size; x++) {
        			for (int y = 0; y < tile.size; y++) {
        				for (int z = 0; z < tile.size; z++) {
        					int xx = (tile.xCoord << 3) + x;
        					int yy = (tile.yCoord << 3) + y;
        					int zz = (tile.zCoord << 3) + z;
	        				int blockId = blocks[x][y][z];
        					if (blockId > 0 && blockId <= 4096) {
        						Block littleBlock = Block.blocksList[blocks[x][y][z]];
        						if (littleBlock != null && littleBlock.getTickRandomly()) {
        							littleBlock.updateTick(this, xx, yy, zz, this.rand);
        						}
        					}
        				}
        			}
        		}
        	}
        }
	}
	
	@Override
    protected void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        Iterator chunks = this.getRealWorld().activeChunkSet.iterator();
        while (chunks.hasNext()) {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair)chunks.next();
            int k = chunkcoordintpair.chunkXPos * 16;
            int l = chunkcoordintpair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
            Iterator tiles = chunk.chunkTileEntityMap.values().iterator();
            while (tiles.hasNext()) {
                TileEntity tileentity = (TileEntity)tiles.next();
	            if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
	            	this.activeChunkSet.add(tileentity);
	            }
            }
        }
	}

	@Override
	public World getRealWorld() {
		return this.realWorld;
	}

	@Override
	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		int blockX = (x << 3) + littleX,
				blockY = (y << 3) + littleY,
				blockZ = (z << 3) + littleZ;
/*		this.notifyBlockChange(
				blockX,
				blockY,
				blockZ,
				blockId);*/
		Block block = Block.blocksList[blockId];
		if (block != null) {
			PacketLib.sendMetadata(
					this,
					blockX,
					blockY,
					blockZ,
					blockId,
					side,
					metadata);
		}
	}
	
	@Override
	public void updateTileEntityChunkAndDoNothing(int x, int y, int z,
			TileEntity tileentity) {
		if (!this.isRemote) {
			if (this.blockExists(x, y, z)) {
				PacketLib.sendTileEntity(this, tileentity, x, y, z);
			}
		}
	}

	@Override
	public void idModified(int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		if (blockId == 0 && lastBlockId != 0) {
			Block block = Block.blocksList[lastBlockId];
			if (block != null) {
				block.breakBlock(
						this,
						blockX,
						blockY,
						blockZ,
						side,
						metadata);
				PacketLib.sendBreakBlock(
						this,
						blockX,
						blockY,
						blockZ,
						side,
						lastBlockId,
						metadata);
			}
		}
		if (blockId != 0) {
			Block block = Block.blocksList[blockId];
			if (block != null) {
				block.onBlockAdded(
						this,
						blockX,
						blockY,
						blockZ);
				PacketLib.sendBlockAdded(
						this,
						blockX,
						blockY,
						blockZ,
						side,
						blockId,
						metadata);
			}
		}
/*		this.notifyBlockChange(
				blockX,
				blockY,
				blockZ,
				blockId);*/
	}
	
	@Override
    public void addBlockEvent(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        BlockEventData blockeventdata = new BlockEventData(par1, par2, par3, par4, par5, par6);
        Iterator iterator = this.blockEventCache[this.blockEventCacheIndex].iterator();
        BlockEventData blockeventdata1;

        do
        {
            if (!iterator.hasNext())
            {
                this.blockEventCache[this.blockEventCacheIndex].add(blockeventdata);
                return;
            }

            blockeventdata1 = (BlockEventData)iterator.next();
        }
        while (!blockeventdata1.equals(blockeventdata));
    }
	
    private void sendAndApplyBlockEvents()
    {
        while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty())
        {
            int i = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;
            Iterator iterator = this.blockEventCache[i].iterator();

            while (iterator.hasNext())
            {
                BlockEventData blockeventdata = (BlockEventData)iterator.next();

                if (this.onBlockEventReceived(blockeventdata))
                {}
            }

            this.blockEventCache[i].clear();
        }
    }
    
    private boolean onBlockEventReceived(BlockEventData par1BlockEventData)
    {
        int i = this.getBlockId(par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ());
        return i == par1BlockEventData.getBlockID() ? Block.blocksList[i].onBlockEventReceived(this, par1BlockEventData.getX(), par1BlockEventData.getY(), par1BlockEventData.getZ(), par1BlockEventData.getEventID(), par1BlockEventData.getEventParameter()) : false;
    }

}
