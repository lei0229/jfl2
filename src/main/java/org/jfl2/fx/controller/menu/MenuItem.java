package org.jfl2.fx.controller.menu;

import groovy.lang.Closure;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jfl2.fx.controller.event.input.trigger.Jfl2Key;
import org.jfl2.fx.controller.event.input.trigger.Jfl2KeyChar;
import org.jfl2.fx.controller.event.input.trigger.Jfl2KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.jfl2.core.Jfl2Const.SYSTEM_NEWLINE;
import static org.jfl2.core.ResourceBundleManager.getString;

/**
 */
@AllArgsConstructor
public class MenuItem {
    @Getter
    private String label;
    @Getter
    private Jfl2Key key;
    @Getter
    private Consumer<Event> consumer;

    /**
     * 文字列表記
     * @return
     */
    public String toString(){
        return label;
    }

    /**
     * MenuItemを
     *
     * @param list
     */
    static public MenuItem newInstance(List<Object> list) {
        List<String> errorMessage = new ArrayList<>();
        Consumer<Event> consumer = null;

        if (list.size() > 0 && !String.class.isInstance(list.get(0))) {
            errorMessage.add(getString("The 1st argument of the MenuItem require String."));
        }
        if (list.size() >= 3) {
            if (Consumer.class.isInstance(list.get(list.size() - 1))) {
                consumer = (Consumer<Event>) list.get(list.size()-1);
            } else if (list.get(list.size() - 1) instanceof Closure) {
                final Closure cl = (Closure) list.get(list.size() - 1);
                consumer = ev-> cl.call(ev);
            } else {
                errorMessage.add(getString("The end argument of the MenuItem require Consumer or Closure."));
            }
        }

        if (list.size() >= 3 && list.size() <= 5) {
            Jfl2Key key = null;


            if (list.size() == 3) {
                if (KeyCode.class.isInstance(list.get(1))) {
                    key = new Jfl2KeyCode((KeyCode) list.get(1), KeyEvent.KEY_PRESSED, Jfl2Key.NONE);
                } else if (String.class.isInstance(list.get(1))) {
                    key = new Jfl2KeyChar((String) list.get(1), KeyEvent.KEY_PRESSED, Jfl2Key.NONE);
                } else {
                    errorMessage.add(getString("The 2nd argument of the MenuItem require KeyCode or String."));
                }
            } else if (list.size() == 4 || list.size() == 5) {
                int modifier = Jfl2Key.NONE;
                if (list.size() == 5) {
                    try {
                        modifier = Integer.parseInt(list.get(3).toString());
                    } catch (NumberFormatException e) {
                        errorMessage.add(getString("The 4th argument of the MenuItem require Jfl2Key."));
                    }
                }
                if (EventType.class.isInstance(list.get(1))) {
                    EventType eventType = (EventType) list.get(1);
                    if (KeyCode.class.isInstance(list.get(2))) {
                        key = new Jfl2KeyCode((KeyCode) list.get(2), eventType, modifier);
                    } else if (String.class.isInstance(list.get(2))) {
                        key = new Jfl2KeyChar((String) list.get(2), eventType, modifier);
                    } else {
                        errorMessage.add(getString("The 3rd argument of the MenuItem require KeyCode or String."));
                    }
                } else {
                    errorMessage.add(getString("The 2nd argument of the MenuItem require EventType."));
                }
            }
            if (errorMessage.size() <= 0) {
                return new MenuItem((String) list.get(0), key, consumer);
            }
        } else {
            errorMessage.add(getString("MenuItem require arguments is 3 to 5."));
        }

        errorMessage.add(0, getString("Could not create MenuItem."));
        if (errorMessage.size() > 0) {
            throw new IllegalArgumentException(String.join(SYSTEM_NEWLINE, errorMessage) + SYSTEM_NEWLINE + list.toString());
        }
        return null;
    }
}
