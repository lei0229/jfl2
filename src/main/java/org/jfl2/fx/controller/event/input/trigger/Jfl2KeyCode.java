package org.jfl2.fx.controller.event.input.trigger;

import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * キーコードによる指定用
 */
@Slf4j
public class Jfl2KeyCode extends Jfl2Key {
    protected KeyCode code;

    public Jfl2KeyCode(KeyCode code, EventType<KeyEvent> keyEvent, int modifier) {
        super(keyEvent, modifier);
        this.code = code;
    }

    /**
     * このInputEventで処理する場合はtrueを返す
     *
     * @param event
     * @return
     */
    @Override
    public boolean isHandle(KeyEvent event) {
        return code.equals(event.getCode()) && super.isHandle(event);
    }
}
