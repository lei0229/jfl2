package org.jfl2.core.conf.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "FileListBox")
public class FileListBoxHistory {
    @JsonProperty("focus")
    private boolean focus = false;
    @JsonProperty("path_history")
    private List<String> pathHistory = new ArrayList<>();

    /**
     * 左右タブのどっちにフォーカスがあったか
     *
     * @param
     */
    @XmlElement(name = "focus")
    public boolean isFocus() {
        return focus;
    }

    /**
     * 左右タブのどっちにフォーカスがあったか
     *
     * @param focus
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * @return
     * @Getter と@XMLElementの相性が悪い
     */
    @XmlElement(name = "path_history")
    public List<String> getPathHistory() {
        return pathHistory;
    }

    /**
     * @return
     * @Setter と@XMLElementの相性が悪い
     */
    public void setPathHistory(List<String> list) {
        this.pathHistory = list;
    }
}
