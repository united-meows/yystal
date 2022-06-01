package pisi.unitedmeows.yystal.ui.element;

import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.ui.utils.YOrigin;
import pisi.unitedmeows.yystal.utils.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class YContainer extends YElement {

	protected Vertex2f location;
	protected YOrigin origin;
	private final List<YElement> elements;


	public YContainer(Vertex2f _location, Vector2f _size, YOrigin _origin) {
		location = _location;
		size = _size;
		origin = _origin;
		elements = new ArrayList<>();
	}


	public YOrigin origin() {
		return origin;
	}


	public Vertex2f location() {
		return location;
	}

	public void setLocation(Vertex2f location) {
		this.location = location;
	}

	public void setOrigin(YOrigin origin) {
		this.origin = origin;
	}

}
