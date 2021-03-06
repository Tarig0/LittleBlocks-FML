package slimevoid.littleblocks.core;

import slimevoid.lib.data.Logger;

public class LoggerLittleBlocks extends Logger {
	
	private static LoggerLittleBlocks instance;

	@Override
	protected String getLoggerName() {
		return "LittleBlocksMod";
	}
	
	public static Logger getInstance(String name) {
		if (instance == null) {
			instance = new LoggerLittleBlocks();
		}

		instance.setName(name);
		
		return instance;
	}
}
