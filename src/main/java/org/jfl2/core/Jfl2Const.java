package org.jfl2.core;

public interface Jfl2Const {
    String GROOVY_MAIN = "main";
    String CSS_FILELISTBOX = "css/FileListBox.css";
    String CSS_MENU = "css/Menu.css";
    String CSS_TOP = "css/Top.css";

    String SYSTEM_NEWLINE = System.getProperty("line.separator");
    double MENU_MAX_HEIGHT = 800;
    String DIR_DISPLAY = "<dir>";
    String DATETIME_FORMAT_RFC_3339 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String JFL2_HISTORY_JSON_FILE = "jfl2_history.xml";
    String VIRTUAL_DIR_DELIMITER = " > ";

    /**
     * 最大値セット用
     * @param src
     * @param maxValue
     * @return
     */
    static double getMaxValue(double src, double maxValue){
        if(src > maxValue){
            return maxValue;
        }
        return src;
    }
}
