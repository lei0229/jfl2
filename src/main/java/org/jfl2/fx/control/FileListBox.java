package org.jfl2.fx.control;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.IntRange;
import org.jfl2.core.Jfl2Const;
import org.jfl2.core.conf.ConfigBase;
import org.jfl2.file.Jfl2Path;
import org.jfl2.file.system.CustomFileSystemOption;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.jfl2.core.ResourceBundleManager.getMessage;
import static org.jfl2.fx.control.FileListBox.CURSOR_MOVE_TYPE.*;

@Slf4j
public class FileListBox extends VBox {
    public static final EventType<Event> SET_PATH = new EventType<Event>(Event.ANY, "SET_PATH");
    final private IntRange PATH_HISTORY_NUMBER_RANGE = new IntRange(1, 100);
    final private int DEFAULT_PATH_HISTORY_NUMBER = 20;
    @FXML
    @Getter
    private ComboBox comboBox;
    @FXML
    @Getter
    private TableView tableView;

    private FileSystem vFileSystem;

    /**
     * Extension to Scheme
     */
    private Map<String,CustomFileSystemOption> ext2Scheme;

    /**
     * コンボボックスの履歴最大数
     */
    private int pathHistoryNumber = DEFAULT_PATH_HISTORY_NUMBER;

    /**
     * 表示中パス
     */
    private Jfl2Path currentPath;

    @Getter
    private ObservableList<Jfl2Path> records = FXCollections.observableArrayList();

    private VirtualFlow flow;
    /**
     * ComboBox選択用イベント
     */
    private ChangeListener comboSelectListener = (observableValue, oldValue, newValue) -> {
//        if (!inSetPath && newValue != null && !Objects.equals(oldValue, newValue)) {
        if (newValue != null && !Objects.equals(oldValue, newValue)) {
            Platform.runLater(() -> {
                try {
                    this.setPath(newValue.toString());
                } catch (IOException | URISyntaxException e) {
                    log.error("Change dir is failure to " + this.getComboBoxValue(), e);
                }
            });
        }
    };
    /**
     * virtualFlow 取得用
     */
    private ChangeListener<Skin> forVirtualFlowListener = new ChangeListener<Skin>() {
        @Override
        public void changed(ObservableValue<? extends Skin> observable, Skin oldValue, Skin newValue) {
            if (newValue == null) {
                return;
            }
            flow = null;
            if (TableViewSkin.class.isInstance(newValue)) {
                ObservableList<Node> kids = ((TableViewSkin) newValue).getChildren();
                if (kids == null || kids.isEmpty()) {
                    return;
                }
                if (VirtualFlow.class.isInstance(kids.get(1))) {
                    flow = (VirtualFlow) kids.get(1);
                }

            }
        }
    };

    /**
     * Constructor
     *
     * @throws IOException
     */
    public FileListBox() throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileListBox.fxml"), getMessage());
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        comboBox.setOnAction(e -> {
            log.debug("Action : " + e);
        });
        comboBox.getSelectionModel().selectedItemProperty().removeListener(comboSelectListener); // 履歴選択

        currentPath = null;

        tableView.getFocusModel().focus(0);
        tableView.getFocusModel().getFocusedIndex();


        /* RowFactory */
        // 見た目変更用にRowFactoryのカスタムが居るかと思ったが、cssの指定だけでいけそうなので放置
        Callback<TableView<Jfl2Path>, TableRow<Jfl2Path>> orgCallback = new SimpleObjectProperty<Callback<TableView<Jfl2Path>, TableRow<Jfl2Path>>>(this, "rowFactory").get();

        final List<String> removeCssList = Jfl2Path.getRemoveCssList();

        tableView.setRowFactory(new Callback<TableView<Jfl2Path>, TableRow<Jfl2Path>>() {
            @Override
            public TableRow call(TableView param) {
                final TableRow<Jfl2Path> row = new TableRow<Jfl2Path>() {
                    @Override
                    protected void updateItem(Jfl2Path item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().removeAll(removeCssList);
                        if (item != null) {
                            try {
                                item.setCss(getStyleClass());
                            } catch (IOException e) {
                                log.error("Exception occurred", e);
                            }
                        }
//                        getStyleClass().removeAll("focus_row");
//                        if (item != null && Objects.equals(param.getFocusModel().getFocusedItem(), item)) {
//                            getStyleClass().add("focus_row");
//                        }
//                        getStyleClass().add("file");
                    }

                    /*
                    @Override
                    public void updateIndex(int i) {
                        super.updateIndex(i);
                        getStyleClass().removeAll("even", "odd");
                    }
                    /**/
                };
//                row.focusedProperty();
//                row.focusedProperty().addListener((listener) -> {
//                    getStyleClass().add("focus_row");
//                });
//                param.scrollToColumnIndex(0);
                return row;
            }
        });
        // Fetch VirtualFlow
        addSkinListener();

    }

    /**
     * VirtualFlowを取得するためのリスナ登録
     */
    private void addSkinListener() {
        tableView.skinProperty().addListener(forVirtualFlowListener);
    }

    /**
     * VirtualFlowを取得するためのリスナ登録
     */
    private void removeSkinListener() {
        tableView.skinProperty().removeListener(forVirtualFlowListener);
    }

    /**
     * コンボボックスの一覧返却
     *
     * @return
     */
    public List<Jfl2Path> getPathHistoryList() {
        return comboBox.getItems();
    }

    /**
     * setter
     *
     * @param pathHistoryNumber
     */
    public void setPathHistoryNumber(int pathHistoryNumber) {
        this.pathHistoryNumber = pathHistoryNumber;
        if (pathHistoryNumber < PATH_HISTORY_NUMBER_RANGE.getMinimumInteger()) {
            this.pathHistoryNumber = PATH_HISTORY_NUMBER_RANGE.getMinimumInteger();
        }
        if (pathHistoryNumber > PATH_HISTORY_NUMBER_RANGE.getMaximumInteger()) {
            this.pathHistoryNumber = PATH_HISTORY_NUMBER_RANGE.getMaximumInteger();
        }
    }

    /**
     * getter
     *
     * @return
     */
    public Jfl2Path getPath() {
        return currentPath;
    }

    /**
     * up dir
     *
     * @return
     * @throws IOException
     */
    public FileListBox upDir() throws IOException, URISyntaxException {
        Jfl2Path path = currentPath.getParent();
        if( path != null ) {
            String fName = currentPath.getName();
            if( currentPath.isVirtual() ) {
                fName = currentPath.getVirtualSourcePath().getName();
            }
            setPath(path, "^" + Pattern.quote(fName) + "$");
        }
        return this;
    }

    /**
     * カーソル下のpathに移動
     *
     * @return
     * @throws IOException
     */
    public FileListBox setPath() throws IOException, URISyntaxException {
        return setPath(getCursorPath());
    }

    /**
     * setter
     *
     * @param path
     * @return
     * @throws IOException
     */
    public FileListBox setPath(String path) throws IOException, URISyntaxException {
        return setPath(new Jfl2Path(path));
    }

    /**
     * setter
     *
     * @param path
     * @param regExp
     * @return
     * @throws IOException
     */
    public FileListBox setPath(Jfl2Path path, String regExp) throws IOException, URISyntaxException {
        return setPath(path, false, regExp);
    }

    /**
     * setter
     *
     * @param path
     * @return
     * @throws IOException
     */
    public FileListBox setPath(Jfl2Path path) throws IOException, URISyntaxException {
        return setPath(path, false, null);
    }

    /**
     * setter
     *
     * @param index
     * @return
     * @throws IOException
     */
    public FileListBox setPath(int index) throws IOException, URISyntaxException {
        return setPath(index, false);
    }

    /**
     * setter
     *
     * @param index
     * @param moveFocus ファイル一覧にフォーカスを移すならtrue
     * @return
     * @throws IOException
     */
    public FileListBox setPath(int index, boolean moveFocus) throws IOException, URISyntaxException {
        ObservableList<Jfl2Path> list = comboBox.getItems();
        setPath(list.get(index));
        return this;
    }

    /**
     * setter
     *
     * @param path
     * @param moveFocus ファイル一覧にフォーカスを移すならtrue
     * @return
     * @throws IOException
     */
    public FileListBox setPath(String path, boolean moveFocus) throws IOException, URISyntaxException {
        return setPath(new Jfl2Path(path), moveFocus, null);
    }

    /**
     * setter
     *
     * @param path         移動先Jfl2Path
     * @param moveFocus    setPathしたPaneにフォーカスを移動する
     * @param regExp 文字列検索してカーソルを移動させる
     * @return
     * @throws IOException
     */
    public FileListBox setPath(Jfl2Path path, boolean moveFocus, String regExp ) throws IOException, URISyntaxException {
        log.debug("call setPath({}, {})", path, moveFocus);

        // init
        tableView.setItems(FXCollections.observableArrayList());
        if(vFileSystem != null){
            vFileSystem.close();
        }

        if( path.isFile() ) {
            CustomFileSystemOption opt = ext2Scheme.get(path.getExtension());
            if (opt == null) {
                return this;
            }
            URI orgURI = path.getPath().toUri();
            URI newURI = new URI(opt.scheme + orgURI.getScheme(), orgURI.getSchemeSpecificPart(), orgURI.getFragment());
            vFileSystem = FileSystems.newFileSystem(newURI, opt.env);

            for( Path root : vFileSystem.getRootDirectories() ){
                currentPath = new Jfl2Path(path, root);
                break;
            }
        }else{
            currentPath = path;
        }

        // List Box
        List<Jfl2Path> files = Files.list(currentPath.getPath()).map(o -> new Jfl2Path(o)).collect(Collectors.toList());
        records = FXCollections.observableArrayList(files);
        tableView.setItems(records);

        if( regExp == null ) {
            setCursor(0, false, AUTO);
        }else{
            setCursor(regExp);
        }

        // Combo Box
        addComboBox(path);
        comboBox.getSelectionModel().selectFirst();

        // Fire event
        this.fireEvent(new Event(SET_PATH));

        //comboBox.getSelectionModel().selectedItemProperty().addListener(comboSelectListener);
        if (moveFocus) {
            focusList();
        }

        log.debug("finish setPath({})", path);
        return this;
    }

    /**
     * りれきに一覧を設定する。選択アイテムは一番上のものになる。
     *
     * @param history
     * @return
     */
    public FileListBox setPathHistory(List<Jfl2Path> history) throws IOException, URISyntaxException {
        comboBox.setItems(FXCollections.observableArrayList(history));
        comboBox.getSelectionModel().selectFirst();
        if (history.size() > 0) {
            setPath(0);
        }
        return this;
    }

    public String getComboBoxValue() {
        return comboBox.getEditor().getText();
    }

    /**
     * Reload css file
     */
    public FileListBox reloadCss() {
        log.debug("Call realoadCss()");

        if (tableView.getStylesheets().size() > 0) {
            tableView.getStylesheets().clear();
        }
        Path css = ConfigBase.getConfigFilePath(Paths.get(Jfl2Const.CSS_FILELISTBOX));
        if (Files.exists(css)) {
            tableView.getStylesheets().add(css.toAbsolutePath().toUri().toString());
        }

        return this;
    }

    /**
     * 履歴対応のコンボボックスパス表示処理
     *
     * @param path
     */
    private void addComboBox(Jfl2Path path) {
        // 追加分の重複削除
        if (comboBox.getItems().contains(path)) {
            comboBox.getItems().removeAll(path);
        }

        comboBox.getItems().add(0, path);   // 頭に追加

        // 0個 → 1個 時に1個目を選択する処理
        if (comboBox.getSelectionModel().getSelectedIndex() < 0) {
            comboBox.getSelectionModel().selectFirst();
        }

        // 最大個数オーバーしたら消す
        if (comboBox.getItems().size() > pathHistoryNumber) {
            comboBox.getItems().remove(pathHistoryNumber, comboBox.getItems().size());
        }
    }

    /**
     * カーソル位置取得
     *
     * @return
     */
    public int getCursorIndex() {
        return tableView.getFocusModel().getFocusedIndex();
    }

    /**
     * カーソル位置取得
     *
     * @return
     */
    public Jfl2Path getCursorPath() {
        return getRecords().get(getCursorIndex());
    }


    /**
     * 表示行数取得
     *
     * @return
     */
    private int getViewCount() {
        int count = tableView.getItems().size();
        count = flow.getChildrenUnmodifiable().size();
        log.debug("count == {}", count);
        return count;
//        if (flow != null) {
//            return flow.getLastVisibleCell().getIndex() - flow.getFirstVisibleCell().getIndex();
//        }
//        return 0;
    }

    //----------------------------------------
    // カーソル関連

    /**
     * カーソル位置を一番上表示にする
     *
     * @return
     */
    public FileListBox cursorToTop() {
        setCursor(getCursorIndex(), false, TOP);
        return this;
    }

    /**
     * カーソル位置を一番下表示にする
     *
     * @return
     */
    public FileListBox cursorToBottom() {
        setCursor(getCursorIndex(), false, BOTTOM);
        return this;
    }

    /**
     * カーソル移動 1P下へ
     *
     * @return this
     */
    public FileListBox nextPage() {
        return setCursor(getCursorIndex() + getViewCount(), false, TOP);
    }

    /**
     * カーソル移動 1P上へ
     *
     * @return this
     */
    public FileListBox prevPage() {
        return setCursor(getCursorIndex() - getViewCount(), false, TOP);
    }

    /**
     * カーソル移動 最上段
     *
     * @return this
     */
    public FileListBox top() {
        setCursor(0, false, AUTO);
        return this;
    }

    /**
     * カーソル移動 最下段
     *
     * @return this
     */
    public FileListBox bottom() {
        setCursor(Integer.MAX_VALUE, false, AUTO);
        return this;
    }

    /**
     * カーソル移動 ループあり
     *
     * @param n 移動量
     * @return this
     */
    public FileListBox up(int n) {
        int now = getCursorIndex();
        setCursor(now - n, true, AUTO);
        return this;
    }

    /**
     * カーソル移動 ループあり
     *
     * @param n 移動量
     * @return this
     */
    public FileListBox down(int n) {
        int now = getCursorIndex();
        setCursor(now + n, true, AUTO);
        return this;
    }

    /**
     * ファイル名一致カーソル移動
     *
     * @param regExp 検査用文字列
     * @return this
     */
    public FileListBox setCursor(String regExp){
        final Pattern pat = Pattern.compile(regExp);
        for(int n=0; n<=records.size(); n++){
            if( records.get(n).matchName(pat) ){
                return setCursor(n, false, AUTO);
            }
        }
        return this;
    }

    /**
     * カーソル移動
     *
     * @param index 位置。0開始
     * @param loop  true なら上下でカーソルがループする
     * @param type  画面表示タイプ
     * @return this
     */
    public FileListBox setCursor(int index, boolean loop, CURSOR_MOVE_TYPE type) {
        int size = tableView.getItems().size();
        int newValue = index;
        if (loop) {
            if (newValue < 0) {
                newValue = size - 1;
            } else if (index >= size) {
                newValue = 0;
            }
        } else {
            if (index < 0) {
                newValue = 0;
            } else if (index >= size) {
                newValue = size - 1;
            }
        }
        tableView.getFocusModel().focus(newValue);
        if (flow != null) {
            log.debug("New cursor number is {}. (type:{})", newValue, type);
            final int finalNewValue = newValue;
            Platform.runLater(() -> {
                switch (type) {
                    case TOP:
                        tableView.scrollTo(finalNewValue);
                        break;
                    case BOTTOM:
                        flow.showAsLast(flow.getCell(finalNewValue));
                        break;
                    case AUTO:
                        if (size - 1 == finalNewValue) {
                            tableView.scrollTo(finalNewValue); // 上から最下段へのループが上手く動かないのでごまかす
                        } else {
                            flow.show(finalNewValue);
                        }
                        break;
                }
            });
        }

        return this;
    }

    /**
     * フォーカスをTableViewに移す
     * @return this
     */
    public FileListBox focusList() {
        Platform.runLater(() -> {
            tableView.requestFocus();
        });
        return this;
    }

    /**
     * setter
     * @param ext2Scheme
     */
    public void setExt2Scheme(Map<String, CustomFileSystemOption> ext2Scheme) {
        this.ext2Scheme = ext2Scheme;
    }

    /**
     * カーソル移動後の画面表示タイプ
     */
    public enum CURSOR_MOVE_TYPE {
        AUTO,       // 自動
        TOP,        // カーソル位置をトップに
        BOTTOM;     // カーソル位置を最下段に
    }
}
