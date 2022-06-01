package pisi.unitedmeows.yystal.ui.element.simple;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;
import pisi.unitedmeows.yystal.utils.Vector2f;

import java.awt.*;

public class YBackgroundColor2 extends YElement {

    private int startColor;
    private int endColor;

    public YBackgroundColor2(Color _startColor, Color _endColor) {
        startColor = _startColor.getRGB();
        endColor = _endColor.getRGB();
    }

    @Override
    public void draw() {
        Vector2f size = container.size();
        RenderMethods.drawRect(container.renderX(), container.renderY(), size.getX(), size.getY(), startColor, endColor);
    }

    @Override
    public boolean isMouseOver(float mouseX, float mouseY) {
        return false;
    }
}
