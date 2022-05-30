package pisi.unitedmeows.yystal.networking.server.pack;

import pisi.unitedmeows.yystal.clazz.HookClass;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

public class YSignal extends HookClass<MemoryReader> {

	protected YSignal() {}
	public static YSignalBuilder newSignal() { return YSignalBuilder.builder(); }
	public static YSignalBuilder newSignal(MemoryReader memoryReader) { return YSignalBuilder.builder(memoryReader); }
	public static YSignalBuilder newSignal(MemoryWriter memoryWriter) { return YSignalBuilder.builder(memoryWriter); }

	public YSignal(MemoryReader memoryReader) {
		hooked = memoryReader;
	}

	public MemoryReader reader() {
		return super.getHooked();
	}

	public byte[] data() {
		return reader().getBytes();
	}
}
