package org.jfl2.core.conf;

import org.apache.commons.lang.StringUtils;
import org.jfl2.core.ResourceBundleManager;
import org.jfl2.core.util.Jfl2FileUtils;
import org.jfl2.core.util.Jfl2ResouceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * 設定保存用
 */
public abstract class ConfigBase extends SerializeBase {
    static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    static final OutputType DEFAULT_OUTPUT_TYPE = OutputType.XML;
    static final String CONFIG_DIR_NAME = ".jfl2";
    static final String USER_HOME = System.getProperty("user.home");

    static final String ENV_TEST = "jfl2test";  // 環境変数 jfl2test が設定されていれば、TEST_CONFIG_DIRがベースディレクトリになります
    static final Path TEST_CONFIG_DIR = Paths.get("build").resolve("home");
    static final Path CONFIG_PATH;
    private static Logger log = LoggerFactory.getLogger(ConfigBase.class);

    static {
        if (StringUtils.isNotEmpty(System.getenv(ENV_TEST))) {
            CONFIG_PATH = TEST_CONFIG_DIR.resolve(CONFIG_DIR_NAME);
            try {
                Files.createDirectories(CONFIG_PATH);
            } catch (IOException e) {
                log.error("Does not create directory for test. " + CONFIG_PATH, e);
            }
        } else if (USER_HOME != null) {
            CONFIG_PATH = Paths.get(USER_HOME).resolve(CONFIG_DIR_NAME);
        } else {
            CONFIG_PATH = Paths.get(".").resolve(CONFIG_DIR_NAME);
        }
    }

    /**
     * Hide constructor
     */
    protected ConfigBase() {

    }

    /**
     * 設定ファイル用ディレクトリへのフルパスを返します
     *
     * @param filename 相対パス
     * @return 絶対パス
     */
    static public Path getConfigFilePath(Path filename) {
        return CONFIG_PATH.resolve(filename);
    }

    /**
     * .jflがなければ、resourcesの .jfl 以下を CONFIG_PATH にコピーします
     */
    static public void copyResourceFilesIfNotExists() throws IOException, URISyntaxException {
        if (!Files.exists(CONFIG_PATH)) {
            copyResourceFiles();
        }
    }

    /**
     * resourcesの .jfl 以下を CONFIG_PATH にコピーします
     */
    static public void copyResourceFiles() throws IOException, URISyntaxException {
        final Path resourceRoot = Jfl2ResouceUtils.getPath(ConfigBase.class, "/" + CONFIG_DIR_NAME);
        final ResourceBundle rc = ResourceBundleManager.getMessage();
        try (Stream<Path> stream = Files.walk(resourceRoot)) {
            stream.forEach(path -> {
                try {
                    Path configDirectory = Jfl2ResouceUtils.getPath(ConfigBase.class, "/" + CONFIG_DIR_NAME);
                    Path dst = CONFIG_PATH.resolve(configDirectory.relativize(path));
                    // ファイルが存在する場合、ディレクトリならなにもしない
                    if (!Files.exists(dst) || !Files.isDirectory(path)) {
                        log.debug(rc.getString("Copy {} to {}"), path, dst);
                        Files.copy(path, dst, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        } catch (RuntimeException e) {
            if (IOException.class.isInstance(e.getCause())) {
                throw (IOException) e.getCause();
            }
        }
    }


    /**
     * 設定ファイルを読み込みます
     *
     * @param relativePath 設定ファイルディレクトリからの相対パス
     * @param clazz        出力クラス
     * @param charset      Charset
     */
    static protected <T extends SerializeBase> T load(Path relativePath, Class<T> clazz, Charset charset, OutputType type) throws IOException, IllegalAccessException, InstantiationException {
        log.info("Load configuration file : {}", relativePath);
        Path fullPath = getConfigFilePath(relativePath);
        log.debug("Load config file : {}", fullPath);
        if (Files.exists(fullPath)) {
            try (BufferedReader reader = Files.newBufferedReader(fullPath, charset)) {
                return from(reader, clazz, type);
            }
        }
        throw new IOException(String.format("%S is not found.", fullPath));
    }

    /**
     * 存在するか調べる
     */
    public boolean exists() {
        return Files.exists(getConfigFilePath(getFilePath()));
    }

    /**
     * 設定ファイルを読み込みます
     */
    public <T extends SerializeBase> T loadFile() throws IOException, IllegalAccessException, InstantiationException {
        return ConfigBase.load(getFilePath(), (Class<T>) this.getClass(), getFileCharset(), getOutputType());
    }

    /**
     * ファイル形式はXML
     *
     * @return xml
     */
    @Override
    public abstract OutputType getOutputType();

    /**
     * ファイル名取得
     *
     * @return Path
     */
    public abstract Path getFilePath();

    /**
     * Charset名取得
     *
     * @return Path
     */
    public abstract Charset getFileCharset();

    /**
     * ファイル出力
     */
    public void write() {
        Path fullPath = getConfigFilePath(getFilePath());

        log.info("Write configuration file : {}", fullPath);
        if (Jfl2FileUtils.createParentDirectories(fullPath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(fullPath, getFileCharset())) {
                writer.write(this.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
