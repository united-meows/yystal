package pisi.unitedmeows.yystal.ui.element;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.font.TTFRenderer;
import pisi.unitedmeows.yystal.utils.Vector4;

import java.awt.*;

public class YLabel extends YElement {

    public prop<String> text = new prop<>();
    public prop<Color> color = new prop<>(Color.BLACK);

    private TTFRenderer ttfRenderer;

    public YLabel() {
        this("Label");
    }

    public YLabel(String _text) {
        text.set(_text);
    }

    @Override
    public void setup() {
        currentWindow.get().executeOnThread(() ->
        {
            font("samsung");
            show = true;
        });
    }

    @Override
    public void draw() {
        int color2 = isMouseOver.get() ? 0xffff0000 : color.get().getRGB();
        ttfRenderer.drawString(text.get(), location().x, location().y, color2, false);
        size.setX(ttfRenderer.width(text.get()));
        size.setY(ttfRenderer.height(text.get()));
    }

    public void font(String name) {
        final Vector4<Float, Float, Float, Float> vector = new Vector4<>(1F, 31F, 1F, 30F);
        ttfRenderer = new TTFRenderer(name, vector, true);

    }


}
