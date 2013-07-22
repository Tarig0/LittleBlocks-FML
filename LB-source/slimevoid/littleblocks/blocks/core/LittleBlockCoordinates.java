package slimevoid.littleblocks.blocks.core;

public class LittleBlockCoordinates {

	public int x, y, z;
	private boolean invalid;
	
	public LittleBlockCoordinates(int x, int y, int z) {
		this(x, y, z, false);
	}

	public LittleBlockCoordinates(int x, int y, int z, boolean isInvalid) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.invalid = isInvalid;
	}
	
	public boolean isInvalid() {
		return this.invalid;
	}

	public void invalidate() {
		this.invalid = true;
	}
}
