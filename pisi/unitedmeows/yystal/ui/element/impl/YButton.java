package pisi.unitedmeows.yystal.ui.element.impl;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.prop;
import pisi.unitedmeows.yystal.parallel.repeaters.Repeater;
import pisi.unitedmeows.yystal.parallel.utils.YTimer;
import pisi.unitedmeows.yystal.ui.element.YElement;
import pisi.unitedmeows.yystal.ui.events.ButtonClickEvent;
import pisi.unitedmeows.yystal.ui.font.TTFRenderer;
import pisi.unitedmeows.yystal.ui.utils.RenderMethods;
import pisi.unitedmeows.yystal.utils.Vector4;

import java.awt.*;
import java.util.function.Supplier;

public class YButton extends YElement {

    public prop<String> text = new prop<>("Button");
    public prop<Boolean> autoSize = new prop<>(false);
    public prop<Color> color = new prop<>(Color.BLACK);

    public event<ButtonClickEvent> clickEvent = new event<>();

    private TTFRenderer ttfRenderer;

    private Repeater repeater = YYStal.repeater(50)
            .onTick(() -> {

            });

    private YTimer timer = new YTimer(20) {
        @Override
        public void tick() {

        }
    };

    @Override
    public void setup() {
        boolean wasHidden = !show;
        show = false;
        font(() -> {
            TTFRenderer renderer = new TTFRenderer("samsung", 1F, 20F, 1F, 14F, true);
            if (!wasHidden) {
                show = true;
            }
            return renderer;
        });
    }

    @Override
    public void draw() {
        RenderMethods.drawRoundedBorderedRect(renderX(), renderY(), renderX() + size.x(), renderY() + size.y(),
                1F, isMouseOver.get() ? 0xfff2f1f1 : 0xfffcfaff, isMouseOver.get() ? 0xffadacac : 0xffd9dadb, 0xffadadad);
        ttfRenderer.drawStringCentered(text.get(), renderX() + size.x() / 2 + 2, renderY() + size.y() / 2 + ttfRenderer.defaultSize() / 2, color.get().getRGB());
    }

    @Override
    public void _mouseRelease() {
        clickEvent.fire();
        super._mouseRelease();
    }

    @Override
    public void tick() {
        if (autoSize.get()) {
            float width = ttfRenderer.width(text.get()) + 16;
            float height = ttfRenderer.defaultSize() + 4;
            size(width, height);
        }
        super.tick();
    }

    public void font(Supplier<TTFRenderer> fontSupplier) {
        currentWindow.get().executeOnThread(() -> ttfRenderer = fontSupplier.get());
    }
}
