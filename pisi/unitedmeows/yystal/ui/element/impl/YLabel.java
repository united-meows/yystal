package pisi.unitedmeows.yystal.ui.element.impl;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.font.TTFRenderer;
import pisi.unitedmeows.yystal.utils.Vector4;

import java.awt.*;
import java.util.function.Supplier;

public class YLabel extends YElement {

    public prop<String> text = new prop<>();
    public prop<Color> color = new prop<>(Color.BLACK);
    public prop<TTFRenderer.DrawType> drawType = new prop<>(TTFRenderer.DrawType.NORMAL);

    private TTFRenderer ttfRenderer;


    public YLabel() {
        this("Label");
    }

    public YLabel(String _text) {
        text.set(_text);
    }

    @Override
    public void setup() {
        boolean wasHidden = !show;
        show = false;
        font(() -> {
            TTFRenderer renderer = new TTFRenderer("samsung", 1f, 20f, 1f, 16f, true);
            if (!wasHidden) {
                show = true;
            }

            return renderer;
        });
    }

    @Override
    public void draw() {
        ttfRenderer.drawString(text.get(), location().x, location().y, color.get().getRGB(), drawType.get());
        size(ttfRenderer.width(text.get()), ttfRenderer.height(text.get()));
    }


    public void font(Supplier<TTFRenderer> fontSupplier) {
        currentWindow.get().executeOnThread(() -> ttfRenderer = fontSupplier.get());
    }


}
