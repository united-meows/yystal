package pisi.unitedmeows.yystal.sql;

import pisi.unitedmeows.yystal.clazz.HookClass;
import pisi.unitedmeows.yystal.hook.YString;

import java.util.Date;

public class YSQLCommand extends HookClass<String> {

	public YSQLCommand(String input) {
		hooked = input;
	}

	public YSQLCommand putString(String data) {
		putRaw(String.format("'%s'", data));
		return this;
	}

	public YSQLCommand putInt(int data) {
		return putRaw(data);
	}	

	public YSQLCommand putDouble(double data) {
		return putRaw(data);
	}

	public YSQLCommand putFloat(float data) {
		return putRaw(data);
	}

	public YSQLCommand putBool(boolean bool) {
		return putRaw(bool ? 1 : 0);
	}

	public YSQLCommand putDate(Date date) {
		// ?
		return this;
	}

	public YSQLCommand put(Object data) {
		if (data instanceof String) {
			return putString((String)data);
		} else if (data instanceof Integer) {
			return putInt((int) data);
		} else if (data instanceof Boolean) {
			return putBool((boolean) data);
		} else {
			return putRaw(data);
		}
	}

	public YSQLCommand putRaw(Object data) {
		hooked = new YString(hooked).replaceFirst('^', String.valueOf(data)).toString();
		return this;
	}

	@Override
	public String getHooked() {
		return super.getHooked();
	}
}
