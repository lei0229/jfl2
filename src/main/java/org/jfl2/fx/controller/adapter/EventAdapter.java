package org.jfl2.fx.controller.adapter;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jfl2.fx.controller.event.input.*;
import org.jfl2.fx.controller.event.input.trash.Jfl2KeyCharEventAction;
import org.jfl2.fx.controller.event.input.trash.Jfl2KeyCodeEventAction;

import java.util.function.Consumer;
import static org.jfl2.fx.controller.event.input.EventTargetComponentType.*;

/**
 * jfl2Controllerの操作分離クラス基本
 */
public interface EventAdapter<T extends EventAdapter> extends Jfl2Adapter<T> {
    EventTargetComponentManager getEventTargetComponentManager();

    /**
     * イベント登録とNodeのマッピング作成
     *
     * @return
     */
    default EventTargetComponentManager getDefaultTargetComponentManager() {
        EventTargetComponentManager manager = new EventTargetComponentManager();
        manager.addNode(getJfl2().getLeft(), FILELIST, LEFT_FILELIST);
        manager.addNode(getJfl2().getLeft().getTableView(), FILELIST_TABLEVIEW, LEFT_FILELIST_TABLEVIEW);
        manager.addNode(getJfl2().getLeft().getComboBox(), FILELIST_COMBOBOX, LEFT_FILELIST_COMBOBOX);

        manager.addNode(getJfl2().getRight(), FILELIST, RIGHT_FILELIST);
        manager.addNode(getJfl2().getRight().getTableView(), FILELIST_TABLEVIEW, RIGHT_FILELIST_TABLEVIEW);
        manager.addNode(getJfl2().getRight().getComboBox(), FILELIST_COMBOBOX, RIGHT_FILELIST_COMBOBOX);

        manager.addNode(getJfl2().getMenuPane(), MENU);
        manager.addNode(getJfl2().getMenuPane().getRadioBox(), MENU_ITEMS);
        return manager;
    }

    /**
     * イベント追加
     * Groovyから以下のように使う
     * <pre>
     * jfl2.addEvent(FILELIST_COMBOBOX, { eReg ->
     *     eReg.add( KeyCode.ENTER, { jfl2.current.setPath(jfl2.current.getComboBoxValue()) });
     * });
     *</pre>
     *
     * @param type    KeyCode
     * @param operate Execution content
     * @return
     */
    default T addEvent(EventTargetComponentType type, Consumer<EventRegister> operate) {
        operate.accept(EventRegister.getInstance(getEventTargetComponentManager(), type));
        return getInstance();
    }

    /**
     * イベント追加
     *  EventRegister経由で登録するようになったので使っていない
     *
     * @return
     */
    default T addEvent(KeyCode code, EventType<KeyEvent> keyEvent, int modifier, EventTargetComponentType target, boolean consume, Consumer<Event> operate) {
        Jfl2KeyCodeEventAction ev = new Jfl2KeyCodeEventAction(code, keyEvent, modifier, target, getEventTargetComponentManager(), consume, operate);
        getEventTargetComponentManager().addEvent(ev);
        return getInstance();
    }

    /**
     * イベント追加
     *  EventRegister経由で登録するようになったので使っていない
     *
     * @return
     */
    default T addEvent(String character, EventType<KeyEvent> keyEvent, int modifier, EventTargetComponentType target, boolean consume, Consumer<Event> operate) {
        Jfl2KeyCharEventAction ev = new Jfl2KeyCharEventAction(character, keyEvent, modifier, target, getEventTargetComponentManager(), consume, operate);
        getEventTargetComponentManager().addEvent(ev);
        return getInstance();
    }

}
