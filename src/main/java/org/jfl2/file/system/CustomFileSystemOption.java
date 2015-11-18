package org.jfl2.file.system;

import org.jfl2.core.ResourceBundleManager;

import java.util.Collections;
import java.util.Map;

/**
 * Scheme文字列とEnvironmentを保持
 */
public class CustomFileSystemOption {
    final public Map<String, ?> env;
    final public String scheme;

    /**
     * constructor
     * @param scheme Add to scheme string to head.
     * @param env This value is passed to FileSystems.newFileSystem()
     */
    public CustomFileSystemOption(String scheme, Map<String, ?> env) {
        if(scheme == null){
            throw new IllegalArgumentException(ResourceBundleManager.getString("Scheme value is not supported null."));
        }
        if( env == null){
            env = Collections.EMPTY_MAP;
        }

        this.env = Collections.unmodifiableMap(env);
        this.scheme = scheme;
    }
}
