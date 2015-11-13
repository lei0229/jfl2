package org.jfl2.fx.controller.adapter;

import org.jfl2.core.conf.Jfl2History;
import org.jfl2.fx.control.FileListBox;

public interface TopPaneAdapter<T extends TopPaneAdapter> extends Jfl2Adapter<T> {
    FileListBox getLeft();

    FileListBox getRight();

    Jfl2History getJfl2History();

}
