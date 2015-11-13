package org.jfl2.core.util;

import junit.framework.TestCase;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Jfl2ResouceUtilsTest extends TestCase {
    private String resouceFile4InputTest = "testInputStream.txt";
    private String expect4InputTest = "testInputStream用リソースファイル\n";
    private Charset charset4InputTest = Charset.forName("UTF-8");

    private String resouceFile4OutputTest = "testOutputStream.txt";
    private String expect4OutputTest = "testOutputStream用リソースファイル\n";
    private Charset charset4OutputTest = Charset.forName("UTF-8");

    /**
     * input streamの取得
     *
     * @throws Exception
     */
    public void testInputStream() throws Exception {
        try (InputStream in = Jfl2ResouceUtils.inputStream(this.getClass(), resouceFile4InputTest)) {
            byte[] buff = new byte[expect4InputTest.getBytes(charset4InputTest).length];
            in.read(buff);
            assertThat(buff, is(expect4InputTest.getBytes(charset4InputTest)));
        }
    }

    /**
     * readerの取得
     *
     * @throws Exception
     */
    public void testReader() throws Exception {
        try (Reader in = Jfl2ResouceUtils.reader(this.getClass(), resouceFile4InputTest, charset4InputTest)) {
            char[] buff = new char[expect4InputTest.length()];
            in.read(buff);
            assertThat(String.valueOf(buff), is(expect4InputTest));
        }
    }

    /**
     * outputStreamの取得
     *
     * @throws Exception
     */
    public void testOutputStream() throws Exception {
        Path file = Paths.get(this.getClass().getResource(resouceFile4OutputTest).toURI());

        try (OutputStream out = Jfl2ResouceUtils.outputStream(this.getClass(), resouceFile4OutputTest)) {
            out.write(expect4OutputTest.getBytes(charset4OutputTest));
        }
        assertThat(Files.exists(file), is(true));
        // 内容確認
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buff = new byte[expect4OutputTest.getBytes(charset4OutputTest).length];
            in.read(buff);
            assertThat(buff, is(expect4OutputTest.getBytes(charset4OutputTest)));
        }
    }

}
