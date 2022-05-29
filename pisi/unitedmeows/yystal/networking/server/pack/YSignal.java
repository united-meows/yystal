package pisi.unitedmeows.yystal.networking.server.pack;

import pisi.unitedmeows.yystal.clazz.HookClass;
import pisi.unitedmeows.yystal.utils.MemoryReader;

public class YapSignal extends HookClass<MemoryReader> {

	public YapSignal() {}

	public YapSignal(MemoryReader memoryReader) {
		hooked = memoryReader;
	}

	public MemoryReader reader() {
		return super.getHooked();
	}

	public byte[] data() {
		return reader().getBytes();
	}
}
