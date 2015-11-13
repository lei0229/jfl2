package org.jfl2.fx.controller.event.input.trigger;

import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

import java.util.Objects;

/**
 * マウス関連イベントトリガ
 */
public class Jfl2Mouse extends Jfl2ModifierKey implements EventTrigger<MouseEvent> {

    final public static int NONE_BUTTON = 0x00;
    final public static int PRIMARY_BUTTON = 0x01;
    final public static int SECONDARY_BUTTON = 0x02;
    final public static int MIDDLE_BUTTON = 0x04;
    final public static int PRIMARY_SECONDARY_BUTTON = 0x08; // push 2buttons Right -> Left
    final public static int SECONDARY_PRIMARY_BUTTON = 0x10; // push 2buttons Left  -> Right

    @Getter
    final protected EventType<MouseEvent> eventType;

    /**
     * 複数押し対応ボタン
     */
    private Jfl2MouseButton mouseButton = Jfl2MouseButton.NONE;

    /**
     * Constructor
     *
     * @param eventType
     */
    public Jfl2Mouse(EventType<MouseEvent> eventType) {
        this(Jfl2ModifierKey.NONE, Jfl2MouseButton.NONE, eventType);
    }

    /**
     * Constructor
     *
     * @param modifier
     * @param mouseButton
     * @param eventType
     */
    public Jfl2Mouse(int modifier, Jfl2MouseButton mouseButton, EventType<MouseEvent> eventType) {
        super(modifier);
        this.mouseButton = mouseButton;
        this.eventType = eventType;
    }

    @Override
    public boolean isHandle(MouseEvent event) {
        return Objects.equals(event.getEventType(), eventType) && Jfl2MouseButton.sameButton(mouseButton, event) && match(event);
    }

}
