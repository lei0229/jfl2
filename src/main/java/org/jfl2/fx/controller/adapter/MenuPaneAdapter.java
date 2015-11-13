package org.jfl2.fx.controller.adapter;

import javafx.event.EventType;
import org.jfl2.fx.controller.menu.MenuWindow;
import org.jfl2.fx.controller.menu.MenuWindowManager;

import java.util.List;

public interface MenuPaneAdapter<T extends MenuPaneAdapter> extends Jfl2Adapter<T> {

    /**
     * メニュー管理用クラスの取得
     * @return
     */
    MenuWindowManager getMenuWindowManager();

    /**
     * メニューを開く
     *
     * @return
     */
    default T showMenu(String id) {
        getMenuWindowManager().show(id);
        return getInstance();
    }

    /**
     * メニューを開く
     *
     * @return
     */
    default T hideMenu() {
        getLog().debug("call hideMenu()");
        getMenuWindowManager().hide();
        return getInstance();
    }

    /**
     * メニューを追加登録する
     *
     * @return
     */
    default T addMenu(String id, String description, List<Object>... options) {
        getMenuWindowManager().add(new MenuWindow(id, description, options));
        return getInstance();
    }

    /**
     * メニュー操作オブジェクト取得
     * @return
     */
    default MenuWindowManager getMenu(){
        return getMenuWindowManager();
    }

    /**
     * その他キーの処理
     * @param eventType
     * @return
     */
    default T directSelect(EventType eventType){

        return getInstance();
    }

}
