package org.jfl2.fx.controller.event.input.trigger;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 */
public interface EventTrigger<T extends Event> {

    /**
     * このEventTriggerで処理する場合はtrueを返す
     *
     * @param event
     * @return
     */
    boolean isHandle(T event);

    /**
     * このEventTriggerで処理するEventTypeを返す
     * @return
     */
    EventType<T> getEventType();
}
