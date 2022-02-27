package pisi.unitedmeows.yystal.hook;
import pisi.unitedmeows.yystal.clazz.HookClass;

import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class YString extends HookClass<StringBuilder> {

	public static final String EMPTY_R = "";
	public static final YString EMPTY = new YString("");

	private boolean changed;
	private String currentValue;

	public YString(StringBuilder stringBuilder) {
		hooked = stringBuilder;
		changed = true;
	}

	public YString(String initial) {
		hooked = new StringBuilder(initial);
		changed = true;
	}

	public YString add(String string)
	{
		changed = true;
		hooked.append(string);
		return this;
	}

	public YString add(YString string) {
		return add(string.toString());
	}

	public YString replaceFirst(char character, String toReplace) {
		char lastChar = 'Y';
		int lastIndex = 0;
		String text = currentValue();
		StringBuilder stringBuilder = new StringBuilder();
		boolean replaced = false;
		for (int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);
			if (currentChar == '\\' && text.length() > i + 1) {
				if (text.charAt(i + 1) != character)
					stringBuilder.append(text.charAt(i));
			} else if (lastChar != '\\' && currentChar == character) {
				stringBuilder.append(toReplace);
				lastIndex = i;
				replaced = true;
				break;
			} else {
				stringBuilder.append(text.charAt(i));
			}
			lastChar = text.charAt(i);
		}

		if (replaced && lastIndex  + 1 < text.length()) {
			stringBuilder.append(text.substring(lastIndex + 1));
		}
		return new YString(stringBuilder);
	}


	public String substring(int start) {
		return hooked.substring(start);
	}

	public YString substringY(int start) {
		return new YString(hooked.substring(start));
	}

	public YString substringY(int start, int end) {
		return new YString(hooked.substring(start, end));
	}


	public YString replace(String text, String toReplace) {
		hooked = new StringBuilder(hooked.toString().replaceAll(text, toReplace));
		changed = true;
		return this;
	}

	public YString remove(String text) {
		return replace(text, EMPTY_R);
	}

	public String substring(int start, int end) {
		return hooked.substring(start, end);
	}

	public List<String> lines() {
		String temp = currentValue().replace("\r\n", "\n");
		if (!temp.contains("\n")) {
			return Collections.singletonList(currentValue());
		}

		return Arrays.asList(temp.split("\n"));
	}

	@Override
	public String toString() {
		return currentValue();
	}

	protected String currentValue() {
		return  changed ? (currentValue = hooked.toString()) : currentValue;
	}
}
