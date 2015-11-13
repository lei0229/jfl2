package org.jfl2.fx.controller.event.input.trigger;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * 2つ同時押し対応
 */
public enum Jfl2MouseButton {
    NONE,
    PRIMARY_CLICK,
    SECONDARY_CLICK,
    MIDDLE_CLICK,
    PRIMARY_DOUBLE_CLICK,
    SECONDARY_DOUBLE_CLICK,
    MIDDLE_DOUBLE_CLICK,
    PRIMARY_SECONDARY_CLICK,
    SECONDARY_PRIMARY_CLICK;


    /**
     * eventで押されたボタンがbuttonと合致すればtrueを返す
     *
     * @param button
     * @param event
     * @return
     */
    static public boolean sameButton(Jfl2MouseButton button, MouseEvent event) {
        switch (button) {
            case PRIMARY_CLICK:
                return event.getButton().equals(MouseButton.PRIMARY);
            case SECONDARY_CLICK:
                return event.getButton().equals(MouseButton.SECONDARY);
            case MIDDLE_CLICK:
                return event.getButton().equals(MouseButton.MIDDLE);
            case PRIMARY_DOUBLE_CLICK:
                return event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2;
            case SECONDARY_DOUBLE_CLICK:
                return event.getButton().equals(MouseButton.SECONDARY) && event.getClickCount() == 2;
            case MIDDLE_DOUBLE_CLICK:
                return event.getButton().equals(MouseButton.MIDDLE) && event.getClickCount() == 2;
            case PRIMARY_SECONDARY_CLICK:
                return (event.isPrimaryButtonDown() && event.isSecondaryButtonDown() && event.getButton().equals(MouseButton.SECONDARY));
            case SECONDARY_PRIMARY_CLICK:
                return (event.isPrimaryButtonDown() && event.isSecondaryButtonDown() && event.getButton().equals(MouseButton.PRIMARY));
            case NONE:
                return event.getButton().equals((MouseButton.NONE));
        }
        return false;
    }
}
