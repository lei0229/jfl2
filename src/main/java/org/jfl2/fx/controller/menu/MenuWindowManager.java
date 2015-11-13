package org.jfl2.fx.controller.menu;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.jfl2.core.util.Jfl2NumberUtils;
import org.jfl2.fx.control.MenuPane;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MenuWindowManager {
    private Map<String, MenuWindow> id2MenuWindow = new HashMap<>();
    /**
     * 表示中のメニュー
     */
    private MenuWindow nowMenu;
    /**
     * 選択中のアイテムIndex
     */
    private int selected = -1;
    /**
     * Pane
     */
    private MenuPane menuPane;

    /**
     * Constructor
     *
     * @param pane
     */
    public MenuWindowManager(MenuPane pane) {
        menuPane = pane;
    }

    /**
     * 管理対象にMenuWindowを追加
     *
     * @param menu
     * @return
     */
    public MenuWindowManager add(MenuWindow menu) {
        id2MenuWindow.put(menu.id, menu);
        return this;
    }

    /**
     * id からMenuWindowを取得
     *
     * @param id Specify string
     * @return
     */
    public MenuWindow get(String id) {
        return id2MenuWindow.get(id);
    }

    /**
     * メニューを開く
     *
     * @param id
     * @return
     */
    public MenuWindowManager show(String id) {
        nowMenu = get(id);
        if (nowMenu != null) {
            menuPane.setTitleText(nowMenu.id);
            menuPane.setDescriptionText(nowMenu.description);

            menuPane.getListView().setVisible(false);
            menuPane.getListView().setManaged(false);

            /* listview は廃止
            menuPane.setItems(FXCollections.observableList(nowMenu.items));
            VirtualFlow flow = (VirtualFlow) menuPane.getListView().getChildrenUnmodifiable().get(0);
            double height = 0;
            for (int n = 0; n < nowMenu.items.size(); n++) {
                IndexedCell cell = flow.getCell(n);
                if (cell != null) {
                    height += cell.getHeight();
                }
            }
            height = Jfl2Const.getMaxValue(height, Jfl2Const.MENU_MAX_HEIGHT);
//            menuPane.getListView().setStyle("-fx-pref-height: " + height + ";");
            /**/

            List<RadioButton> rList = nowMenu.items.stream().map(menuItem -> {
                RadioButton btn = new RadioButton(menuItem.toString());
                btn.setFocusTraversable(false);
                btn.setToggleGroup(menuPane.getToggleGroup());
                btn.onMouseEnteredProperty().set((ev) -> select(menuItem));
                menuPane.getRadioBox().getChildren().add(btn);
                menuPane.getButtons().add(btn);
                return btn;
            }).collect(Collectors.toList());
            selectFirst();

            menuPane.setVisible(true);
            getFocus();
        }
        return this;
    }

    /**
     * 1つ上を選択
     */
    public MenuWindowManager up() {
        select(selected - 1, true);
        return this;
    }

    /**
     * 1つ下を選択
     */
    public MenuWindowManager down() {
        select(selected + 1, true);
        return this;
    }

    /**
     * 現在選択されているものを実行する
     *
     * @return
     */
    public MenuWindowManager enter() {
        return enter(selected);
    }

    /**
     * 指定したIndexのMenuを実行する
     *
     * @return
     */
    public MenuWindowManager enter(int index) {
        return enter(nowMenu.items.get(index));
    }

    /**
     * 指定したMenuItemを実行する
     *
     * @param item MenuItem is executed.
     * @return
     */
    public MenuWindowManager enter(MenuItem item) {
        hide();
        item.getConsumer().accept(null);
        return this;
    }

    /**
     * 指定したボタンを選択する
     *
     * @param index 0開始
     * @param loop  上下間ループするならtrue
     * @return
     */
    public MenuWindowManager select(int index, boolean loop) {
        if (menuPane.getButtons() != null) {
            selected = Jfl2NumberUtils.loopValue(index, menuPane.getButtons().size(), loop);
            menuPane.getButtons().get(selected).setSelected(true);
        }
        return this;
    }

    /**
     * 指定したボタンを選択する
     *
     * @param menuItem MenuItem
     * @return
     */
    public MenuWindowManager select(MenuItem menuItem){
        int index=0;
        for( MenuItem item : nowMenu.items ){
            if(Objects.equals(item, menuItem)){
                select(index, false);
            }
            index++;
        }
        return this;
    }

    /**
     * 最初のボタンを選択する
     */
    public MenuWindowManager selectFirst() {
        select(0, false);
        return this;
    }

    /**
     * ListViewにフォーカスを移す
     */
    public void getFocus() {
        Platform.runLater(() -> {
            Optional<RadioButton> selected = menuPane.getButtons().stream().filter(RadioButton::isSelected).findFirst();
            selected.ifPresent(RadioButton::requestFocus);
        });
    }

    /**
     * メニューを閉じる
     *
     * @return
     */
    public MenuWindowManager hide() {
        menuPane.setVisible(false);
        menuPane.setDescriptionText("");
//        menuPane.clearItems();
        menuPane.getMenuBox().getChildren().removeAll();
        menuPane.getButtons().stream().forEach(node->node.onMouseEnteredProperty().unbind());
        menuPane.getButtons().clear();
        menuPane.getRadioBox().getChildren().clear();
        nowMenu = null;
        return this;
    }

    /**
     * その他キーの処理
     *
     * @param event
     * @return
     */
    public MenuWindowManager quickSelect(Event event) {
        if (KeyEvent.class.isInstance(event)) {
            KeyEvent keyEvent = (KeyEvent) event;
            nowMenu.items.stream().filter(
                    (item) -> item.getKey().isHandle(keyEvent)
            ).findFirst().ifPresent(item -> enter(item));
        }
        return this;
    }

    /**
     * ホバー時のイベント
     * @param mouseEvent
     * @return
     */
    public MenuWindowManager hover(MouseEvent mouseEvent) {
        int index = 0;
        for( Node node : menuPane.getRadioBox().getChildren() ){
            if( node.contains(mouseEvent.getSceneX(), mouseEvent.getSceneY()) ){
                select(index, false);
            }
            if( node.contains(mouseEvent.getScreenX(), mouseEvent.getScreenY()) ){
                select(index, false);
            }
            index++;
        }
        return this;
    }

}
