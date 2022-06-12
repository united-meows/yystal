package pisi.unitedmeows.yystal.ui.element;

import javafx.scene.layout.Background;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.element.simple.YBackgroundColor;
import pisi.unitedmeows.yystal.ui.events.MouseEvent;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.ui.utils.YOrigin;
import pisi.unitedmeows.yystal.ui.utils.yuiprop;
import pisi.unitedmeows.yystal.utils.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class YContainer extends YElement {

	protected YOrigin origin;
	private final List<YElement> elements;
    private YContainer instance;

    public prop<IBackground> background = new prop<IBackground>(new YBackgroundColor(Color.WHITE)) {
        @Override
        public void set(IBackground newValue) {
            super.set(newValue);
            if (newValue instanceof YElement) {
                ((YElement) background.get()).container(instance);
                ((YElement) background.get()).setup();
            }
        }
    };

	public YContainer(Vertex2f _location, Vertex2f _size, YOrigin _origin) {
		location = _location;
		size = _size;
		origin = _origin;
		elements = new ArrayList<>();
        instance = this;
	}

    public YContainer(Vertex2f _location, Vertex2f _size) {
        this(_location, _size, YOrigin.TOP_LEFT);
    }

    @Override
    public void draw() {
        background.get().draw();
        for (YElement element : elements) {
            if (element.isShown()) {
                element.draw();
            }
        }
    }

    @Override
    public boolean isMouseOver(float mouseX, float mouseY) {
        return false;
    }


    @Override
    public void setup() {

        if (background.get() instanceof YElement) {
            ((YElement) background.get()).container(this);
        }

        super.setup();
    }

    public YOrigin origin() {
		return origin;
	}

    public void addElement(YElement element) {
        element.container(this);
        element.setup();
        elements.add(element);

        //todo: maybe this sort should be in reverse?
        elements.sort(Comparator.comparingInt(o -> o.zLevel.get()));
        element.show();
    }

	public Vertex2f location() {
		return location;
	}

    public List<YElement> elements() {
        return elements;
    }

    public void setLocation(Vertex2f location) {
		this.location = location;
	}

	public void setOrigin(YOrigin origin) {
		this.origin = origin;
	}

}
