package pisi.unitedmeows.yystal.yap;

import pisi.unitedmeows.yystal.clazz.HookClass;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

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
