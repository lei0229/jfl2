package org.jfl2.core;

import java.util.ResourceBundle;

public class ResourceBundleManager {
    /**
     * Bundle for message
     */
    static final private ResourceBundle messageBundle = ResourceBundle.getBundle("i18n.message");

    /**
     * Fetch message bundle
     *
     * @return
     */
    static public ResourceBundle getMessage() {
        return messageBundle;
    }

    /**
     * Fetch resource value
     *
     * @param key
     * @return
     */
    static public String getString(String key) {
        return messageBundle.getString(key);
    }
}
