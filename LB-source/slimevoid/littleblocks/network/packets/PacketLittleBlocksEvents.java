package slimevoid.littleblocks.network.packets;

import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoidlib.network.PacketPayload;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlocksEvents extends PacketUpdate {

    public int getEventID() {
    	return this.side;
    }

    /** The block ID this action is set for. */
    public int getBlockId() {
    	return this.payload.getIntPayload(0);
    }

    public int getEventParameter() {
    	return this.payload.getIntPayload(1);
    }

    public PacketLittleBlocksEvents() {
    	super(PacketLib.PACKETID_EVENT, new PacketPayload(2, 0, 0, 0));
    	this.setChannel(CoreLib.MOD_CHANNEL);
    	this.setCommand(CommandLib.BLOCK_EVENT);
    }
    
    public PacketLittleBlocksEvents(int x, int y, int z, int blockID, int eventID, int eventParameter) {
    	this();
    	this.setPosition(x, y, z, eventID);
    	this.setEventParameter(eventParameter);
    	this.setBlockID(blockID);
    }

    /** The block ID this action is set for. */
    public void setBlockID(int blockId) {
    	this.payload.setIntPayload(0, blockId);
    }

    public void setEventParameter(int eventParameter) {
    	this.payload.setIntPayload(1, eventParameter);
    }

	@Override
	public boolean targetExists(World world) {
		return false;
	}
}
