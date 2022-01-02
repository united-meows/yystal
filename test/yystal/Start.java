package test.yystal;

import pisi.unitedmeows.yystal.ui.YWindow;

public enum Start {
	gaming;

	public static void main(final String[] args) {
		final YWindow yWindow = new YWindow("coding", 1000, 500);
		yWindow.open();
	}
}
