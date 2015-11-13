package org.jfl2.fx.controller.event.input.trigger;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ToString
abstract public class Jfl2ModifierKey {
    final public static int NONE = 0x00;
    final public static int ALT = 0x01;
    final public static int CTRL = 0x02;
    final public static int META = 0x04;
    final public static int SHORTCUT = 0x08;
    final public static int SHIFT = 0x10;

    @Getter
    protected boolean altDown;
    @Getter
    protected boolean controlDown;
    @Getter
    protected boolean metaDown;
    @Getter
    protected boolean shiftDown;

    /**
     * Constructor
     *
     * @param modifier
     */
    public Jfl2ModifierKey(int modifier) {
        altDown = (modifier & ALT) != 0;
        controlDown = (modifier & CTRL) != 0;
        metaDown = (modifier & META) != 0;
        shiftDown = (modifier & SHIFT) != 0;

        // shortcut key
        if ((modifier & SHORTCUT) != 0) {
            switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
                case SHIFT:
                    shiftDown = true;
                    break;
                case CONTROL:
                    controlDown = true;
                    break;
                case ALT:
                    altDown = true;
                    break;
                case META:
                    metaDown = true;
                    break;
            }
        }
    }

    /**
     * 環境ごとのShortcutキーを取得
     *
     * @return
     */
    protected int getShortcutModifier() {
        switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
            case SHIFT:
                return SHIFT;
            case CONTROL:
                return CTRL;
            case ALT:
                return ALT;
            case META:
                return META;
            default:
                return NONE;
        }
    }

    /**
     * このKeyEventで処理する場合はtrueを返す
     *
     * @param keyEvent
     * @return
     */
    public boolean match(KeyEvent keyEvent) {
        return (
                keyEvent.isAltDown() == altDown &&
                        keyEvent.isControlDown() == controlDown &&
                        keyEvent.isShiftDown() == shiftDown &&
                        keyEvent.isMetaDown() == metaDown
        );
    }

    /**
     * このMouseEventで処理する場合はtrueを返す
     *
     * @param mouseEvent
     * @return
     */
    public boolean match(MouseEvent mouseEvent) {
        return (
                mouseEvent.isAltDown() == altDown &&
                        mouseEvent.isControlDown() == controlDown &&
                        mouseEvent.isShiftDown() == shiftDown &&
                        mouseEvent.isMetaDown() == metaDown
        );
    }

}
