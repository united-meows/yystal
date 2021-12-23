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
