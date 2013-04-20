package slimevoid.littleblocks.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeDirection;
import slimevoid.lib.data.Logger;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LittleWorldAbstract extends World implements ILittleWorld {

	protected World realWorld;
	
    private List addedTileEntityList = new ArrayList();
    private boolean scanningTileEntities;

	@SideOnly(Side.CLIENT)
	public LittleWorldAbstract(World world) {
		super(
				world.getSaveHandler(),
					"LittleBlocksWorld",
					LBCore.littleProviderClient,
					new WorldSettings(world.getWorldInfo().getSeed(), world
							.getWorldInfo()
								.getGameType(), world
							.getWorldInfo()
								.isMapFeaturesEnabled(), world
							.getWorldInfo()
								.isHardcoreModeEnabled(), world
							.getWorldInfo()
								.getTerrainType()), null, null/**field_98181_L**/);
		this.realWorld = world;
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"LittleWorld[" + world.toString() + "].("+ 
						world.getWorldInfo().getSeed() + ", " +
						world.getWorldInfo().getGameType() + ", " +
						world.getWorldInfo().isMapFeaturesEnabled() + ", " +
						world.getWorldInfo().isHardcoreModeEnabled() + ", " +
						world.getWorldInfo().getTerrainType() + ").realWorld[" + realWorld.toString() + "]",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
	}

	public LittleWorldAbstract(World world, WorldProvider worldprovider) {
		super(world.getSaveHandler(), "LittleBlocksWorld", new WorldSettings(
				world.getWorldInfo().getSeed(),
					world.getWorldInfo().getGameType(),
					world.getWorldInfo().isMapFeaturesEnabled(),
					world.getWorldInfo().isHardcoreModeEnabled(),
					world.getWorldInfo().getTerrainType()), worldprovider, null, null);
		this.realWorld = world;
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"LittleWorld[" + world.toString() + "].("+ 
						world.getWorldInfo().getSeed() + ", " +
						world.getWorldInfo().getGameType() + ", " +
						world.getWorldInfo().isMapFeaturesEnabled() + ", " +
						world.getWorldInfo().isHardcoreModeEnabled() + ", " +
						world.getWorldInfo().getTerrainType() + ").realWorld[" + realWorld.toString() + "]",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return new LittleChunkProvider(this);
	}

	@Override
	public void setSpawnLocation() {
	}
	
    //public int getFirstUncoveredBlock(int x, int z) {}

	@Override
	public int getBlockId(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y >= "+ this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int littleBlockId = tile.getBlockID(x & 7, y & 7, z & 7);
				return littleBlockId;
			} else {
				return id;
			}
		}
	}
	
    //public boolean isAirBlock(int par1, int par2, int par3) {}
	
	//public boolean blockHasTileEntity(int par1, int par2, int par3) {}
	
	//public int blockGetRenderType(int par1, int par2, int par3) {}
	
	@Override
	public boolean blockExists(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y >= "+ this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int littleBlockId = tile.getBlockID(x & 7, y & 7, z & 7);
				return littleBlockId > 0 ? true : false;
			} else {
				return false;
			}
		}
	}
	
	//public boolean doChunksNearChunkExist(int par1, int par2, int par3, int par4) {}

	@Override
	public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		int xDiff = (maxX - minX) >> 1, yDiff = (maxY - minY) >> 1, zDiff = (maxZ - minZ) >> 1;
		int xMid = (minX + maxX) >> 1, yMid = (minY + maxY) >> 1, zMid = (minZ + maxZ) >> 1;

		boolean flag = this.getRealWorld().checkChunksExist(
				(xMid >> 3) - xDiff,
				(yMid >> 3) - yDiff,
				(zMid >> 3) - zDiff,
				(xMid >> 3) + xDiff,
				(yMid >> 3) + yDiff,
				(zMid >> 3) + zDiff);

		return flag;
	}

    //protected boolean chunkExists(int x, int y) {}
	
    //public Chunk getChunkFromBlockCoords(int x, int y) {}
	
    //public Chunk getChunkFromChunkCoords(int x, int y) {}
    
	@Override
    public boolean setBlock(int x, int y, int z, int blockID, int newmeta, int update) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ", " + blockID + ", " + newmeta + ", " + update + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ", " + blockID + ", " + newmeta + ", " + update + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ", " + blockID + ", " + newmeta + ", " + update + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlock(
						x >> 3,
						y >> 3,
						z >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getBlockID(x & 7, y & 7, z & 7);
			int currentData = tile.getBlockMetadata(x & 7, y & 7, z & 7);
			if (currentId != blockID || currentData != newmeta) {
				int originalId = 0;
				if ((update & 1) != 0) {
					originalId = currentId;
				}
				tile.setBlockIDWithMetadata(x & 7, y & 7, z & 7, blockID, newmeta);
				flag = true;
				if ((update & 2) != 0 && (!this.isRemote || (update & 4) == 0))
                {
                    this.markBlockForUpdate(x, y, z);
                }

                if (!this.isRemote && (update & 1) != 0)
                {
                    this.notifyBlockChange(x, y, z, originalId);
                    Block block = Block.blocksList[blockID];

                    if (block != null && block.hasComparatorInputOverride())
                    {
                        this.func_96440_m(x, y, z, blockID);
                    }
                }
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlock(" + x + ", " + y + ", " + z + ", " + blockID + ", " + newmeta + ", " + update + ").[" + blockID + ", "+ newmeta + "]:No Change",
						LoggerLittleBlocks.LogLevel.ERROR
				);
			}
			return flag;
		}
    }
	
	//public Material getBlockMaterial(int par1, int par2, int par3) {}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			int metadata = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockMetadata(
								(x & 0x7f) >> 3,
								y >> 3,
								(z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int littleMetaData = tile.getBlockMetadata(x & 7, y & 7, z & 7);
				return littleMetaData;
			} else {
				return metadata;
			}
		}
	}

	@Override
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int newmeta, int update) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ", " + newmeta + ", " + update + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ", " + newmeta + ", " + update + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ", " + newmeta + ", " + update + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlock(
						x >> 3,
						y >> 3,
						z >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentData = tile.getBlockMetadata(x & 7, y & 7, z & 7);
			if (currentData != newmeta) {
				int currentId = tile.getBlockID(x & 7, y & 7, z & 7);
				tile.setBlockMetadata(x & 7, y & 7, z & 7, newmeta);
				if ((update & 2) != 0 && (!this.isRemote || (update & 4) == 0))
                {
                    this.markBlockForUpdate(x, y, z);
                }

                if (!this.isRemote && (update & 1) != 0)
                {
                    this.notifyBlockChange(x, y, z, currentId);
                    Block block = Block.blocksList[currentId];

                    if (block != null && block.hasComparatorInputOverride())
                    {
                        this.func_96440_m(x, y, z, currentId);
                    }
                }
                return true;
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlock(" + x + ", " + y + ", " + z + ", " + newmeta + ", " + update + ").[" + newmeta + "]:No Change",
						LoggerLittleBlocks.LogLevel.ERROR
				);
				return false;
			}
		}
    }
	
	//public boolean setBlockToAir(int par1, int par2, int par3) {}
	
	//public boolean destroyBlock(int par1, int par2, int par3, boolean par4) {}
	
	//public boolean setBlock(int par1, int par2, int par3, int par4) {}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {
		this.getRealWorld().markBlockForUpdate(x >> 3, y >> 3, z >> 3);
	}
	
	//public void notifyBlockChange(int par1, int par2, int par3, int par4) {}
	
	//public void markBlocksDirtyVertical(int par1, int par2, int par3, int par4) {}

	@Override
    public void markBlockRangeForRenderUpdate(int x, int y, int z, int x2, int y2, int z2) {
		this.getRealWorld().markBlockRangeForRenderUpdate(x >> 3, y >> 3, z >> 3, x2 >> 3, y2 >> 3, z2 >> 3);
	}

	@Override
	public void notifyBlocksOfNeighborChange(int x, int y, int z, int blockId) {
		System.out.println("InitialWorld[" + this.toString() + "]" +
				" | BlockID[" + blockId + "]" + 
				" | x[" + x + "]" + " | y[" + y + "]" + " | z[" + z + "]");
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"notifyBlocksOfNeighborChange(" + x + ", " + y + ", " + z + ", " + blockId + ")",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
		this.notifyBlockOfNeighborChange(x - 1, y, z, blockId);
		this.notifyBlockOfNeighborChange(x + 1, y, z, blockId);
		this.notifyBlockOfNeighborChange(x, y - 1, z, blockId);
		this.notifyBlockOfNeighborChange(x, y + 1, z, blockId);
		this.notifyBlockOfNeighborChange(x, y, z - 1, blockId);
		this.notifyBlockOfNeighborChange(x, y, z + 1, blockId);
	}
	
	//public void notifyBlocksOfNeighborChange(int par1, int par2, int par3, int par4, int par5) {}

	@Override
	public void notifyBlockOfNeighborChange(int x, int y, int z, int blockId) {
		if (!this.isRemote) {
			World world;
			int id = this.realWorld.getBlockId(x >> 3, y >> 3, z >> 3);
			if (id == LBCore.littleBlocksID) {
				world = this;
			} else {
				x >>= 3;
				y >>= 3;
				z >>= 3;
				world = this.getRealWorld();
			}
			System.out.println("World[" + world.toString() + "]" +
								" | BlockID[" + blockId + "]" + 
								" | x[" + x + "]" + " | y[" + y + "]" + " | z[" + z + "]");
			Block block = Block.blocksList[world.getBlockId(x, y, z)];
			if (block != null) {
				try {
					block.onNeighborBlockChange(world, x, y, z, blockId);
				} catch (Throwable thrown) {
					LoggerLittleBlocks.getInstance(
							Logger.filterClassName(
									this.getClass().toString()
							)
					).write(
							world.isRemote,
							"onNeighborBlockChange(" + x + ", " + y + ", " + z + ", " + blockId + ").[" + block.getLocalizedName() + "]",
							LoggerLittleBlocks.LogLevel.DEBUG
					);
				}
				world.markBlockForRenderUpdate(x, y, z);
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						world.isRemote,
						"notifyBlockOfNeighborChange(" + x + ", " + y + ", " + z + ", " + blockId + "):Null",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
			}
		}
	}
	
	//public boolean isBlockTickScheduled(int par1, int par2, int par3, int par4) {}
	
	@Override
    public boolean canBlockSeeTheSky(int x, int y, int z) {
		return this.getRealWorld().canBlockSeeTheSky(x >> 3, y >> 3, z >> 3);
	}
	
	@Override
	public int getFullBlockLightValue(int x, int y, int z) {
		return this.getRealWorld().getFullBlockLightValue(x >> 3, y >> 3, z >> 3);
	}

	@Override
	public int getBlockLightValue(int x, int y, int z) {
		return this.getRealWorld().getBlockLightValue(x >> 3, y >> 3, z >> 3);
	}
	
	//public int getBlockLightValue_do(int par1, int par2, int par3, boolean par4) {}
	
	//public int getHeightValue(int par1, int par2) {}
	
	//public int getChunkHeightMapMinimum(int par1, int par2) {}

    @SideOnly(Side.CLIENT)
	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
		return this.getRealWorld().getSkyBlockTypeBrightness(enumskyblock, x >> 3, y >> 3, z >> 3);
	}
    
    @Override
    public int getSavedLightValue(EnumSkyBlock enumskyblock, int x, int y, int z) {
    	return this.getRealWorld().getSavedLightValue(enumskyblock, x >> 3, y >> 3, z >> 3);
    }
    
    public void setLightValue(EnumSkyBlock enumskyblock, int x, int y, int z, int lightvalue) {
    	this.getRealWorld().setLightValue(enumskyblock, x >> 3, y >> 3, z >> 3, lightvalue);
    }

	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {
		this.realWorld.markBlockForRenderUpdate(x >> 3, y >> 3, z >> 3);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
		return this.getRealWorld().getLightBrightnessForSkyBlocks(
				x >> 3,
				y >> 3,
				z >> 3,
				l);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getBrightness(int x, int y, int z, int l) {
		return this.getRealWorld().getBrightness(
				x >> 3,
				y >> 3,
				z >> 3,
				l);
	}

	@Override
	public float getLightBrightness(int x, int y, int z) {
		return this.getRealWorld().provider.lightBrightnessTable[
		        this.getBlockLightValue(
		        		x >> 3,
		        		y >> 3,
		        		z >> 3)
		        ];
	}
	
	//public boolean isDaytime() {}
	
	//public MovingObjectPosition rayTraceBlocks(Vec3 par1Vec3, Vec3 par2Vec3) {}
	
	//public MovingObjectPosition rayTraceBlocks_do(Vec3 par1Vec3, Vec3 par2Vec3, boolean par3) {}

	@Override
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1) {
		Vec3.xCoord *= LBCore.littleBlocksSize;
		Vec3.yCoord *= LBCore.littleBlocksSize;
		Vec3.zCoord *= LBCore.littleBlocksSize;

		Vec31.xCoord *= LBCore.littleBlocksSize;
		Vec31.yCoord *= LBCore.littleBlocksSize;
		Vec31.zCoord *= LBCore.littleBlocksSize;
		return super.rayTraceBlocks_do_do(Vec3, Vec31, flag, flag1);
	}
	
	@Override
    public void playSoundAtEntity(Entity par1Entity, String par2Str, float par3, float par4) {
    	this.getRealWorld().playSoundAtEntity(par1Entity, par2Str, par3, par4);
    }
	
	@Override
	public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, float par3, float par4) {
		this.getRealWorld().playSoundToNearExcept(par1EntityPlayer, par2Str, par3, par4);
	}
	
	@Override
	public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
		this.getRealWorld().playSoundEffect(
				x / LBCore.littleBlocksSize,
				y / LBCore.littleBlocksSize,
				z / LBCore.littleBlocksSize,
				s,
				f,
				f1);
	}
	
	//public void playSound(double par1, double par3, double par5, String par7Str, float par8, float par9, boolean par10) {}

	@Override
	public void playRecord(String s, int x, int y, int z) {
		this.getRealWorld().playRecord(s, x >> 3, y >> 3, z >> 3);
	}

	@Override
	public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
		this.realWorld.spawnParticle(
				s,
				x / LBCore.littleBlocksSize,
				y / LBCore.littleBlocksSize,
				z / LBCore.littleBlocksSize,
				d3,
				d4,
				d5);
	}
	
	//public boolean addWeatherEffect(Entity par1Entity) {}

	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		entity.setPosition(
				entity.posX / LBCore.littleBlocksSize,
				entity.posY / LBCore.littleBlocksSize,
				entity.posZ / LBCore.littleBlocksSize);
		entity.motionX /= LBCore.littleBlocksSize;
		entity.motionY /= LBCore.littleBlocksSize;
		entity.motionZ /= LBCore.littleBlocksSize;
		entity.worldObj = this.realWorld;
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"spawnEntityInWorld(" + entity.entityId + ").[" +
						entity.posX + ", " +
						entity.posY + ", " +
						entity.posZ + "]",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
		return this.getRealWorld().spawnEntityInWorld(entity);
	}
	
	//protected void obtainEntitySkin(Entity par1Entity) {}
	
	//public void releaseEntitySkin(Entity par1Entity) {}
	
	//public void removeEntity(Entity par1Entity) {}
	
	//public void removePlayerEntityDangerously(Entity par1Entity) {}
	
	//public void addWorldAccess(IWorldAccess par1IWorldAccess) {}
	
	//public List getCollidingBoundingBoxes(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB) {}
	
	//public List getAllCollidingBoundingBoxes(AxisAlignedBB par1AxisAlignedBB) {}
	
	//public int calculateSkylightSubtracted(float par1) {}
	
	//public void removeWorldAccess(IWorldAccess par1IWorldAccess) {}
	
	//public float getSunBrightness(float par1) {}
	
	//public Vec3 getSkyColor(Entity par1Entity, float par2) {}
	
	//public Vec3 getSkyColorBody(Entity par1Entity, float par2) {}
	
	//public float getCelestialAngle(float par1) {}
	
	//public int getMoonPhase() {}
	
	//public float getCelestialAngleRadians(float par1) {}
	
	//public Vec3 getCloudColour(float par1) {}
	
	//public Vec3 drawCloudsBody(float par1) {}

	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		this.getRealWorld().playAuxSFX(
				x / LBCore.littleBlocksSize,
				y / LBCore.littleBlocksSize,
				z / LBCore.littleBlocksSize,
				l,
				i1);
	}
	
	@Override
	public void tick() {
		super.tick();
        this.func_82738_a(this.getTotalWorldTime() + 1L);
		this.setWorldTime(this.getWorldTime() + 1L);
	}

	/**
	 * Runs through the list of updates to run and ticks them
	 */
	@Override
	public boolean tickUpdates(boolean tick) {
		return false;
	}
	
    public List<TileEntity> loadedTiles = new ArrayList<TileEntity>();
    private List<TileEntity> addedTiles = new ArrayList<TileEntity>();
    private boolean scanningTiles;
	
	@Override
	public void updateEntities() {
		this.scanningTiles = true;
		Iterator<TileEntity> loadedTile = this.loadedTiles.iterator();

		while (loadedTile.hasNext()) {
			TileEntity tileentity = loadedTile.next();

			if (!tileentity.isInvalid()
					&& tileentity.func_70309_m()
					&& this.blockExists(tileentity.xCoord, tileentity.yCoord,
							tileentity.zCoord)) {
				try {
					tileentity.updateEntity();
				} catch (Throwable t) {
				}
			}

			if (tileentity.isInvalid()) {
				loadedTile.remove();

				TileEntity tileentitylb = this.realWorld.getBlockTileEntity(
						tileentity.xCoord >> 3,
						tileentity.yCoord >> 3,
						tileentity.zCoord >> 3);
				if (tileentitylb != null && tileentitylb instanceof TileEntityLittleBlocks) {
						((TileEntityLittleBlocks)tileentitylb).cleanTileEntity(
								tileentity.xCoord & 7,
								tileentity.yCoord & 7,
								tileentity.zCoord & 7);
				}
			}
		}

		this.scanningTiles = false;

		if (!this.addedTiles.isEmpty()) {
			for (int i = 0; i < this.addedTiles.size(); ++i) {
				TileEntity tileentity = this.addedTiles
						.get(i);

				if (!tileentity.isInvalid()) {
					if (!this.loadedTiles.contains(tileentity)) {
						this.loadedTiles.add(tileentity);
					}
				} else {
					if (this.chunkExists(tileentity.xCoord >> 4, tileentity.zCoord >> 4)) {
						Chunk var15 = this.getChunkFromChunkCoords(
								tileentity.xCoord >> 4, tileentity.zCoord >> 4);

						if (var15 != null) {
							var15.cleanChunkBlockTileEntity(tileentity.xCoord & 15,
									tileentity.yCoord, tileentity.zCoord & 15);
						}
					}
				}
			}

			this.addedTiles.clear();
		}
	}

	@Override
    public long getWorldTime() {
		if (this.realWorld != null) {
			return this.realWorld.provider.getWorldTime();
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getWorldTime().[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
    }

	@Override
	public long getTotalWorldTime() {
		if (this.realWorld != null) {
			return this.realWorld.getWorldInfo().getWorldTotalTime();
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getTotalWorldTime().[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
	}

	public boolean isOutdated(World world) {
		boolean outdated = !realWorld.equals(world);
		if (outdated) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"isOutDated(" + world.toString() + ").[" +
					outdated + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		return outdated;
	}

	@Override
	public int getHeight() {
		return super.getHeight() * LBCore.littleBlocksSize;
	}

/*	@Override
	public boolean setBlockAndMetadata(int x, int y, int z, int id, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						x >> 3,
						y >> 3,
						z >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getBlockID(x & 7, y & 7, z & 7);
			int currentData = tile.getBlockMetadata(x & 7, y & 7, z & 7);
			if (currentId != id || currentData != metadata) {
				tile.setBlockIDWithMetadata(x & 7, y & 7, z & 7, id, metadata);
				flag = true;
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[" + id + ", "+ metadata + "]:No Change",
						LoggerLittleBlocks.LogLevel.ERROR
				);
			}
			this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			this.markBlockForRenderUpdate(x, y, z);
			return flag;
		}
	}*/

/*	@Override
	public boolean setBlockAndMetadataWithUpdate(int x, int y, int z, int blockId, int metadata, boolean needsUpdate) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			boolean flag = false;
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntity tileentity = realWorld.getBlockTileEntity(
					x >> 3,
					y >> 3,
					z >> 3);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
				int currentId = tileentitylb.getBlockID(x & 7, y & 7, z & 7);
				int currentData = tileentitylb.getBlockMetadata(x & 7, y & 7, z & 7);
				if (currentId != blockId || currentData != metadata) {
					tileentitylb.setBlockIDWithMetadata(
							x & 7,
							y & 7,
							z & 7,
							blockId,
							metadata);
					flag = true;
				} else {
					LoggerLittleBlocks.getInstance(
							Logger.filterClassName(
									this.getClass().toString()
							)
					).write(
							this.isRemote,
							"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[" + blockId + ", " + metadata + "]:No Change",
							LoggerLittleBlocks.LogLevel.DEBUG
					);
				}
				this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
				this.markBlockForRenderUpdate(x, y, z);
			}
			return flag;
		}
	}*/

/*	@Override
	public boolean setBlock(int x, int y, int z, int id) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getBlockID(x & 7, y & 7, z & 7);
			if (currentId != id) {
				tile.setBlockID(x & 7, y & 7, z & 7, id);
				flag = true;
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlock(" + x + ", " + y + ", " + z + ").[" + id + "]:No Change",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
			}
			this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			this.markBlockForRenderUpdate(x, y, z);
			return flag;
		}
	}*/

/*	@Override
	public boolean setBlockMetadata(int x, int y, int z, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int id = tile.getBlockID(x & 7, y & 7, z & 7);
				int currentData = tile.getBlockMetadata(x & 7, y & 7, z & 7);
				if (currentData != metadata) {
					tile.setBlockMetadata(x & 7, y & 7, z & 7, metadata);
				} else {
					LoggerLittleBlocks.getInstance(
							Logger.filterClassName(
									this.getClass().toString()
							)
					).write(
							this.isRemote,
							"setBlockMetadata(" + x + ", " + y + ", " + z + ").[" + id + ", " + metadata + "]:No Change",
							LoggerLittleBlocks.LogLevel.DEBUG
					);
				}
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlockMetadata(" + x + ", " + y + ", " + z + ").[chunkData]",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
				chunk.setBlockMetadata(
						(x & 0x7f) >> 3,
						y >> 3,
						(z & 0x7f) >> 3,
						metadata);
			}
			this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			this.markBlockForRenderUpdate(x, y, z);
			return true;
		}
	}*/


	@Override
	public void idModified(int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		/*int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		this.notifyBlockChange(
				blockX,
				blockY,
				blockZ,
				blockId);*/
		if (this.realWorld != null) {
			this.realWorld.updateAllLightTypes(x, y, z);
			this.realWorld.markBlockForRenderUpdate(x, y, z);
		}
	}

	@Override
	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		/*int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		this.notifyBlockChange(
				blockX,
				blockY,
				blockZ,
				blockId);*/
		if (this.realWorld != null) {
			this.realWorld.updateAllLightTypes(x, y, z);
			this.realWorld.markBlockForRenderUpdate(x, y, z);
		}
	}

	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockTileEntity(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return null;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return null;
		}
		if (y >= this.getHeight()) {
			return null;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntity tileentity = null;
				int l;
				TileEntity tileentity1;

				if (this.scanningTiles) {
					for (l = 0; l < this.addedTiles.size(); ++l) {
						tileentity1 = (TileEntity) this.addedTiles.get(l);

						if (!tileentity1.isInvalid() && tileentity1.xCoord == x
								&& tileentity1.yCoord == y
								&& tileentity1.zCoord == z) {
							tileentity = tileentity1;
							break;
						}
					}
				}

				if (tileentity == null) {
					TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
							.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
					tileentity = tile.getTileEntity(x & 7, y & 7, z & 7);
				}

				if (tileentity == null) {
					for (l = 0; l < this.addedTiles.size(); ++l) {
						tileentity1 = (TileEntity) this.addedTiles.get(l);

						if (!tileentity1.isInvalid() && tileentity1.xCoord == x
								&& tileentity1.yCoord == y
								&& tileentity1.zCoord == z) {
							tileentity = tileentity1;
							break;
						}
					}
				}

				return tileentity;
			} else {
				return realWorld.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			}
		}
	}

	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileentity) {
		System.out.println("Setting TileEntity: " + tileentity.toString());
		Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
		if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
			if (tileentity == null || tileentity.isInvalid()) {
				return;
			}
			if (tileentity.canUpdate()) {
	            if (scanningTiles) {
	                Iterator iterator = addedTiles.iterator();
	                while (iterator.hasNext()) {
	                    TileEntity tileentity1 = (TileEntity)iterator.next();
	    
	                    if (tileentity1.xCoord == x && tileentity1.yCoord == y && tileentity1.zCoord == z) {
	                        tileentity1.invalidate();
	                        iterator.remove();
	                    }
	                }
	                addedTiles.add(tileentity);
	            }
	            else
	            {
	            	loadedTiles.add(tileentity);
	            }
	        }
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			tile.setTileEntity(x & 7, y & 7, z & 7, tileentity);
		} else {
			realWorld.setBlockTileEntity(x >> 3, y >> 3, z >> 3, tileentity);
		}
	}

	@Override
	public void addTileEntity(TileEntity tileentity) {
		System.out.println("Adding TileEntity: " + tileentity.toString());
		List<TileEntity> dest = scanningTiles ? addedTiles : loadedTiles;
		if (tileentity.canUpdate()) {
			dest.add(tileentity);
		}
	}

	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= this.getHeight()) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.removeTileEntity(x & 7, y & 7, z & 7);
			}
		}
	}

	@Override
	public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return _default;
		}

		Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
		if (chunk == null || chunk.isEmpty()) {
			return _default;
		}

		Block block = Block.blocksList[this.getBlockId(x, y, z)];
		if (block == null) {
			return false;
		}

		return block.isBlockSolidOnSide(this, x, y, z, side);
	}

	@Override
    public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
    {
        Explosion explosion = new Explosion(this, entity, x, y, z, strength / 8);
        explosion.isFlaming = isFlaming;
        explosion.isSmoking = isSmoking;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

	@Override
	public World getRealWorld() {
		return this.realWorld;
	}

	@Override
	public Entity getEntityByID(int entityId) {
		return null;
	}
	
	@Override
    public EntityPlayer getClosestPlayer(double par1, double par3, double par5, double par7) {
		return this.realWorld.getClosestPlayer(par1, par3, par5, par7);
	}
	
	@Override
    public EntityPlayer getClosestVulnerablePlayer(double par1, double par3, double par5, double par7) {
		return this.realWorld.getClosestPlayer(par1, par3, par5, par7);
	}
}