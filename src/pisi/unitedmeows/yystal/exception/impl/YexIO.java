package pisi.unitedmeows.yystal.exception.impl;

import pisi.unitedmeows.yystal.exception.YEx;

public class YexIO extends YEx {

	private final String message;

	public YexIO(String _message) {
		message = _message;
	}

	public YexIO() {
		this("Received IO Exception");
	}

	@Override
	public String getMessage() {
		return message;
	}
}
