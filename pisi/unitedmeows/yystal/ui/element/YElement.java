package pisi.unitedmeows.yystal.ui.element;

import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.ui.utils.YOrigin;

public abstract class YElement
{
	private Vertex2f location;
	private YOrigin origin;
	protected YContainer container;

	public final prop<YWindow> currentWindow = new prop<YWindow>(null) {
		@Override
		public YWindow get() {
			if (value == null) {
				YElement owner = instance();
				while (owner.container != null) {
					owner = owner.container;
				}
				if (owner instanceof YWindow) {
					value = (YWindow) owner;
				}
			}
			return value;
		}

		@Deprecated
		@Override
		public void set(YWindow newValue) { }
	};

	public final prop<Boolean> isMouseOver = new prop<Boolean>() {
		@Override
		public Boolean get() {
			YWindow window = currentWindow.get();
			if (window != null) {
				return isMouseOver(window.mouseX(), window.mouseY());
			}
			return false;
		}

		@Deprecated
		@Override
		public void set(Boolean newValue) {}
	};

	public abstract void draw();
	public abstract boolean isMouseOver(final float mouseX, final float mouseY);

	public Vertex2f location() {
		return location;
	}

	public float getRenderX() {
		switch (origin) {

		}
		return -1;
	}

	public YElement instance() {
		return this;
	}

}
