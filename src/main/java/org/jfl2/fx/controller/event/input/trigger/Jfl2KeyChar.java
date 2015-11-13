package org.jfl2.fx.controller.event.input.trigger;

import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * キー文字による指定用
 */
@Slf4j
public class Jfl2KeyChar extends Jfl2Key {
    protected final String character;

    public Jfl2KeyChar(String character, EventType<KeyEvent> keyEvent, int modifier ) {
        super(keyEvent, modifier);
        this.character = character;
    }

    /**
     * このInputEventで処理する場合はtrueを返す
     *
     * @param event
     * @return
     */
    @Override
    public boolean isHandle(KeyEvent event) {
        return character.equals(event.getCharacter()) && super.isHandle(event);
    }
}
