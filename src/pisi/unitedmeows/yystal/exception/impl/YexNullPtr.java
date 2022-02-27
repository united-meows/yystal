package pisi.unitedmeows.yystal.exception.impl;

import pisi.unitedmeows.yystal.exception.YEx;

public class YexNullPtr extends YEx {

	private final String message;

	public YexNullPtr(String _message) {
		message = _message;
	}

	public YexNullPtr() {
		this("Received Null Exception");
	}

	@Override
	public String getMessage() {
		return message;
	}
}
