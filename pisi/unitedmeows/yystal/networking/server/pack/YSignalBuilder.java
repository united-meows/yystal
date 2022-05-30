package pisi.unitedmeows.yystal.networking.server.pack;

import pisi.unitedmeows.yystal.clazz.HookClass;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

import java.io.IOException;

public class YSignalBuilder extends HookClass<MemoryWriter> {

	public YSignalBuilder() {
		hooked = new MemoryWriter();
	}

	public YSignalBuilder(MemoryWriter memoryWriter) {
		hooked = memoryWriter;
	}

	public static YSignalBuilder newSignal() { return YSignalBuilder.builder(); }
	public static YSignalBuilder newSignal(MemoryReader memoryReader) { return YSignalBuilder.builder(memoryReader); }
	public static YSignalBuilder newSignal(MemoryWriter memoryWriter) { return YSignalBuilder.builder(memoryWriter); }

	public YSignalBuilder write(int data) {
		try {
			hooked.writeInt(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(String data) {
		try {
			hooked.writeUTF(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(float data) {
		try {
			hooked.writeFloat(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(byte data) {
		try {
			hooked.writeByte(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(char data) {
		try {
			hooked.writeChar(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(short data) {
		try {
			hooked.writeShort(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(boolean data) {
		try {
			hooked.writeBoolean(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(byte[] data) {
		try {
			hooked.write(data);
		} catch (IOException e) {}
		return this;
	}

	public YSignalBuilder write(double data) {
		try {
			hooked.writeDouble(data);
		} catch (IOException e) {}
		return this;
	}

	public static YSignalBuilder builder() {
		return new YSignalBuilder();
	}

	public static YSignalBuilder builder(MemoryReader memoryReader) {
		return new YSignalBuilder().write(memoryReader.getBytes());
	}

	public static YSignalBuilder builder(MemoryWriter memoryWriter) {
		return new YSignalBuilder().write(memoryWriter.getBytes());
	}



	public YSignal build() {
		return new YSignal(new MemoryReader(getHooked().getBytes()));
	}
	public byte[] buildBytes() { return getHooked().getBytes(); }

}