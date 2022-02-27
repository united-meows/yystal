package pisi.unitedmeows.yystal.ui.font;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;

import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.font.data.CFont;
import pisi.unitedmeows.yystal.ui.font.data.CharInfo;
// TODO: add alpha support

public class Batch {
	private final int[] indices = { 0, 1, 3, 1, 2, 3 };
	public static int BATCH_SIZE = 100 , VERTEX_SIZE = 7;
	public float[] vertices = new float[BATCH_SIZE * VERTEX_SIZE];
	private final Matrix4f projection = new Matrix4f();
	public int vao , vbo , size;
	public Shader shader , sdfShader;
	public CFont font;
	public YWindow yWindow;

	public void generateEbo() {
		final int elementSize = BATCH_SIZE * 3;
		final int[] elementBuffer = new int[elementSize];
		for (int i = 0; i < elementSize; i++) { elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4); }
		final int ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
	}

	public void initBatch(final YWindow yWindow) {
		this.yWindow = yWindow;
		projection.identity();
		projection.ortho(0, yWindow.width(), 0, yWindow.height(), 1F, 100F);
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
		generateEbo();
		final int stride = 7 * Float.BYTES;
		glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 5 * Float.BYTES);
		glEnableVertexAttribArray(2);
	}

	public void flushBatch() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		shader.use();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_BUFFER, font.textureId);
		sdfShader.uploadTexture("uFontTexture", 0);
		sdfShader.uploadMat4f("uProjection", projection);
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, size * 6, GL_UNSIGNED_INT, 0);
		size = 0;
	}

	public void addCharacter(final float x, final float y, final float scale, final CharInfo charInfo, final int rgb) {
		if (size >= BATCH_SIZE - 4) {
			flushBatch();
		}
		final float r = ((rgb >> 16) & 0xFF) / 255.0F;
		final float g = ((rgb >> 8) & 0xFF) / 255.0F;
		final float b = ((rgb >> 0) & 0xFF) / 255.0F;
		final float x0 = x;
		final float y0 = y;
		final float x1 = x + scale * charInfo.width;
		final float y1 = y + scale * charInfo.height;
		final float ux0 = charInfo.textureCoordinates[0].getX();
		final float uy0 = charInfo.textureCoordinates[0].getY();
		final float ux1 = charInfo.textureCoordinates[1].getX();
		final float uy1 = charInfo.textureCoordinates[1].getY();
		int index = size * 7;
		vertices[index] = x1;
		vertices[index + 1] = y0;
		vertices[index + 2] = r;
		vertices[index + 3] = g;
		vertices[index + 4] = b;
		vertices[index + 5] = ux1;
		vertices[index + 6] = uy0;
		index += 7;
		vertices[index] = x1;
		vertices[index + 1] = y1;
		vertices[index + 2] = r;
		vertices[index + 3] = g;
		vertices[index + 4] = b;
		vertices[index + 5] = ux1;
		vertices[index + 6] = uy1;
		index += 7;
		vertices[index] = x0;
		vertices[index + 1] = y1;
		vertices[index + 2] = r;
		vertices[index + 3] = g;
		vertices[index + 4] = b;
		vertices[index + 5] = ux0;
		vertices[index + 6] = uy1;
		index += 7;
		vertices[index] = x0;
		vertices[index + 1] = y0;
		vertices[index + 2] = r;
		vertices[index + 3] = g;
		vertices[index + 4] = b;
		vertices[index + 5] = ux0;
		vertices[index + 6] = uy0;
		size += 4;
	}

	public void addText(final String text, int x, final int y, final float scale, final int rgb) {
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			final CharInfo charInfo = font.getCharacter(c);
			if (charInfo.width == 0) {
				System.out.println("Unknown character " + c);
				continue;
			}
			final float xPos = x;
			final float yPos = y;
			addCharacter(xPos, yPos, scale, charInfo, rgb);
			x += charInfo.width * scale;
		}
	}
}
