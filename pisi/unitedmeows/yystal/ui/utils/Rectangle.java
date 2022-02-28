package pisi.unitedmeows.yystal.ui.utils;

import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex2d;

import org.joml.Vector4f;

public class Rectangle {
	public Vector4f vector;

	public Rectangle(final Vector4f vector) {
		this.vector = vector;
	}

	public Rectangle(final float x, final float y, final float z, final float w) {
		this.vector = new Vector4f(x, y, z, w);
	}

	public Rectangle(final float[] xyzw) {
		this.vector = new Vector4f(xyzw[0], xyzw[1], xyzw[2], xyzw[3]);
	}

	public Rectangle expand(final float x, final float y, final float x2, final float y2) {
		vector.x += x;
		vector.y += y;
		vector.z += x2;
		vector.w += y2;
		return this;
	}

	public void draw(final int color) {
		draw(color, color);
	}

	public void draw(final int color, final int endColor) {
		glEnable(GL_LINE_SMOOTH);
		glShadeModel(GL_SMOOTH);
		glPushMatrix();
		glBegin(GL_QUADS);
		glColor4f((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F,
					(color >> 24 & 255) / 255.0F);
		glVertex2d(vector.x, vector.y);
		glVertex2d(vector.x, vector.w);
		glColor4f((endColor >> 16 & 255) / 255.0F, (endColor >> 8 & 255) / 255.0F,
					(endColor & 255) / 255.0F, (endColor >> 24 & 255) / 255.0F);
		glVertex2d(vector.z, vector.w);
		glVertex2d(vector.z, vector.y);
		glEnd();
		glPopMatrix();
		glDisable(GL_LINE_SMOOTH);
		glShadeModel(GL_FLAT);
	}

	@Override
	public int hashCode() {
		return vector == null ? -1 : vector.hashCode();
	}
}
