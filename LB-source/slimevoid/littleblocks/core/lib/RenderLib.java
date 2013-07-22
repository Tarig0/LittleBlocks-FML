package slimevoid.littleblocks.core.lib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.client.render.LittleWorldRenderer;

@SideOnly(Side.CLIENT)
public class RenderLib {

	private static LittleWorldRenderer littleWorldRenderer;
	
	public static LittleWorldRenderer getRenderer(ILittleWorld littleWorld) {
		if (littleWorldRenderer == null || littleWorldRenderer.isOutdated(littleWorld)) {
			littleWorldRenderer = new LittleWorldRenderer(littleWorld);
		}
		return littleWorldRenderer;
	}

}
