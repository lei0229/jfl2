package org.jfl2.fx.controller.adapter;

import javafx.scene.control.TableView;
import org.jfl2.core.conf.Jfl2History;
import org.jfl2.fx.control.FileListBox;

import java.io.IOException;
import java.util.Objects;

public interface FileListAdapter<T extends FileListAdapter> extends Jfl2Adapter<T> {
    /**
     * 左タブ
     *
     * @return
     */
    FileListBox getLeft();

    /**
     * 右タブ
     *
     * @return
     */
    FileListBox getRight();

    /**
     * フォーカス中タブ
     *
     * @return
     */
    FileListBox getCurrent();

    /**
     * Historyオブジェクト取得
     *
     * @return
     */
    Jfl2History getJfl2History();

    default T setPath() {
        return getInstance();
    }

    /**
     * Pathのコンボボックスに残る履歴最大数を設定する
     *
     * @return
     */
    default T setMaxPathHistory(int num) {
        getLeft().setPathHistoryNumber(num);
        return getInstance();
    }

    /**
     * Path履歴をHistoryから読み込みます
     *
     * @return
     */
    default T restorePathHistory() throws IOException {
        getJfl2History().restorePathHistory(getJfl2());
        return getInstance();
    }

    /**
     * カーソル位置を一番上にする
     *
     * @return
     */
    default T cursorToTop() {
        getCurrent().cursorToTop();
        return getInstance();
    }

    /**
     * カーソル位置取得
     *
     * @return
     */
    default int getCursorPosition() {
        return getCurrent().getTableView().getFocusModel().getFocusedIndex();
    }

    /**
     * ファイルをマークして、カーソルを１つさげる
     *
     * @return
     */
    default T mark() {
        return mark(false, true);
    }

    /**
     * ファイルをマークします
     *
     * @param forced 反転しないで強制的にマークする場合はtrue
     * @param down   カーソル下げるならtrue
     * @return
     */
    default T mark(boolean forced, boolean down) {
        final int pos = getCursorPosition();
        TableView.TableViewSelectionModel selectionModel = getCurrent().getTableView().getSelectionModel();
        if (forced) {
            selectionModel.select(pos);
        } else {
            if (selectionModel.isSelected(pos)) {
                selectionModel.clearSelection(pos);
            } else {
                selectionModel.select(pos);
            }
        }
        if (down) {
            down();
        }
        return getInstance();
    }

    /**
     * カーソル位置を一番下にする
     *
     * @return
     */
    default T cursorToBottom() {
        getCurrent().cursorToBottom();
        return getInstance();
    }

    /**
     * 1ページ前
     *
     * @return
     */
    default T prevPage() {
        getCurrent().prevPage();
        return getInstance();
    }

    /**
     * 1ページ後
     *
     * @return
     */
    default T nextPage() {
        getCurrent().nextPage();
        return getInstance();
    }

    /**
     * カーソル一番下に
     *
     * @return
     */
    default T bottom() {
        getCurrent().bottom();
        return getInstance();
    }

    /**
     * カーソル一番上に
     *
     * @return
     */
    default T top() {
        getCurrent().top();
        return getInstance();
    }

    /**
     * カーソル1上に
     *
     * @return
     */
    default T up() {
        getCurrent().up(1);
        return getInstance();
    }

    /**
     * カーソル1下に
     *
     * @return
     */
    default T down() {
        getCurrent().down(1);
        return getInstance();
    }

    /**
     * フォーカスが左タブならtrue
     *
     * @return
     */
    default boolean isFocusLeft() {
        return Objects.equals(getCurrent(), getLeft());
    }

    /**
     * フォーカスが右タブならtrue
     *
     * @return
     */
    default boolean isFocusRight() {
        return Objects.equals(getCurrent(), getRight());
    }

    /**
     * 左タブにフォーカスを移す
     *
     * @return
     */
    default T focusLeft() {
        return focus(getJfl2().getLeft());
    }

    /**
     * 左タブにフォーカスを移す
     *
     * @return
     */
    default T focusRight() {
        return focus(getJfl2().getRight());
    }

    /**
     * フォーカス設定
     *
     * @param obj
     * @return
     */
    T focus(FileListBox obj);

    /**
     * 左右タブのフォーカスを入れ替える
     *
     * @return
     */
    default T focusSwap() {
        if (getJfl2().isFocusLeft()) {
            getJfl2().focusLeft();
        } else {
            getJfl2().focusRight();
        }
        return getInstance();
    }
}
