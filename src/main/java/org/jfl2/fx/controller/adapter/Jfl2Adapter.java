package org.jfl2.fx.controller.adapter;

import javafx.stage.Stage;
import org.jfl2.core.conf.Jfl2History;
import org.jfl2.fx.controller.Jfl2Controller;
import org.slf4j.Logger;

/**
 * jfl2Controllerの操作分離クラス基本
 */
public interface Jfl2Adapter<T extends Jfl2Adapter> {
    /**
     * return this
     *
     * @return
     */
    T getInstance();

    /**
     * Fetch logger
     *
     * @return
     */
    Logger getLog();

    /**
     * Fetch stage
     *
     * @return
     */
    Jfl2Controller getJfl2();

    /**
     * Fetch stage
     *
     * @return
     */
    Stage getStage();

    /**
     * Fetch history object
     *
     * @return
     */
    Jfl2History getJfl2History();

    /**
     * Windowサイズを前回終了時に戻す
     *
     * @return
     */
    default T restoreWindowSize() {
        getJfl2History().restoreStageSize(getStage());  // サイズ復帰
        return getInstance();
    }

    /**
     * Windowサイズを前回終了時に戻す
     *
     * @return
     */
    default T restoreWindowPosition() {
        getJfl2History().restoreStagePosition(getStage());  // 位置復帰
        return getInstance();
    }

    /**
     * すべての CSSファイルを読み込みなおします
     *
     * @return
     */
    default T reloadAllCss() {
        // 強制上書きは止めた
        /*
        if (getJfl2().getOptions() != null && getJfl2().getOptions().isForceInitialization()) {
            try {
                ConfigBase.copyResourceFiles();             // 強制上書き
            } catch (IOException | URISyntaxException e) {
                getLog().error("Coping configuration files is failed.", e);
            }
        }
        */
        getJfl2().getRight().reloadCss();
        getJfl2().getLeft().reloadCss();
        getJfl2().getMenuPane().reloadCss();
        getJfl2().reloadCss();
        return getInstance();
    }


    /**
     * 終了
     *
     * @return
     */
    default T quit() {
//        Platform.runLater(() -> getStage().close());
//        Platform.exit();
        getStage().getOnCloseRequest().handle(null); // バグなのかclose requestイベントが呼ばれないので自力で呼ぶ
        getStage().close();
        return getInstance();
    }

    /**
     * コピペ用
     *
     * @return
     */
    default T dummy() {
        return getInstance();
    }

}
