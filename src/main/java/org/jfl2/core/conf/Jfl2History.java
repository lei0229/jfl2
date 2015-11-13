package org.jfl2.core.conf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.jfl2.core.Jfl2Const;
import org.jfl2.core.conf.elements.FileListBoxHistory;
import org.jfl2.core.conf.elements.MainFrameHistory;
import org.jfl2.file.Jfl2Path;
import org.jfl2.fx.control.FileListBox;
import org.jfl2.fx.controller.Jfl2Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.jfl2.file.Jfl2Path.toStringList;

/**
 * 設定保存用
 */
@XmlRootElement(name = "jfl2_history")
public class Jfl2History extends ConfigBase {
    public static final Path JFL2_HISTORY_FILE = Paths.get(Jfl2Const.JFL2_HISTORY_JSON_FILE);

    private static Logger log = LoggerFactory.getLogger(Jfl2History.class);

    @JsonProperty("main_frame")
    @XmlElement(name = "main_frame")
    @Getter
    private MainFrameHistory mainFrameHistory;
    @JsonProperty("filelist_left")
    @XmlElement(name = "filelist_left")
    @Getter
    private FileListBoxHistory leftHistory;
    @JsonProperty("filelist_right")
    @XmlElement(name = "filelist_right")
    @Getter
    private FileListBoxHistory rightHistory;

    /**
     * リソースファイルを読み込みます
     */
    static public Jfl2History load() {
        Jfl2History history = new Jfl2History();
        if (!history.exists()) {
            return getDefault();
        }
        try {
            return ((Jfl2History) history.loadFile()).removeNullProperty();
        } catch (DataBindingException | IOException | InstantiationException | IllegalAccessException e) {
            log.error(String.format("History file %s is broken.", history.getFilePath()), e);
        }
        return getDefault();
    }

    /**
     * デフォルト取得
     *
     * @return
     */
    public static Jfl2History getDefault() {
        Jfl2History result = new Jfl2History();
        return result.removeNullProperty();
    }

    /**
     * 書き出し
     */
    @Override
    public void write() {
        super.write();
    }

    /**
     * NULLのプロパティがあれば初期値をいれる
     *
     * @return
     */
    public Jfl2History removeNullProperty() {
        if (mainFrameHistory == null) mainFrameHistory = new MainFrameHistory();
        if (leftHistory == null) leftHistory = new FileListBoxHistory();
        if (rightHistory == null) rightHistory = new FileListBoxHistory();
        return this;
    }

    @Override
    @JsonIgnore
    public OutputType getOutputType() {
        return DEFAULT_OUTPUT_TYPE;
    }

    /**
     * ファイル名取得
     *
     * @return Path
     */
    @JsonIgnore
    public Path getFilePath() {
        return JFL2_HISTORY_FILE;
    }

    @Override
    @JsonIgnore
    public Charset getFileCharset() {
        return DEFAULT_CHARSET;
    }

    private void updatePosition() {

    }

    /**
     * 履歴用リスナー登録
     *
     * @param jfl2 jfl2controller
     * @return this
     */
    public Jfl2History addListener(Jfl2Controller jfl2) {
        Stage stage = jfl2.getStage();

        // 終了イベント登録
        stage.setOnCloseRequest((WindowEvent t) -> {
            log.debug("write history : ", this);

            // Exceptionを投げると終了処理が止まってしまうので外には投げない
            try {
                // Windows size & position
                mainFrameHistory.displayX = stage.xProperty().doubleValue();
                mainFrameHistory.displayY = stage.yProperty().doubleValue();
                mainFrameHistory.width = stage.widthProperty().doubleValue();
                mainFrameHistory.height = stage.heightProperty().doubleValue();

                // Path history
                leftHistory.setPathHistory(toStringList(jfl2.getLeft().getPathHistoryList()));
                leftHistory.setFocus(jfl2.isFocusLeft());
                rightHistory.setPathHistory(toStringList(jfl2.getRight().getPathHistoryList()));
                rightHistory.setFocus(jfl2.isFocusRight());

                this.write();
            } catch (Exception e) {
                log.error("Quit function is failed.", e);
            }
        });

        // 移動毎に保存するのをやめた
 /*
        stage.xProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("X prop change to {} from {} ({})", newValue, oldValue, observable);
            this.mainFrameHistory.displayX = newValue.doubleValue();
        });
        stage.yProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Y prop change to {} from {} ({})", newValue, oldValue, observable);
            this.mainFrameHistory.displayY = newValue.doubleValue();
        });
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Width prop change to {} from {} ({})", newValue, oldValue, observable);
            this.mainFrameHistory.width = newValue.doubleValue();
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Height prop change to {} from {} ({})", newValue, oldValue, observable);
            this.mainFrameHistory.height = newValue.doubleValue();
        });
*/

        // path変更 ※終了時にComboBoxの内容を保存するよう修正
        /*
        jfl2.getLeft().addEventHandler(org.jfl2.fx.control.FileListBox.SET_PATH, (event) -> {
            leftHistory.getPathHistory().add(0, jfl2.getLeft().getPath().toString());
        });
        jfl2.getRight().addEventHandler(org.jfl2.fx.control.FileListBox.SET_PATH, (event) -> {
            rightHistory.getPathHistory().add(0, jfl2.getLeft().getPath().toString());
        });
        */

        return this;
    }

    /**
     * ウィンドウサイズをhistoryから取得してhistoryの値に戻す
     *
     * @param stage
     * @return
     */
    public Jfl2History restoreStageSize(Stage stage) {
        if (mainFrameHistory != null) {
            if (mainFrameHistory.width != null) stage.setWidth(mainFrameHistory.width);
            if (mainFrameHistory.height != null) stage.setHeight(mainFrameHistory.height);
        }
        return this;
    }

    /**
     * ウィンドウ位置をhistoryから取得してhistoryの値に戻す
     *
     * @param stage
     * @return
     */
    public Jfl2History restoreStagePosition(Stage stage) {
        if (mainFrameHistory != null) {
            if (mainFrameHistory.displayX != null) stage.setX(mainFrameHistory.displayX);
            if (mainFrameHistory.displayY != null) stage.setY(mainFrameHistory.displayY);
        }
        return this;
    }

    /**
     * Pathの履歴を復元します
     *
     * @param jfl2
     * @return
     */
    public Jfl2History restorePathHistory(Jfl2Controller jfl2) throws IOException {
        restorePathHistory(jfl2, leftHistory, jfl2.getLeft());
        restorePathHistory(jfl2, rightHistory, jfl2.getRight());
        return this;
    }

    /**
     * restorePathHistory の内部処理用
     * @param jfl2
     * @param history
     * @param list
     * @throws IOException
     */
    private void restorePathHistory(Jfl2Controller jfl2, FileListBoxHistory history, FileListBox list) throws IOException {
        List<Jfl2Path> files = history.getPathHistory().stream().map(str -> new Jfl2Path(str)).collect(Collectors.toList());
        if (files.size() > 0) {
            list.setPathHistory(files);
            list.setPath(files.get(0));
        }
        if (history.isFocus()) {
            jfl2.focus(list);
        }

    }
}
