package org.jfl2.core.conf;

import junit.framework.TestCase;
import org.jfl2.core.util.Jfl2FileUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ConfigBaseTest extends TestCase {

    private ConfigBase xmlObj = new ConfigBase() {
        @Override
        public OutputType getOutputType() {
            return OutputType.XML;
        }

        @Override
        public Path getFilePath() {
            return Paths.get("test/.jfl2/groovy/main.groovy");
        }

        @Override
        public Charset getFileCharset() {
            return Charset.forName("UTF-8");
        }
    };

    public void testGetConfigFilePath() throws Exception {
        String filename = "groovy/main.groovy";
        assertThat(xmlObj.getConfigFilePath(Paths.get(filename)), is(xmlObj.CONFIG_PATH.resolve(filename)));
    }

    public void testCopyResourceFiles() throws Exception {
        Jfl2FileUtils.removeDirectory(ConfigBase.CONFIG_PATH);
        assertThat(Files.exists(ConfigBase.CONFIG_PATH), is(false));
        ConfigBase.copyResourceFiles();

        assertThat(Files.exists(ConfigBase.CONFIG_PATH), is(true));
        Path path = ConfigBase.getConfigFilePath(Paths.get("groovy/main.groovy"));
        assertThat(path, notNullValue());
        assertThat(Files.exists(path), is(true));
    }

}
