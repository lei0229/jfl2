package org.jfl2.fx.controller.menu;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MenuWindow {
    /**
     * Identifier
     */
    final String id;
    /**
     * Description
     */
    final String description;
    final List<MenuItem> items = new ArrayList<>();

    public MenuWindow(String id, String description, List<Object>... options) {
        this.id = id;
        this.description = description;
        for (List<Object> item : options) {
            items.add(MenuItem.newInstance(item));
        }
    }
}
