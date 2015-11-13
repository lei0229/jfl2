package org.jfl2.groovy;

import groovy.util.GroovyScriptEngine;
import mockit.Deencapsulation;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static java.nio.file.Paths.get;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.jfl2.groovy.GroovyScript.*;
import static org.junit.Assert.assertThat;

public class GroovyScriptTest {

    @Test
    public void testClearAllCache() throws Exception {
        Files.createDirectories(SOURCE_ROOT);
        Files.createDirectories(CACHE_ROOT);
        Files.createDirectories(MD5_ROOT);
        assertThat(Files.exists(SOURCE_ROOT), is(true));
        assertThat(Files.exists(CACHE_ROOT), is(true));
        assertThat(Files.exists(MD5_ROOT), is(true));

        clearAllCaches();
        assertThat(Files.exists(SOURCE_ROOT), is(true));
        assertThat(Files.exists(CACHE_ROOT), is(false));
        assertThat(Files.exists(MD5_ROOT), is(false));
    }

    @Test
    public void testConstructor() throws Exception {
        GroovyScript gl = new GroovyScript(get("main.groovy"));
        gl.run();
        GroovyScript gl2 = new GroovyScript(get("main2.groovy"));
        gl2.run();
    }

    @Test
    public void testConstructor2() throws Exception {
        GroovyScriptEngine engine = getDefaultEngine();
        GroovyScript gl = new GroovyScript(get("main.groovy"), engine);
        gl.run();
        GroovyScript gl2 = new GroovyScript(get("main2.groovy"), engine);
        gl2.run();
    }

    @Test
    public void testGetAllScripts() throws Exception {
        HashMap<String, GroovyScript> map = getAllScripts();
        assertThat(map.size(), is(2));
        assertThat(map.get("main"), notNullValue());
        assertThat(map.get("main2"), notNullValue());
    }

    /**
     * class ファイルがちゃんと消されるか確認
     *
     * @throws Exception
     */
    @Test
    public void testRemoveCache() throws Exception {
        GroovyScript gl = new GroovyScript(get("main.groovy"));
        Path mainClass = CACHE_ROOT.resolve("test/groovy/main.class");
        Path fooClass = CACHE_ROOT.resolve("test/groovy/Foo.class");
        assertThat(Files.exists(mainClass), is(true));
        assertThat(Files.exists(fooClass), is(true));

        GroovyScript.GroovyFile file = Deencapsulation.getField(gl, "file");
        file.removeCache();
        assertThat(Files.exists(mainClass), is(false));
        assertThat(Files.exists(fooClass), is(false));
    }
}
