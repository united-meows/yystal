package pisi.unitedmeows.yystal.yap;

import pisi.unitedmeows.yystal.clazz.HookClass;
import pisi.unitedmeows.yystal.utils.MemoryReader;
import pisi.unitedmeows.yystal.utils.MemoryWriter;

import java.io.IOException;

public class YapSignalBuilder extends HookClass<MemoryWriter> {

	public YapSignalBuilder() {
		hooked = new MemoryWriter();
	}

	public YapSignalBuilder write(int data) {
		try {
			hooked.writeInt(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(String data) {
		try {
			hooked.writeUTF(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(float data) {
		try {
			hooked.writeFloat(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(byte data) {
		try {
			hooked.writeByte(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(char data) {
		try {
			hooked.writeChar(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(short data) {
		try {
			hooked.writeShort(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(boolean data) {
		try {
			hooked.writeBoolean(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(byte[] data) {
		try {
			hooked.write(data);
		} catch (IOException e) {}
		return this;
	}

	public YapSignalBuilder write(double data) {
		try {
			hooked.writeDouble(data);
		} catch (IOException e) {}
		return this;
	}

	public static YapSignalBuilder builder() {
		return new YapSignalBuilder();
	}

	public YapSignal build() {
		return new YapSignal(new MemoryReader(getHooked().getBytes()));
	}
	public byte[] buildBytes() { return getHooked().getBytes(); }

}