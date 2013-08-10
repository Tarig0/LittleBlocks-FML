package slimevoid.littleblocks.world;

import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class LittlePlayerController extends PlayerControllerMP {

	protected Minecraft mc;
	private int currentBlockY;

	public LittlePlayerController(Minecraft par1Minecraft,
			NetClientHandler par2NetClientHandler) {
		super(par1Minecraft, par2NetClientHandler);
		this.mc = par1Minecraft;
	}

	public boolean onPlayerRightClick(EntityPlayer par1EntityPlayer,
			World par2World, ItemStack par3ItemStack, int par4, int par5,
			int par6, int par7, Vec3 par8Vec3) {
		float f = (float) par8Vec3.xCoord - (float) par4;
		float f1 = (float) par8Vec3.yCoord - (float) par5;
		float f2 = (float) par8Vec3.zCoord - (float) par6;
		boolean flag = false;
		int i1;
		if (par3ItemStack != null
				&& par3ItemStack.getItem() != null
				&& par3ItemStack.getItem().onItemUseFirst(par3ItemStack,
						par1EntityPlayer, par2World, par4, par5, par6, par7, f,
						f1, f2)) {
			return true;
		}

		if (!par1EntityPlayer.isSneaking()
				|| (par1EntityPlayer.getHeldItem() == null || par1EntityPlayer
						.getHeldItem()
						.getItem()
						.shouldPassSneakingClickToBlock(par2World, par4, par5,
								par6))) {
			i1 = par2World.getBlockId(par4, par5, par6);

			if (i1 > 0
					&& Block.blocksList[i1].onBlockActivated(par2World, par4,
							par5, par6, par1EntityPlayer, par7, f, f1, f2)) {
				flag = true;
			}
		}

		if (!flag && par3ItemStack != null
				&& par3ItemStack.getItem() instanceof ItemBlock) {
			ItemBlock itemblock = (ItemBlock) par3ItemStack.getItem();

			if (!itemblock.canPlaceItemBlockOnSide(par2World, par4, par5, par6,
					par7, par1EntityPlayer, par3ItemStack)) {
				return false;
			}
		}

		if (flag) {
			return true;
		} else if (par3ItemStack == null) {
			return false;
		}
		if (!par3ItemStack.tryPlaceItemIntoWorld(par1EntityPlayer, par2World,
				par4, par5, par6, par7, f, f1, f2)) {
			return false;
		}
		if (par3ItemStack.stackSize <= 0) {
			MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(
					par1EntityPlayer, par3ItemStack));
		}
		return true;
	}

	public void clickBlock(int x, int y, int z, int side) {
		ILittleWorld world = LittleBlocks.proxy.getLittleWorld(mc.theWorld,
				false);
		int blockId = world.getBlockId(x, y, z);

		if (blockId > 0) {
			Block.blocksList[blockId].onBlockClicked((World) world, x, y, z,
					this.mc.thePlayer);
		}

		if (blockId > 0
				&& Block.blocksList[blockId].getPlayerRelativeBlockHardness(
						this.mc.thePlayer, (World) world, x, y, z) >= 1.0F) {
			this.onPlayerDestroyBlock(x, y, z, side);
		}
	}

	public boolean onPlayerDestroyBlock(int x, int y, int z, int side) {
		ILittleWorld world = LittleBlocks.proxy.getLittleWorld(
				this.mc.theWorld, false);
		Block block = Block.blocksList[world.getBlockId(x, y, z)];

		if (block == null) {
			return false;
		} else {
			int meta = world.getBlockMetadata(x, y, z);
			boolean flag = block.removeBlockByPlayer((World) world,
					mc.thePlayer, x, y, z);

			if (flag) {
				block.onBlockDestroyedByPlayer((World) world, x, y, z,
						meta);
			}

			this.currentBlockY = -1;

			return flag;
		}
	}
}
