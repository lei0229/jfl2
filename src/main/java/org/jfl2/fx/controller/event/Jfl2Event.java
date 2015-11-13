package org.jfl2.fx.controller.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 */
public class Jfl2Event extends Event {
    public static final EventType<Jfl2Event> RELOAD_CSS = new EventType<Jfl2Event>(Event.ANY, "RELOAD_CSS");

    public Jfl2Event(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
