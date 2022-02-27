package pisi.unitedmeows.yystal.ui.font.data;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

public class CFont {
	private final String filepath;
	private final int fontSize;
	private int width , height , lineHeight;
	private final Map<Integer, CharInfo> characterMap;
	public int textureId;

	public CFont(final String filepath, final int fontSize) {
		this.filepath = filepath;
		this.fontSize = fontSize;
		this.characterMap = new HashMap<>();
		generateBitmap();
	}

	public CharInfo getCharacter(final int codepoint) { return characterMap.getOrDefault(codepoint, new CharInfo(0, 0, 0, 0)); }

	private Font registerFont(final String fontFile) {
		try {
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final Font font = Font.createFont(Font.TRUETYPE_FONT, new File(filepath));
			ge.registerFont(font);
			return font;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void generateBitmap() {
		Font font = registerFont(filepath);
		if (font == null) {
			System.err.println("Font is null @ CFont#generateBitmap");
			return;
		}
		font = new Font(font.getName(), Font.PLAIN, fontSize);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		final FontMetrics fontMetrics = g2d.getFontMetrics();
		final int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
		width = 0;
		height = fontMetrics.getHeight();
		lineHeight = fontMetrics.getHeight();
		int x = 0;
		int y = (int) (fontMetrics.getHeight() * 1.4F);
		for (int i = 0; i < font.getNumGlyphs(); i++) {
			if (font.canDisplay(i)) {
				final CharInfo charInfo = new CharInfo(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight());
				characterMap.put(i, charInfo);
				width = Math.max(x + fontMetrics.charWidth(i), width);
				x += charInfo.width;
				if (x > estimatedWidth) {
					x = 0;
					y += fontMetrics.getHeight() * 1.4F;
					height += fontMetrics.getHeight() * 1.4F;
				}
			}
		}
		height += fontMetrics.getHeight() * 1.4F;
		g2d.dispose();
		// Create the real texture
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);
		for (int i = 0; i < font.getNumGlyphs(); i++) {
			if (font.canDisplay(i)) {
				final CharInfo info = characterMap.get(i);
				info.calculateTextureCoordinates(width, height);
				g2d.drawString("" + (char) i, info.sourceX, info.sourceY);
			}
		}
		g2d.dispose();
		uploadTexture(img);
	}

	private void uploadTexture(final BufferedImage image) {
		// Taken from https://stackoverflow.com/questions/10801016/lwjgl-textures-and-strings
		final int[] pixels = new int[image.getHeight() * image.getWidth()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				final int pixel = pixels[y * image.getWidth() + x];
				final byte alphaComponent = (byte) ((pixel >> 24) & 0xFF);
				buffer.put(alphaComponent);
				buffer.put(alphaComponent);
				buffer.put(alphaComponent);
				buffer.put(alphaComponent);
			}
		}
		buffer.flip();
		textureId = glGenTextures();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		buffer.clear();
	}
}
