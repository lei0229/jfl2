package org.jfl2.fx.controller.event.input.trigger;

import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 全てのキーの入力取得用
 */
@Slf4j
public class Jfl2AnyKey extends Jfl2Key {

    static public Jfl2AnyKey KEY_ANY = new Jfl2AnyKey(KeyEvent.ANY);
    static public Jfl2AnyKey KEY_PRESSED = new Jfl2AnyKey(KeyEvent.KEY_PRESSED);
    static public Jfl2AnyKey KEY_RELEASED = new Jfl2AnyKey(KeyEvent.KEY_RELEASED);
    static public Jfl2AnyKey KEY_TYPED = new Jfl2AnyKey(KeyEvent.KEY_TYPED);

    private Jfl2AnyKey(EventType<KeyEvent> eventType) {
        super(eventType, Jfl2Key.NONE);
    }

    /**
     * このInputEventで処理する場合はtrueを返す
     *
     * @param event
     * @return
     */
    @Override
    public boolean isHandle(KeyEvent event) {
        if (eventType.equals(KeyEvent.ANY)) {
            return true;
        }
        return eventType.equals(event.getEventType());
    }
}
