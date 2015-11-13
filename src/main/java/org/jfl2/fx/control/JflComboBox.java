package org.jfl2.fx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

// 使ってないはず 2015-09-04
public class JflComboBox extends ComboBox<Path> {
    public JflComboBox() {
        super();
        FileSystem fs = FileSystems.getDefault();
        try {
            List<Path> files = Files.list(fs.getPath("c:\\")).map(o -> o).collect(Collectors.toList());
            ObservableList<Path> numbers = FXCollections.observableArrayList(files);
            this.setItems(numbers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getSelectionModel().selectFirst();

    }
}
