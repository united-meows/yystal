package pisi.unitedmeows.yystal.yap;

import java.nio.charset.StandardCharsets;

public enum YapConstants {

	ASK_INTRODUCTION(";;YAP_ASK_INTRODUCTION".getBytes(StandardCharsets.UTF_8));

	private byte[] data;
	YapConstants(byte[] _data) {
		data = _data;
	}

	public byte[] data() {
		return data;
	}
}
