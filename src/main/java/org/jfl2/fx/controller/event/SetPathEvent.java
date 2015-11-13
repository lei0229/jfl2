package org.jfl2.fx.controller.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.jfl2.fx.control.FileListBox;

public class SetPathEvent extends Event {
    public SetPathEvent(FileListBox fileListBox) {
        super(new EventType<>("SetPathEvent"));
    }
}
