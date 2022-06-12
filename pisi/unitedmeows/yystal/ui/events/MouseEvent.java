package pisi.unitedmeows.yystal.ui.events;

import com.sun.istack.internal.Nullable;
import org.lwjgl.glfw.GLFW;
import pisi.unitedmeows.yystal.clazz.delegate;

import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface MouseEvent extends delegate {

    void onMouseClick(Button button, Action action, int mods);

    enum Button {


        MOUSE1(GLFW.GLFW_MOUSE_BUTTON_1),
        MOUSE2(GLFW.GLFW_MOUSE_BUTTON_2),
        MOUSE3(GLFW.GLFW_MOUSE_BUTTON_3),
        MOUSE4(GLFW.GLFW_MOUSE_BUTTON_4),
        MOUSE5(GLFW.GLFW_MOUSE_BUTTON_5),
        MOUSE6(GLFW.GLFW_MOUSE_BUTTON_6),
        MOUSE7(GLFW.GLFW_MOUSE_BUTTON_7),
        MOUSE8(GLFW.GLFW_MOUSE_BUTTON_8);

        private static Map<Integer, Button> map = new HashMap<>();


        static {
            for (Button button : Button.values()) {
                map.put(button.id, button);
            }
        }

        int id;
        Button(int _id) {
            id = _id;
        }

        @Nullable
        public static Button parse(int key) {
            return map.getOrDefault(key, null);
        }

    }

    enum Action {

        PRESS(GLFW.GLFW_PRESS),
        RELEASE(GLFW.GLFW_RELEASE);


        private static Map<Integer, Action> map = new HashMap<>();


        static {
            for (Action button : Action.values()) {
                map.put(button.id, button);
            }
        }

        int id;
        Action(int _id) {
            id = _id;
        }

        public int id() {
            return id;
        }

        @Nullable
        public static Action parse(int key) {
            return map.getOrDefault(key, null);
        }
    }

}