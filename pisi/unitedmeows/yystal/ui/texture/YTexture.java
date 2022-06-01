package pisi.unitedmeows.yystal.ui.texture;

import org.lwjgl.opengl.GL11;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;
import pisi.unitedmeows.yystal.utils.IDisposable;

import static org.lwjgl.opengl.GL11.*;

public class YTexture implements IDisposable {

    private int textureId;
    private boolean loaded;

    public YTexture(int _textureId) {
        textureId = _textureId;
        loaded = true;
    }

    @Override
    public void close() {
        /* unload */
        loaded = false;
    }

    public int textureId() {
        return textureId;
    }

    public boolean loaded() {
        return loaded;
    }

    public void draw(float x, float y, float width, float height) {
        RenderMethods.drawImage(textureId(), x, y, width, height);
    }
}
