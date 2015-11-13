package org.jfl2.fx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jfl2.core.Jfl2Const;
import org.jfl2.core.ResourceBundleManager;
import org.jfl2.core.conf.ConfigBase;
import org.jfl2.fx.controller.menu.MenuItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Pane for menu
 */
@Slf4j
public class MenuPane extends FlowPane {
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    @Getter
    private VBox menuBox;
    @FXML
    @Getter
    private VBox radioBox;

    @Getter
    private ToggleGroup toggleGroup = new ToggleGroup();
    @Getter
    private List<RadioButton> buttons = new ArrayList<>();
    @FXML
    @Getter
    private ListView<MenuItem> listView;

    /**
     * Constructor
     */
    public MenuPane() throws IOException {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuPane.fxml"), ResourceBundleManager.getMessage());
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    /**
     * タイトル用ラベル取得
     *
     * @return
     */
    public Label getTitle() {
        return title;
    }

    /**
     * 説明用ラベル取得
     *
     * @return
     */
    public Label getDescription() {
        return description;
    }

    /**
     * Text is set to Description
     *
     * @param description
     */
    public void setDescriptionText(String description) {
        this.description.setText(description);
    }

    /**
     * Text is set to Title
     *
     * @param description
     */
    public void setTitleText(String description) {
        this.title.setText(description);
    }

    /**
     * List is set to ListView
     *
     * @param list
     */
    public void setItems(ObservableList<MenuItem> list) {
        listView.setItems(list);
    }

    /**
     * Clear list
     */
    public void clearItems() {
        listView.setItems(FXCollections.emptyObservableList());
    }

    /**
     * Clear data
     */
    public void clear(){

    }

    /**
     * Reload css file
     */
    public MenuPane reloadCss() {
        log.debug("Call realoadCss()");

        if (this.getStylesheets().size() > 0) {
            this.getStylesheets().clear();
        }
        Path css = ConfigBase.getConfigFilePath(Paths.get(Jfl2Const.CSS_MENU));
        if (Files.exists(css)) {
            this.getStylesheets().add(css.toAbsolutePath().toUri().toString());
        }

        return this;
    }
}
