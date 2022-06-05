package pisi.unitedmeows.yystal.ui.utils.keyboard;

import java.util.HashMap;
import java.util.Map;

public class YKeyManager {

    private Map<Integer, Boolean> keyMap;

    public YKeyManager() {
        keyMap = new HashMap<>();
    }

    public boolean isPressed(int key) {
        return keyMap.containsKey(key);
    }

    public void press(int key) {
        keyMap.put(key, true);
    }

    public void release(int key) {
        keyMap.remove(key);
    }

}