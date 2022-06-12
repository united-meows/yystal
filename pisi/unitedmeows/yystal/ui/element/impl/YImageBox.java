package pisi.unitedmeows.yystal.ui.element.impl;

import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.YUI;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.texture.YTexture;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;
import pisi.unitedmeows.yystal.ui.utils.Vertex2f;
import pisi.unitedmeows.yystal.utils.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;

public class YImageBox extends YElement {

    private YTexture imageTexture;

    public YImageBox() {
        size(new Vertex2f(100, 100));
    }

    public prop<BufferedImage> image = new prop<BufferedImage>(null) {
        @Override
        public void set(BufferedImage newValue) {
            value = newValue;
            currentWindow.get().executeOnThread(() -> {
                if (imageTexture != null) {
                    imageTexture.close();
                }
                imageTexture = YUI.loadTexture(newValue);
                System.out.println(imageTexture == null);
            });
        }
    };

    @Override
    public void draw() {
        if (imageTexture != null)
            RenderMethods.drawRect(renderX(), renderY(), renderX() + size().x(),
                    renderY() + size().y(), Color.GREEN.getRGB());
    }

}
