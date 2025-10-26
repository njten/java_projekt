package core;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class InputHandler {
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public void handleKeyPressed(KeyEvent e) {
        pressedKeys.add(e.getCode());
    }

    public void handleKeyReleased(KeyEvent e) {
        pressedKeys.remove(e.getCode());
    }

    public boolean isPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public Set<KeyCode> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    public void clear() {
        pressedKeys.clear();
    }
}