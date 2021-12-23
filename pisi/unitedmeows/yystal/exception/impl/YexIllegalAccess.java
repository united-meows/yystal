package pisi.unitedmeows.yystal.exception.impl;

import pisi.unitedmeows.yystal.exception.YEx;

public class YexIllegalAccess extends YEx {

	private final String message;
	public YexIllegalAccess(final String _message) {
		message = _message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
