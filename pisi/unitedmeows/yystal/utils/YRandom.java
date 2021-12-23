package pisi.unitedmeows.yystal.utils;

import java.util.Random;

public class YRandom {
	public static final YRandom BASIC = new YRandom();
	private final Random random;

	public YRandom() {
		random = new Random();
	}

	public int nextInRange(int min, int max) {
		return random.nextInt(max - min) + min;
	}

	public byte nextByte() {
		return (byte) nextInRange(-127, 127);
	}

	public byte nextByteInRange(int min, int max) {
		return (byte) nextInRange(min, max);
	}

	public int nextInt() {
		return random.nextInt();
	}

	public int nextInt(int max) {
		return random.nextInt(max);
	}
}
