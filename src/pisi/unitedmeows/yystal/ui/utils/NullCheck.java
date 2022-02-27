package pisi.unitedmeows.yystal.ui.utils;

import org.lwjgl.system.MemoryUtil;

public enum NullCheck {
	CHECK;

	public boolean isNull(final Object object) {
		if (object == null) return true;
		if ((object instanceof Long) && ((Long) object == MemoryUtil.NULL)) return true;
		if (object instanceof Rectangle) {
			final Rectangle rectangle = (Rectangle) object;
			if (rectangle.hashCode() == -1) return true;
		}
		return false;
	}
}
