package pisi.unitedmeows.yystal.ui.font;

import static com.mlomb.freetypejni.FreeType.FT_Done_Face;
import static com.mlomb.freetypejni.FreeType.FT_Done_FreeType;
import static com.mlomb.freetypejni.FreeType.FT_Load_Char;
import static com.mlomb.freetypejni.FreeType.FT_Set_Pixel_Sizes;
import static com.mlomb.freetypejni.FreeTypeConstants.FT_LOAD_RENDER;
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.mlomb.freetypejni.Face;
import com.mlomb.freetypejni.FreeType;
import com.mlomb.freetypejni.Library;

public class Sdf {
	public static int textureId = -1;

	private static float mapRange(final float val, final float in_min, final float in_max, final float out_min, final float out_max) { return (val - in_min) * (out_max - out_min) / (in_max - in_min) + out_min; }

	private static int getPixel(final int x, final int y, final byte[] bitmap, final int width, final int height) {
		if (x >= 0 && x < width && y >= 0 && y < height) return (bitmap[x + y * width] & 0xFF) == 0 ? 0 : 1;
		return 0;
	}

	private static float findNearestPixel(final int pixelX, final int pixelY, final byte[] bitmap, final int width, final int height, final int spread) {
		final int state = getPixel(pixelX, pixelY, bitmap, width, height);
		final int minX = pixelX - spread;
		final int maxX = pixelX + spread;
		final int minY = pixelY - spread;
		final int maxY = pixelY + spread;
		float minDistance = spread * spread;
		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				final int pixelState = getPixel(x, y, bitmap, width, height);
				final float dxSquared = (x - pixelX) * (x - pixelX);
				final float dySquared = (y - pixelY) * (y - pixelY);
				final float distanceSquared = dxSquared + dySquared;
				if (pixelState != state) {
					minDistance = Math.min(distanceSquared, minDistance);
				}
			}
		}
		minDistance = (float) Math.sqrt(minDistance);
		float output = (minDistance - 0.5f) / (spread - 0.5f);
		output *= state == 0 ? -1 : 1;
		// Map from [-1, 1] to [0, 1]
		return (output + 1) * 0.5f;
	}

	public static void generateCodepointBitmap(final int codepoint, final String fontFile, final int fontSize) {
		final int padding = 15;
		final int upscaleResolution = 1080;
		final int spread = upscaleResolution / 2;
		final Library library = FreeType.newLibrary();
		assert (library != null);
		final Face font = library.newFace(fontFile, 0);
		FT_Set_Pixel_Sizes(font.getPointer(), 0, upscaleResolution);
		if (FT_Load_Char(font.getPointer(), (char) codepoint, FT_LOAD_RENDER)) {
			System.out.println("FreeType could not generate character.");
			free(library, font);
			return;
		}
		final int glyphWidth = font.getGlyphSlot().getBitmap().getWidth();
		final int glyphHeight = font.getGlyphSlot().getBitmap().getRows();
		final byte[] glyphBitmap = new byte[glyphHeight * glyphWidth];
		font.getGlyphSlot().getBitmap().getBuffer().get(glyphBitmap, 0, glyphWidth * glyphHeight);
		System.out.println("Glyph width " + glyphWidth);
		System.out.println("Glyph Height " + glyphHeight);
		final float widthScale = (float) glyphWidth / (float) upscaleResolution;
		final float heightScale = (float) glyphHeight / (float) upscaleResolution;
		final int characterWidth = (int) (fontSize * widthScale);
		final int characterHeight = (int) (fontSize * heightScale);
		final int bitmapWidth = characterWidth + padding * 2;
		final int bitmapHeight = characterHeight + padding * 2;
		final float bitmapScaleX = (float) glyphWidth / (float) characterWidth;
		final float bitmapScaleY = (float) glyphHeight / (float) characterHeight;
		final int[] bitmap = new int[bitmapWidth * bitmapHeight];
		for (int y = -padding; y < characterHeight + padding; y++) {
			for (int x = -padding; x < characterWidth + padding; x++) {
				final int pixelX = (int) mapRange(x, -padding, characterWidth + padding, -padding * bitmapScaleX, (characterWidth + padding) * bitmapScaleX);
				final int pixelY = (int) mapRange(y, -padding, characterHeight + padding, -padding * bitmapScaleY, (characterHeight + padding) * bitmapScaleY);
				final float val = findNearestPixel(pixelX, pixelY, glyphBitmap, glyphWidth, glyphHeight, spread);
				bitmap[(x + padding) + ((y + padding) * bitmapWidth)] = (int) (val * 255.0f);
			}
		}
		final BufferedImage testImage = new BufferedImage(bitmapWidth, bitmapHeight, BufferedImage.TYPE_INT_ARGB);
		int x = 0;
		int y = 0;
		for (final int byteAsInt : bitmap) {
			final int argb = (255 << 24) | (byteAsInt << 16) | (byteAsInt << 8) | byteAsInt;
			testImage.setRGB(x, y, argb);
			x++;
			if (x >= bitmapWidth) {
				x = 0;
				y++;
			}
			if (y >= bitmapHeight) {
				break;
			}
		}
		try {
			final File output = new File("test.png");
			ImageIO.write(testImage, "png", output);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		uploadTexture(testImage);
		free(library, font);
	}

	private static void free(final Library library, final Face font) {
		FT_Done_Face(font.getPointer());
		FT_Done_FreeType(library.getPointer());
	}

	private static void uploadTexture(final BufferedImage image) {
		// Taken from https://stackoverflow.com/questions/10801016/lwjgl-textures-and-strings
		final int[] pixels = new int[image.getHeight() * image.getWidth()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) { final int pixel = pixels[y * image.getWidth() + x]; final byte r = (byte) ((pixel >> 16) & 0xFF); buffer.put(r); buffer.put(r); buffer.put(r); buffer.put(r); }
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
