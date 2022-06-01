package pisi.unitedmeows.yystal.ui.element.simple;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.ui.YUI;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.texture.YTexture;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;

import java.awt.image.BufferedImage;

public class YBackgroundImage extends YElement {

    private int textureId = -1;

    public YBackgroundImage(BufferedImage image) {
        textureId = YUI.loadTexture(image).textureId();
    }

    public YBackgroundImage(YTexture _texture) {
        textureId = _texture.textureId();
    }

    public YBackgroundImage(String textureName) {
        textureId = YUI.texture(textureName).textureId();
    }

    @Override
    public void draw() {
        RenderMethods.drawImage(textureId, container.renderX(), container.renderY(), container.size().getX(), container.size().getY());
    }

    @Override
    public boolean isMouseOver(float mouseX, float mouseY) {
        return false;
    }
}
