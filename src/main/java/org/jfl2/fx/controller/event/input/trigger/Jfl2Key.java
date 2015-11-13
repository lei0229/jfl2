package org.jfl2.fx.controller.event.input.trigger;

import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Slf4j
@ToString
abstract public class Jfl2Key extends Jfl2ModifierKey implements EventTrigger<KeyEvent>{
    @Getter
    final protected EventType<KeyEvent> eventType;

    /**
     * Constructor
     *
     * @param eventType
     */
    public Jfl2Key(EventType<KeyEvent> eventType) {
        this(eventType, Jfl2Key.NONE);
    }

    /**
     * Constructor
     *
     * @param eventType
     * @param modifier
     */
    public Jfl2Key(EventType<KeyEvent> eventType, int modifier) {
        super(modifier);
        this.eventType = eventType;
    }

    /**
     * このInputEventで処理する場合はtrueを返す
     *
     * @param keyEvent
     * @return
     */
    @Override
    public boolean isHandle(KeyEvent keyEvent) {
        return (
                super.match(keyEvent) &&
                Objects.equals(keyEvent.getEventType(), eventType)
        );
    }

}
