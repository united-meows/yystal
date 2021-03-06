package pisi.unitedmeows.yystal.ui.element;

import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.YUI;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.events.MouseEvent;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.ui.utils.YOrigin;
import pisi.unitedmeows.yystal.utils.Vector2f;

public abstract class YElement implements IBackground
{
	protected Vertex2f location = new Vertex2f(0, 0);

	private YOrigin origin = YOrigin.TOP_LEFT;
    protected Vertex2f size = new Vertex2f(0, 0);
    protected boolean show = true;
//    public event<MouseEnterEvent>
//    public event<MouseLeaveEvent>
//    public event<ClickEvent>
    public event<MouseEvent> mouseEvent = new event<>();
    public prop<Integer> zLevel = new prop<>(1);
    private boolean focused;

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

    public void setup() {}
    public void tick() {}

    @Deprecated public void _mouseClick() {}
    @Deprecated public void _mouseRelease() {}

	public abstract void draw();

    public boolean isMouseOver(final float mouseX, final float mouseY) {
        return mouseX >= renderX() && mouseX <= renderX() + size.x() && mouseY >= renderY() && mouseY <= renderY() + size.y();
    }

	public Vertex2f location() { return location; }

	public float renderX() { return container.renderX() + location.x; }

    public float renderY() { return container.renderY() + location.y; }

    public YElement container(YContainer _container) {
        container = _container;
        return this;
    }

    public YContainer container() { return container; }

    public YElement instance() { return this; }

    public Vertex2f size() { return size; }

    public YElement size(float _x, float _y) {
        size.setX(_x);
        size.setY(_y);
        return this;
    }

    public YElement size(Vertex2f _size) {
        size = _size;
        return this;
    }

    public void show() { show = true; }

    public void hide() { show = false; }

    public boolean isShown() {
        return show;
    }

    public void dock(YElement otherElement) {
        size = otherElement.size;
        location = otherElement.location;
    }

    public boolean focused() {
        return focused;
    }

    public YElement focus() {
        focused = true;
        return this;
    }

    public YElement location(Vertex2f _location) {
        location = _location;
        return this;
    }

    public YElement origin(YOrigin _origin) {
        origin = _origin;
        return this;
    }


}
