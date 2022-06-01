package pisi.unitedmeows.yystal.ui.element.simple;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;

import java.awt.*;

public class YBackgroundColor extends YElement {

    private int color;
    public YBackgroundColor(Color _color) {
        color = _color.getRGB();
    }

    @Override
    public void draw() {
        RenderMethods.drawRect(container.renderX(), container.renderY(), container.size().getX(), container.size().getY(), color);
    }

    @Override
    public boolean isMouseOver(float mouseX, float mouseY) {
        return false;
    }
}
