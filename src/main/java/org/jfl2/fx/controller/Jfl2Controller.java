package org.jfl2.fx.controller;

import groovy.lang.Binding;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jfl2.core.Jfl2Const;
import org.jfl2.core.Jfl2Options;
import org.jfl2.core.ResourceBundleManager;
import org.jfl2.core.conf.ConfigBase;
import org.jfl2.core.conf.Jfl2History;
import org.jfl2.core.util.Jfl2ResouceUtils;
import org.jfl2.fx.control.FileListBox;
import org.jfl2.fx.control.MenuPane;
import org.jfl2.fx.controller.adapter.EventAdapter;
import org.jfl2.fx.controller.adapter.FileListAdapter;
import org.jfl2.fx.controller.adapter.MenuPaneAdapter;
import org.jfl2.fx.controller.adapter.TopPaneAdapter;
import org.jfl2.fx.controller.event.input.EventTargetComponentManager;
import org.jfl2.fx.controller.menu.MenuWindowManager;
import org.jfl2.groovy.GroovyScript;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;


@Slf4j
public class Jfl2Controller implements Initializable, FileListAdapter<Jfl2Controller>, TopPaneAdapter<Jfl2Controller>, EventAdapter<Jfl2Controller>, MenuPaneAdapter<Jfl2Controller> {
    @FXML
    private SplitPane normalPane;

    @FXML
    private Pane overwrapPane;

    @FXML
    private FileListBox leftFileListBox;

    @FXML
    private FileListBox rightFileListBox;

    @Getter
    private FileListBox currentFileListBox;

    @FXML
    private StackPane topPane;

    @FXML
    private MenuPane menuPane;

    @Getter
    @Setter
    private Jfl2Options options;

    /**
     * Stage
     */
    @Getter
    private Stage stage;

    /**
     * History
     */
    @Getter
    private Jfl2History jfl2History = Jfl2History.load();
    /**
     * Groovy files
     */
    private HashMap<String, GroovyScript> scripts;

    /**
     * コンポーネントとイベントのマッピング
     */
    @Getter
    private EventTargetComponentManager eventTargetComponentManager;

    /**
     * メニュー管理
     */
    @Getter
    private MenuWindowManager menuWindowManager;

    /**
     * Implement前に実行したい
     *
     * @param options
     */
    static public void beforeInitialize(Jfl2Options options) {
        try {
            if (options.isForceInitialization()) {
                ConfigBase.copyResourceFiles();             // 強制上書き
            } else {
                ConfigBase.copyResourceFilesIfNotExists();  // 設定ファイルが無ければコピー
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Coping configuration files is failed.", e);
        }

    }

    /**
     * 指定したFileListBoxにフォーカスを移す
     *
     * @param target 左右どちらかのFileListBox
     * @return
     */
    public Jfl2Controller focus(FileListBox target) {
        return focus(target, true);
    }

    /**
     * 指定したFileListBoxにフォーカスを移す
     *
     * @param target         左右どちらかのFileListBox
     * @param focusTableView TableViewにフォーカスを移すならtrue
     * @return
     */
    public Jfl2Controller focus(FileListBox target, boolean focusTableView) {
        log.debug("call focus() " + target);
        currentFileListBox = target;
        if (target != null && focusTableView) {
            target.focusList();
        }
        return this;
    }

    /**
     * FXMLLoader.load() 時にImplementされて呼び出されます
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        ResourceBundle rc = ResourceBundleManager.getMessage();
        log.debug(rc.getString("jfl2"));
        log.debug("call Jfl2Controller::initialize()");

        focusLeft();

        // フォーカス設定
        leftFileListBox.getComboBox().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) focus(leftFileListBox, false);
        });
        leftFileListBox.getTableView().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) focusLeft();
        });
        rightFileListBox.getComboBox().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) focus(rightFileListBox, false);
        });
        rightFileListBox.getTableView().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) focusRight();
        });

        // イベントマネージャ初期化
        eventTargetComponentManager = getDefaultTargetComponentManager();
        // メニューウィンドマネージャ初期化
        menuWindowManager = new MenuWindowManager(menuPane);
        menuPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) getJfl2().focus(getJfl2().getCurrent()); // メニュー非表示時はフォーカスをFileListBoxに戻す
        });

        // フォーカスを左のタブへ
        Platform.runLater(() -> {
            getLeft().getTableView().requestFocus();
        });

        // CSS is set
        reloadAllCss();
    }

    /**
     * Called by Main.java
     *
     * @param _stage Stage
     */
    public void setStage(Stage _stage) {
        this.stage = _stage;
        initStage();
    }

    /**
     * Stage関連初期化処理
     *
     * @return this
     */
    private Jfl2Controller initStage() {

        /*
        getStage().addEventFilter(KeyEvent.ANY, (keyEvent) -> {
            // Press/Release/Type, keycode or keystring + modifire + 対象コンポーネント + 処理内容
            getLogger().debug("Key pressed : {}", keyEvent);
        });
        */

        jfl2History.addListener(this); // Listener登録 stage情報必須

        // Load groovy
        try {
            log.debug("Groovy scripts loading start.");
            scripts = GroovyScript.getAllScripts();

            // main.groovy があれば実行する
            if (scripts.get(Jfl2Const.GROOVY_MAIN) != null) {
                log.debug("Run main.groovy.");
                Binding bind = new Binding();
                bind.setVariable("jfl2", this);
                scripts.get(Jfl2Const.GROOVY_MAIN).run(bind);
            }
        } catch (IOException e) {
            log.error("Groovy scripts loading is failed.", e);
        }

        // set path
        Arrays.asList(leftFileListBox, rightFileListBox).stream().forEach(obj -> {
                    if(obj.getPath() == null ) {
                        try {
                            obj.setPath(FileSystems.getDefault().getRootDirectories().iterator().next().toString());
                        } catch (IOException e) {
                            log.error("Could not fetch root directory.", e);
                        }
                    }
                }
        );


        return this;
    }

    /**
     * Reload css file
     */
    public void reloadCss() {
        Jfl2ResouceUtils.reloadCss(topPane, Jfl2Const.CSS_TOP);
    }

    /**
     * メニュー追加
     *
     * @return
     */
    public Jfl2Controller addMenu() {
        return this;
    }


    /**
     * Fetch logger
     *
     * @return
     */
    public Logger getLog() {
        return log;
    }

    /**
     * Fetch this
     *
     * @return
     */
    @Override
    public Jfl2Controller getJfl2() {
        return this;
    }

    /**
     * Fetch instance
     *
     * @return
     */
    @Override
    public Jfl2Controller getInstance() {
        return this;
    }

    /**
     * Fetch left pane
     *
     * @return
     */
    @Override
    public FileListBox getLeft() {
        return leftFileListBox;
    }

    /**
     * Fetch right pane
     *
     * @return
     */
    @Override
    public FileListBox getRight() {
        return rightFileListBox;
    }

    /**
     * Focused pane
     *
     * @return
     */
    public FileListBox getCurrent() {
        return currentFileListBox;
    }

    /**
     * Unfocused pane
     *
     * @return
     */
    public FileListBox getAnother() {
        if(isFocusLeft()){
            return getRight();
        }
        return getLeft();
    }

    /**
     * Fetch menu pane
     *
     * @return
     */
    public MenuPane getMenuPane() {
        return menuPane;
    }
}
