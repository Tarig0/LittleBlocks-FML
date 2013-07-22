package slimevoid.littleblocks.client.render.blocks;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.blocks.core.LittleBlockCoordinates;
import slimevoid.littleblocks.client.render.LittleWorldRenderer;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.RenderLib;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class LittleBlocksRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}
	
	public static HashMap<String, LittleBlocksLittleRenderer[]> renderBlocks = new HashMap<String, LittleBlocksLittleRenderer[]>();

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block.blockID == LBCore.littleChunkID) {
			//if (!LBCore.optifine) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) world
						.getBlockTileEntity(x, y, z);
				if (tile == null) {
					return false;
				}
				LittleBlocksLittleRenderer littleBlocks[];
				if (renderBlocks.containsKey(tile.toString())) {
					littleBlocks = renderBlocks.get(tile.toString());
				} else {
					littleBlocks = new LittleBlocksLittleRenderer[2];
				}
				if (littleBlocks[BlockLittleChunk.currentPass] == null) {
					littleBlocks[BlockLittleChunk.currentPass] = new LittleBlocksLittleRenderer(LBCore.getLittleBlocksRenderer(tile.worldObj));
				
					int[][][] content = tile.getContents();
					for (int x1 = 0; x1 < content.length; x1++) {
						for (int y1 = 0; y1 < content[x1].length; y1++) {
							for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
								int blockId = content[x1][y1][z1];
								if (blockId > 0) {
									Block littleBlock = Block.blocksList[blockId];
									if (littleBlock !=  null) {
										int[] coords = {
												(x << 3) + x1,
												(y << 3) + y1,
												(z << 3) + z1 };
										littleBlocks[BlockLittleChunk.currentPass].addLittleBlockToRender(littleBlock, coords[0], coords[1], coords[2]);
									} else {
										FMLCommonHandler.instance().getFMLLogger().warning("Attempted to render a block that was null!");
									}
								}
							}
						}
					}
				}
				ILittleWorld littleWorld = tile.getLittleWorld();
				LittleWorldRenderer renderWorld = RenderLib.getRenderer(littleWorld);
				if (renderWorld.needsRefresh()) {
					for (LittleBlockCoordinates newBlock : renderWorld.getLittleChunkCache()) {
						Block newLittleBlock = Block.blocksList[littleWorld.getBlockId(
								newBlock.x,
								newBlock.y,
								newBlock.z
								)];
						if (newBlock.isInvalid()) {
							littleBlocks[BlockLittleChunk.currentPass].removeLittleBlockFromRender(
								newLittleBlock,
								newBlock.x,
								newBlock.y,
								newBlock.z);
						} else {					
							littleBlocks[BlockLittleChunk.currentPass].addLittleBlockToRender(
									newLittleBlock,
									newBlock.x,
									newBlock.y,
									newBlock.z);
						}
						renderWorld.blockHasUpdated(newBlock.x, newBlock.y, newBlock.z);
					}
				}
				littleBlocks[BlockLittleChunk.currentPass].renderLittleBlocks(world, x, y, z);
				renderBlocks.put(tile.toString(), littleBlocks);
			//}
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return LBCore.renderType;
	}
}
