package pisi.unitedmeows.yystal.ui.utils;

public class Vertex3f {
	public float x, y, z;

	public Vertex3f(float _x, float _y, float _z) {
		x = _x;
		y = _y;
		z = _z;
	}

	public float x() {
		return x;
	}

	public float y() {
		return y;
	}

	public float z() {
		return z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}
}
