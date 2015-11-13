package org.jfl2.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.jfl2.core.Jfl2Options;
import org.jfl2.core.ResourceBundleManager;
import org.jfl2.fx.controller.Jfl2Controller;
import org.kohsuke.args4j.CmdLineException;

import java.util.Optional;


@Slf4j
public class Main extends Application {
//    private Jfl2Configurations conf = Jfl2Configurations.load();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Jfl2Options options = new Jfl2Options();
        Optional<CmdLineException> argumentParseException = parseArgument(options);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

        Jfl2Controller.beforeInitialize(options);  // 設定ファイルのコピーとか

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPane.fxml"), ResourceBundleManager.getMessage());
            Parent root = loader.load();
            Jfl2Controller ctrl = loader.getController();
            ctrl.setStage(stage);       // And Load mani.groovy
            ctrl.setOptions(options);

            Scene scene = new Scene(root);
//            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * 終了時
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * 引数をパースしてOptionsに格納します
     *
     * @param options
     * @return
     */
    private Optional<CmdLineException> parseArgument(Jfl2Options options) {
        Optional<CmdLineException> parseException = Optional.empty();
        try {
            options.parse(this.getParameters().getRaw());
        } catch (CmdLineException e) {
            Platform.runLater(() -> {
                log.error("Arguments are incorrect. {}", options.getUsage());
            });
        }
        return parseException;
    }

}
