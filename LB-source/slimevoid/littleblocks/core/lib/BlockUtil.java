package slimevoid.littleblocks.core.lib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoid.littleblocks.world.ItemInLittleWorldManager;
import slimevoid.littleblocks.world.LittlePlayerController;
import slimevoidlib.data.Logger;
import buildcraft.core.IItemPipe;

public class BlockUtil {
	
	private static HashMap<EntityPlayerMP, ItemInLittleWorldManager> itemInLittleWorldManagers;
	
	public static ItemInLittleWorldManager getLittleItemManager(EntityPlayerMP entityplayer) {
		if (itemInLittleWorldManagers.containsKey(entityplayer)) {
			return itemInLittleWorldManagers.get(entityplayer);
		}
		return setLittleItemManagerForPlayer(entityplayer);
	}
	
	private static ItemInLittleWorldManager setLittleItemManagerForPlayer(EntityPlayerMP entityplayer) {
		itemInLittleWorldManagers.put(entityplayer, new ItemInLittleWorldManager(entityplayer.worldObj, entityplayer));
		return itemInLittleWorldManagers.get(entityplayer);
	}

	public static void registerPlacementInfo() {
		itemInLittleWorldManagers = new HashMap<EntityPlayerMP, ItemInLittleWorldManager>();
		//registerDisallowedBlockTick(BlockFluid.class);
		//registerDisallowedBlockTick(BlockFlowing.class);
		registerDisallowedTile(TileEntityLittleChunk.class);
		registerDisallowedItem(ItemHoe.class);
		registerDisallowedItem(ItemMonsterPlacer.class);
	}

	private static Set<Integer> disallowedItemIDs = new HashSet<Integer>();
	private static Set<Integer> disallowedBlockIDs = new HashSet<Integer>();
	private static Set<Class<? extends Item>> disallowedItems = new HashSet<Class<? extends Item>>();
	private static Set<Class<? extends Block>> disallowedBlocks = new HashSet<Class<? extends Block>>();
	private static Set<Class<? extends TileEntity>> disallowedBlockTileEntities = new HashSet<Class<? extends TileEntity>>();
	private static Set<Class<? extends Block>> disallowedBlocksToTick = new HashSet<Class<? extends Block>>();
	private static LittlePlayerController littleController;

	private static void registerDisallowedBlockTick(Class<? extends Block> blockClass) {
		if (blockClass != null) {
			if (!disallowedBlocksToTick.contains(blockClass)) {
				disallowedBlocksToTick.add(blockClass);
			}
		}
	}

	public static boolean isBlockAllowedToTick(Block littleBlock) {
		if (littleBlock != null) {
			if (disallowedBlocksToTick.contains(littleBlock.getClass())) {
				return false;
			}
		}
		return true;
	}

	public static void registerDisallowedBlockID(Integer blockID) {
		if (!disallowedBlocks.contains(blockID)) {
			disallowedBlockIDs.add(blockID);
		}
	}

	private static void registerDisallowedBlock(Class<? extends Block> blockClass) {
		if (blockClass != null) {
			if (!disallowedBlocks.contains(blockClass)) {
				disallowedBlocks.add(blockClass);
			}
		}
	}

	public static boolean isBlockAllowed(Block block) {
		if (block != null) {
			if (disallowedBlocks.contains(block.getClass())) {
				return false;
			}
			if (disallowedBlockIDs.contains(block.blockID)) {
				return false;
			}
		}
		return true;
	}

	public static void registerDisallowedItemID(Integer itemID) {
		if (itemID > Block.blocksList.length) {
			if (!disallowedItemIDs.contains(itemID)) {
				disallowedItemIDs.add(itemID);
			}
		}
	}

	private static void registerDisallowedItem(Class<? extends Item> itemClass) {
		if (itemClass != null) {
			if (!disallowedItems.contains(itemClass)) {
				disallowedItems.add(itemClass);
			}
		}
	}

	public static boolean isItemAllowed(Item item) {
		if (item != null) {
			if (disallowedItems.contains(item.getClass())) {
				return false;
			}
			if (disallowedItemIDs.contains(item.itemID)) {
				return false;
			}
			if (item instanceof IItemPipe) {
				return false;
			}
		}
		return true;
	}

	private static void registerDisallowedTile(Class<? extends TileEntity> tileclass) {
		if (tileclass != null) {
			if (!disallowedBlockTileEntities.contains(tileclass)) {
				disallowedBlockTileEntities.add(tileclass);
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(LBCore.class.toString())
				).write(
						true,
						"Tried to add a tileentity to the disallowed list that already exists",
						Logger.LogLevel.DEBUG
				);
			}
		}
	}

	public static boolean isTileEntityAllowed(TileEntity tileentity) {
		boolean flag = true;
		if (tileentity != null) {
			if (disallowedBlockTileEntities.contains(tileentity.getClass())) {
				return false;
			}
		}
		return flag;
	}

	public static boolean hasTile(int itemBlockId) {
		if (Block.blocksList[itemBlockId] != null) {
			Block theBlock = Block.blocksList[itemBlockId];
			if (theBlock.hasTileEntity(0)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLittleBlock(World world, int x, int y, int z) {
		if (world instanceof ILittleWorld) {
			return ((ILittleWorld) world).getRealWorld().getBlockId(x >> 3, y >> 3, z >> 3) == LBCore.littleChunkID;
		}
		return false;
	}

	public static boolean isLittleBlock(World world, MovingObjectPosition target) {
		return isLittleBlock(world, target.blockX, target.blockY, target.blockZ);
	}

	public static void setController(
			LittlePlayerController littlePlayerController) {
		littleController = littlePlayerController;
	}

	public static PlayerControllerMP getLittleController() {
		return littleController;
	}
}
