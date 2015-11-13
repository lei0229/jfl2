package org.jfl2.fx.controller.event.input;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.jfl2.fx.controller.event.input.trigger.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * groovyから少ない表記でイベント登録するためのクラス
 */
public class EventRegister {
    static public Map<EventTargetComponentType, EventRegister> type2Instance = new HashMap<>();
    final private EventTargetComponentManager manager;
    final private EventTargetComponentType type;

    /**
     * constructor
     *
     * @param manager
     * @param type
     */
    private EventRegister(EventTargetComponentManager manager, EventTargetComponentType type) {
        this.manager = manager;
        this.type = type;
    }

    /**
     * キャッシュされたEventRegisterを取得する
     *
     * @param manager
     * @param type
     * @return
     */
    static public EventRegister getInstance(EventTargetComponentManager manager, EventTargetComponentType type) {
        EventRegister result = type2Instance.get(type);
        if (result != null) {
            return result;
        }
        EventRegister newObj = new EventRegister(manager, type);
        type2Instance.put(type, newObj);
        return newObj;
    }

    /**
     * Add Event
     *
     * @param code
     * @param operate
     * @return
     */
    public EventRegister add(KeyCode code, Consumer<Event> operate) {
        return add(Jfl2Key.NONE, code, KeyEvent.KEY_PRESSED, true, operate);
    }

    /**
     * Add Event
     *
     * @param modifier
     * @param code
     * @param operate
     * @return
     */
    public EventRegister add(int modifier, KeyCode code, Consumer<Event> operate) {
        return add(modifier, code, KeyEvent.KEY_PRESSED, true, operate);
    }

    /**
     * Add Event
     *
     * @param modifier
     * @param code
     * @param consume
     * @param operate
     * @return
     */
    public EventRegister add(int modifier, KeyCode code, boolean consume, Consumer<Event> operate) {
        return add(modifier, code, KeyEvent.KEY_PRESSED, consume, operate);
    }

    /**
     * Add Event
     *
     * @param character
     * @param operate
     * @return
     */
    public EventRegister add(String character, Consumer<Event> operate) {
        return add(Jfl2Key.NONE, character, KeyEvent.KEY_PRESSED, true, operate);
    }

    /**
     * Add Event
     *
     * @param modifier
     * @param character
     * @param operate
     * @return
     */
    public EventRegister add(int modifier, String character, Consumer<Event> operate) {
        return add(modifier, character, KeyEvent.KEY_PRESSED, true, operate);
    }

    /**
     * Add Event
     *
     * @param modifier
     * @param character
     * @param consume
     * @param operate
     * @return
     */
    public EventRegister add(int modifier, String character, boolean consume, Consumer<Event> operate) {
        return add(modifier, character, KeyEvent.KEY_PRESSED, consume, operate);
    }

    /**
     * Add Event
     *
     * @param modifier Modifier key
     * @param code     KeyCode
     * @param keyEvent KEY_PRESSED / KEY_RELEASED / KEY_PUSHED / ..
     * @param consume  If true then finish event chain
     * @param operate  Execution content
     * @return
     */
    public EventRegister add(int modifier, KeyCode code, EventType<KeyEvent> keyEvent, boolean consume, Consumer<Event> operate) {
        EventAction ev = new EventAction(new Jfl2KeyCode(code, keyEvent, modifier), type, consume, operate);
        manager.addEvent(ev);
        return this;
    }

    /**
     * Add Event
     *
     * @param modifier  Modifier key
     * @param character Key character
     * @param keyEvent  KEY_PRESSED / KEY_RELEASED / KEY_PUSHED / ..
     * @param consume   If true then finish event chain
     * @param operate   Execution content
     * @return
     */
    public EventRegister add(int modifier, String character, EventType<KeyEvent> keyEvent, boolean consume, Consumer<Event> operate) {
        EventAction ev = new EventAction(new Jfl2KeyChar(character, keyEvent, modifier), type, consume, operate);
        manager.addEvent(ev);
        return this;
    }

    /**
     * Add Mouse Event
     *
     * @param mouseButton Jfl2MouseButton.PRIMARY / MIDDLE / SECONDARY
     * @param operate   Execution content
     * @return
     */
    public EventRegister add(Jfl2MouseButton mouseButton, Consumer<Event> operate) {
        EventAction ev = new EventAction(new Jfl2Mouse(Jfl2ModifierKey.NONE, mouseButton, MouseEvent.MOUSE_CLICKED), type, true, operate);
        manager.addEvent(ev);
        return this;
    }

    /**
     * Add Mouse Event
     *
     * @param operate   Execution content
     * @return
     */
    public EventRegister hover(Consumer<Event> operate) {
        EventAction ev = new EventAction(new Jfl2Mouse(Jfl2ModifierKey.NONE, Jfl2MouseButton.NONE, MouseEvent.MOUSE_MOVED), type, true, operate);
        manager.addEvent(ev);
        return this;
    }

    /**
     * Add Mouse Event
     *
     * @param modifier  Modifier key
     * @param mouseButton Jfl2MouseButton.PRIMARY / MIDDLE / SECONDARY
     * @param operate   Execution content
     * @return
     */
    public EventRegister add(int modifier, Jfl2MouseButton mouseButton, Consumer<Event> operate) {
        EventAction ev = new EventAction(new Jfl2Mouse(modifier, mouseButton, MouseEvent.MOUSE_CLICKED), type, true, operate);
        manager.addEvent(ev);
        return this;
    }

    /**
     * Add Mouse Event
     *
     * @param modifier  Modifier key
     * @param mouseButton Jfl2MouseButton.PRIMARY / MIDDLE / SECONDARY
     * @param mouseEvent  KEY_PRESSED / KEY_RELEASED / KEY_PUSHED / ..
     * @param consume   If true then finish event chain
     * @param operate   Execution content
     * @return
     */
    public EventRegister add(int modifier, Jfl2MouseButton mouseButton, EventType<MouseEvent> mouseEvent, boolean consume, Consumer<Event> operate) {
        EventAction ev = new EventAction(new Jfl2Mouse(modifier, mouseButton, mouseEvent), type, consume, operate);
        manager.addEvent(ev);
        return this;
    }

    /**
     * Add any-key Event
     * @param operate   Execution content
     * @return
     */
    public EventRegister addAnyKeyEvent(Consumer<Event> operate) {
        EventAction ev = new EventAction(Jfl2AnyKey.KEY_ANY, type, true, operate);
        manager.addEvent(ev);
        return this;
    }

}
