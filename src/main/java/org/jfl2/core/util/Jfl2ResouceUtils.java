package org.jfl2.core.util;

import javafx.scene.Parent;
import org.jfl2.core.Jfl2Const;
import org.jfl2.core.conf.ConfigBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * リソース関連ユーティリティ
 */
public class Jfl2ResouceUtils {
    private static Logger log = LoggerFactory.getLogger(Jfl2ResouceUtils.class);

    /**
     * Reload css file
     */
    static public void reloadCss(Parent parent, String cssFile) {
        log.debug("Call realoadCss({}, {})", parent.getClass(), cssFile);
        if (parent.getStylesheets().size() > 0) {
            parent.getStylesheets().clear();
        }
        Path css = ConfigBase.getConfigFilePath(Paths.get(cssFile));
        if (Files.exists(css)) {
            parent.getStylesheets().add(css.toAbsolutePath().toUri().toString());
        }
    }

    /**
     * リソースファイルが存在しない場合に作成する
     *
     * @param baseClass    リソース検索のためのベースクラス
     * @param resourceName リソース名
     * @throws IOException
     * @throws URISyntaxException
     */
    static public void createIfNotExists(Class<?> baseClass, String resourceName) throws IOException, URISyntaxException {
        URL url = baseClass.getResource(resourceName);
        if (url == null) {
            Path path = Paths.get(url.toURI());
            if (Files.isWritable(path)) {
                Files.createFile(path);
            }
            throw new IOException(String.format("%s is not able to write.", url.toString()));
        }
    }

    /**
     * リソースファイルをローカルにコピーする
     *
     * @param resourceName name of resource
     * @param dstPath      output file
     * @param callClass    for search resources
     * @throws IOException
     * @throws URISyntaxException
     */
    static public Path copyToLocal(String resourceName, Path dstPath, Class<?> callClass) throws IOException, URISyntaxException {
        Jfl2FileUtils.createDirectoriesIfNotExists(dstPath.getParent());

        Logger log = LoggerFactory.getLogger(callClass);

        URL url = callClass.getResource(resourceName);

        if (url == null) {
            log.warn("Resource file is not found. {}", resourceName);
        } else {
            Files.deleteIfExists(dstPath);
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(dstPath))) {
                Files.copy(new File(url.toURI()).toPath(), out);
            }
            return dstPath;
        }
        return null;
    }

    /**
     * 指定のリソースのPathを返す
     *
     * @param baseClass    リソース検索のためのベースクラス
     * @param resourceName リソース名
     * @return Path
     * @throws IOException
     */
    static public Path getPath(Class<?> baseClass, String resourceName) throws FileNotFoundException, URISyntaxException {
        URL url = baseClass.getResource(resourceName);
        if (url == null) {
            throw new FileNotFoundException(String.format("Resource file is not found. %s", resourceName));
        }
        return Paths.get(url.toURI());
    }

    /**
     * 指定のリソースのOutputStreamを返す
     * 書き込めない場合はIOExceptionが発生する
     *
     * @param baseClass    リソース検索のためのベースクラス
     * @param resourceName リソース名
     * @return IOStream
     * @throws IOException
     */
    static public OutputStream outputStream(Class<?> baseClass, String resourceName) throws IOException, URISyntaxException {
        Path path = getPath(baseClass, resourceName);
        if (Files.isWritable(path)) {
            return Files.newOutputStream(path, StandardOpenOption.CREATE);
        }
        throw new IOException(String.format("%s is not able to write.", path.toString()));
    }

    /**
     * 指定のリソースのInputStreamを返す
     *
     * @param baseClass    リソース検索のためのベースクラス
     * @param resourceName リソース名
     * @return IOStream
     * @throws IOException
     */
    static public InputStream inputStream(Class<?> baseClass, String resourceName) throws IOException {
        URL url = baseClass.getResource(resourceName);
        if (url == null) {
            throw new FileNotFoundException(String.format("Resource file is not found. %s", resourceName));
        }
        return url.openStream();
    }

    /**
     * 指定のリソースのReaderを返す
     *
     * @param baseClass    リソース検索のためのベースクラス
     * @param resourceName リソース名
     * @param charset      文字セット
     * @return Reader
     * @throws IOException
     */
    static public Reader reader(Class<?> baseClass, String resourceName, Charset charset) throws IOException {
        return new InputStreamReader(inputStream(baseClass, resourceName), charset);
    }

}
