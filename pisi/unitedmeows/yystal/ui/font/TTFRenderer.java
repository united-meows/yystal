package pisi.unitedmeows.yystal.ui.font;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import pisi.unitedmeows.yystal.ui.font.GlyphCache.Glyph;
import pisi.unitedmeows.yystal.ui.font.StringCache.Entry;
import pisi.unitedmeows.yystal.utils.Vector4;

// TODO generate faster using async
// TODO fix unicode rendering issues
// TODO getWidth, getHeight, line seperation...
public class TTFRenderer {
	public final Map<Float, StringCache> caches = new HashMap<>();
	public final float defaultSize , increment;
	public final StringCache defaultCache;

    public TTFRenderer(String name, float _sizeStart, float _maxSize, float _increment, float _defaultSize, boolean _antiAlias) {
        defaultSize = _defaultSize;
        increment = _increment;
        for (float f = _sizeStart; f < _maxSize; f += increment) {
            StringCache cache = new StringCache();
            cache.setDefaultFont(name, f, _antiAlias);
            caches.put(f, cache);
        }
        defaultCache = caches.get(defaultSize);
    }

    public TTFRenderer(Font font,float _sizeStart, float _maxSize, float _increment, float _defaultSize, boolean _antiAlias) {
        defaultSize = _defaultSize;
        increment = _increment;
        for (float f = _sizeStart; f < _maxSize; f += increment) {
            StringCache cache = new StringCache();
            cache.setDefaultFont(font, f, _antiAlias);
            caches.put(f, cache);
        }
        defaultCache = caches.get(defaultSize);
    }

    public TTFRenderer(InputStream stream, float _sizeStart, float _maxSize, float _increment, float _defaultSize, boolean _antiAlias) {
        defaultSize = _defaultSize;
        increment = _increment;
        for (float f = _sizeStart; f < _maxSize; f += increment) {
            StringCache cache = new StringCache();
            cache.setDefaultFont(stream, f, _antiAlias);
            caches.put(f, cache);
        }
        defaultCache = caches.get(defaultSize);
    }

    public float drawStringCentered(String text, double x, double y, int color) {
        float width = width(text);
        drawString(text, x - width / 2, y, color);
        return (float) (x + width / 2);
    }

    public float drawStringCentered(String text, double x, double y, int color, DrawType type) {
        float width = width(text);
        drawString(text, x - width / 2, y, color, type);
        return (float) (x + width / 2);
    }

    public float drawString(String text, double x, double y, int color, DrawType type) {
        switch (type) {
            case NORMAL: {
                return drawString(text, x, y, color);
            }
            case SHADOW_THIN: {
                drawString(text, x + 1, y + 1, color & 0xFF000000);
                return drawString(text, x, y, color);
            }
            case SHADOW_THICK: {;
                drawString(text, x - 1, y - 1, color & 0xFF000000);
                drawString(text, x - 1, y + 1, color & 0xFF000000);
                drawString(text, x + 1, y - 1, color & 0xFF000000);
                drawString(text, x + 1, y + 1, color & 0xFF000000);
                return drawString(text, x, y, color);
            }
        }

        return 0;
    }

	public float drawString(String text, double x, double y, int color) {
		if (text == null) return 0F;
		text = text.replace("\u0000", "null_char");
		x -= 1;
		if (color == 553648127) {
			color = 16777215;
		}
		if ((color & 0xFC000000) == 0) {
			color |= -16777216;
		}

		float scale = GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX);
		float calculate = increment * (Math.round((defaultSize * scale) / increment));
		StringCache current = caches.getOrDefault(calculate, defaultCache);
		final Entry entry = current.cacheString(text);
		final double antiAlias = scale * 2D;
		x *= antiAlias;
		y = (y - 3.0D) * antiAlias;
		GL11.glPushMatrix();
		GL11.glScaled(1 / antiAlias, 1 / antiAlias, 1 / antiAlias);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		for (int glyphIndex = 0; glyphIndex < entry.glyphs.length; glyphIndex++) {
			final Glyph glyph = entry.glyphs[glyphIndex];
			pisi.unitedmeows.yystal.ui.font.GlyphCache.Entry texture = glyph.texture;
			final float factor = .5F;
			int glyphX = glyph.x;
			final char c = text.charAt(glyph.stringIndex);
			if (c >= '0' && c <= '9') {
				final int oldWidth = texture.width;
				texture = current.digitGlyphs[0][c - '0'].texture;
				final int newWidth = texture.width;
				glyphX += (oldWidth - newWidth) >> 1;
			}
			final float x1 = (glyphX) / factor;
			final float x2 = (glyphX + texture.width) / factor;
			final float y1 = (glyph.y) / factor;
			final float y2 = (glyph.y + texture.height) / factor;
			final int r = color >> 16 & 0xff;
			final int g = color >> 8 & 0xff;
			final int b = color & 0xff;
			final int a = color >> 24 & 0xff;
			final float minU = texture.u1;
			final float minV = texture.v1;
			final float maxU = texture.u2;
			final float maxV = texture.v2;
			final float calcX1 = (float) (x + x1);
			final float calcY1 = (float) (y + y1);
			final float calcX2 = (float) (x + x2);
			final float calcY2 = (float) (y + y2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureName);
			GL11.glBegin(7);
			GL11.glColor4f(r, g, b, a);
			GL11.glTexCoord2f(minU, minV);
			GL11.glVertex2d(calcX1, calcY1);
			GL11.glTexCoord2f(minU, maxV);
			GL11.glVertex2d(calcX1, calcY2);
			GL11.glTexCoord2f(maxU, maxV);
			GL11.glVertex2d(calcX2, calcY2);
			GL11.glTexCoord2f(maxU, minV);
			GL11.glVertex2d(calcX2, calcY1);
			GL11.glEnd();
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
		GL11.glPopMatrix();
		return (float) x;
	}


    public float height(String text) {
        if (text == null || text.isEmpty()) return 0;
        float scale = GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX);
        final StringCache.Entry entry = caches.get(scale).cacheString(text);
        float biggestDif = -1.0F;
        for (int glyphIndex = 0; glyphIndex < entry.glyphs.length; glyphIndex++) {
            final GlyphCache.Glyph glyph = entry.glyphs[glyphIndex];
            final GlyphCache.Entry entry2 = glyph.texture;
            final float height = entry2.height;
            if (height > biggestDif) {
                biggestDif = height;
            }
        }
        return biggestDif * defaultSize;
    }

    public float width(String text) {
        if (text == null) return 0F;
        text = text.replace("\u0000", "null_char");
        float scale = GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX);
        float calculate = increment * (Math.round((defaultSize * scale) / increment));
        StringCache current = caches.getOrDefault(calculate, defaultCache);
        final Entry entry = current.cacheString(text);
        final double antiAlias = scale * 2D;
        return (float) entry.advance;
    }

    public Float defaultSize() {
        return defaultSize;
    }

    public enum DrawType {
        NORMAL,
        SHADOW_THIN,
        SHADOW_THICK
    }
}
