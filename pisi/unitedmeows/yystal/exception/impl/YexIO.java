package pisi.unitedmeows.yystal.exception.impl;

import pisi.unitedmeows.yystal.exception.YEx;

public class YexIO extends YEx {

	private final String message;

	public YexIO(String _message, Object... args) {
		message = String.format(_message, args);
	}

	public YexIO() {
		this("Received IO Exception");
	}

	@Override
	public String getMessage() {
		return message;
	}
}
