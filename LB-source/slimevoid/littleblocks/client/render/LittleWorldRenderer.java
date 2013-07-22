package slimevoid.littleblocks.client.render;

import java.util.ArrayList;
import java.util.List;

import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.blocks.core.LittleBlockCoordinates;

public class LittleWorldRenderer {
	
	private List<LittleBlockCoordinates> littleChunkCache = new ArrayList<LittleBlockCoordinates>();
	
	private ILittleWorld littleWorld;
	private boolean needsRefresh;
	
	public LittleWorldRenderer(ILittleWorld littleWorld) {
		this.littleWorld = littleWorld;
		this.needsRefresh = false;
	}
	
	public boolean needsRefresh() {
		return this.needsRefresh;
	}

	public void markDirty() {
		this.needsRefresh = true;
	}

	public void markBlockForRenderUpdate(int x, int y, int z) {
		LittleBlockCoordinates blockToUpdate = new LittleBlockCoordinates(x, y, z);
		if (!this.littleChunkCache.contains(blockToUpdate)) {
			this.littleChunkCache.add(blockToUpdate);
		}
		this.markDirty();
	}

	public void markBlockForRenderRemoval(int x, int y, int z) {
		LittleBlockCoordinates blockToRemove = new LittleBlockCoordinates(x, y, z);
		if (this.littleChunkCache.contains(blockToRemove)) {
			this.littleChunkCache.remove(blockToRemove);
		}
		blockToRemove.invalidate();
		this.littleChunkCache.add(blockToRemove);
		this.markDirty();
	}
	
	public void blockHasUpdated(int x, int y, int z) {
		LittleBlockCoordinates updatedBlock = new LittleBlockCoordinates(x, y, z);
		if (this.littleChunkCache.contains(updatedBlock)) {
			this.littleChunkCache.remove(updatedBlock);
		}
	}
	
	public boolean isOutdated(ILittleWorld littleWorld) {
		return !this.littleWorld.equals(littleWorld);
	}

	public List<LittleBlockCoordinates> getLittleChunkCache() {
		return this.littleChunkCache;
	}
}
