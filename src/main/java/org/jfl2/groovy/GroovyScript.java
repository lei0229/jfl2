package org.jfl2.groovy;

import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.tools.FileSystemCompiler;
import org.codehaus.groovy.tools.GroovyClass;
import org.jfl2.core.conf.ConfigBase;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.jfl2.core.util.Jfl2FileUtils.createDirectoriesIfNotExists;
import static org.jfl2.core.util.Jfl2FileUtils.removeDirectory;

/**
 * Groovyファイルのコンパイル状態をキャッシュして呼び出します。
 */
@Slf4j
public class GroovyScript {
    /**
     * Groovyファイル置き場
     */
    public static final Path SOURCE_ROOT = ConfigBase.getConfigFilePath(Paths.get("groovy"));
    /**
     * クラスファイルの入れ先
     */
    public static final Path CACHE_ROOT = ConfigBase.getConfigFilePath(Paths.get("groovy_cache"));
    /**
     * md5ファイルの入れ先
     */
    public static final Path MD5_ROOT = ConfigBase.getConfigFilePath(Paths.get("groovy_md5"));
    /**
     * Static Groovy script object
     */
    private static GroovyScriptEngine staticGse;
    /**
     * Groovy script object
     */
    private final Script groovyScript;
    /**
     * Groovy script engine
     */
    private GroovyScriptEngine gse;
    /**
     * Groovy script file
     */
    final private GroovyFile file;

    /**
     * Constructor
     *
     * @param path 相対パスファイル名
     * @throws IOException
     */
    public GroovyScript(Path path) throws Exception {
        this(path, null);
    }

    /**
     * Constructor
     *
     * @param path   相対パスファイル名
     * @param engine Script生成クラス
     * @throws Exception
     */
    public GroovyScript(Path path, GroovyScriptEngine engine) throws Exception {
        init(engine);

        file = new GroovyFile(path);
        file.readCache();
        if (file.canNotUseCache()) {
            file.removeCache();
            file.compile();
            file.writeCache();
        }
        groovyScript = file.load();
    }

    /**
     * ファイル名を取得
     *
     * @return
     */
    public String getName() {
        return file.getName();
    }

    /**
     * md5ファイルとキャッシュされたclassファイルをフォルダごと全て消します
     *
     * @throws IOException
     */
    static public void clearAllCaches() throws IOException {
        removeDirectory(CACHE_ROOT);
        removeDirectory(MD5_ROOT);
        staticGse = null;
    }

    /**
     * デフォルトクラスパスを持つ GroovyScriptEngine を返す
     *
     * @return object
     * @throws IOException
     */
    static public GroovyScriptEngine getDefaultEngine() throws IOException {
        createDirectoriesIfNotExists(CACHE_ROOT);
        if (staticGse == null) {
            staticGse = new GroovyScriptEngine((CACHE_ROOT.toString()));
        }
        return staticGse;
    }

    /**
     * スクリプトファイル全部取得する
     *
     * @return
     * @throws IOException
     */
    static public HashMap<String, GroovyScript> getAllScripts() throws IOException {
        HashMap<String, GroovyScript> result = new HashMap<>();
        if (!Files.exists(SOURCE_ROOT)) {
            return result;
        }
        try (Stream<Path> stream = Files.list(SOURCE_ROOT)) {
            stream.forEach(path -> {
                try {
                    GroovyScript script = new GroovyScript(path.getFileName());
                    result.put(script.getName(), script);
                } catch (Exception e) {
                    log.warn("Groovy script file load is failed. " + path, e);
                }
            });

        }
        return result;
    }

    /**
     * Running method
     *
     * @return
     */
    public Object run() {
        Binding bind = new Binding();
        bind.setVariable("varstr", "Stringの変数");
        return run(bind);
    }

    /**
     * Running method
     *
     * @return
     */
    public Object run(Binding bind) {
        groovyScript.setBinding(bind);
        return groovyScript.run();
    }

    /**
     * GroovyScriptEngine オブジェクト作成
     * ディレクトリが無ければ作成する
     *
     * @param engine スクリプト生成クラス
     * @throws IOException
     */
    private void init(GroovyScriptEngine engine) throws IOException {
        createDirectoriesIfNotExists(SOURCE_ROOT);
        createDirectoriesIfNotExists(MD5_ROOT);
        createDirectoriesIfNotExists(CACHE_ROOT);

        if (engine == null) {
            gse = getDefaultEngine();
        } else {
            gse = engine;
        }
    }

    /**
     * キャッシュファイル用オブジェクト
     */
    @ToString
    class GroovyFile {
        public static final String EXTENSION_MD5 = ".md5";
        public static final String EXTENSION_GROOVY = ".groovy";
        private final Path orgPath;
        private final Path sourceFile;
        private final Path md5File;
        private final String md5hex;
        private final List<Path> classFiles = new ArrayList<>();
        private String fullClassName;
        private String cachedMd5hex;
        private String mainClass;

        public GroovyFile(Path path) throws IOException {
            orgPath = path;
            sourceFile = SOURCE_ROOT.resolve(orgPath);
            md5File = MD5_ROOT.resolve(path.getFileName() + EXTENSION_MD5);
            md5hex = getMd5();
        }

        /**
         * SOURCE_ROOTからの相対パスを返す
         * 末尾が.groovyだった場合は除去する
         *
         * @return
         */
        public String getName() {
            if (orgPath == null) {
                return "";
            }
            return orgPath.toString().replaceAll(EXTENSION_GROOVY + "$", "");
        }

        /**
         * キャッシュファイルを保存
         *
         * @throws IOException
        public void save(List<GroovyClass> classList) throws IOException {
        String basename = FilenameUtils.getBaseName(String.valueOf(sourceFile));
        try (BufferedWriter out = Files.newBufferedWriter(md5File)) {
        out.write(md5hex);
        out.newLine();
        for (GroovyClass gClass : classList) {
        if (gClass.getName().endsWith(basename)) {
        out.write(gClass.getName());
        out.newLine();
        break;
        }
        }
        log.info("--------- output cache data to {}", md5File);
        }
        }
         */

        /**
         * sourceFileからclassFileを作成します
         *
         * @throws Exception
         */
        public void compile() throws Exception {
            CompilerConfiguration conf = gse.getConfig();
            conf.setTargetDirectory(CACHE_ROOT.toFile());
            CompilationUnit cu = new CompilationUnit(conf);
            FileSystemCompiler cc = new FileSystemCompiler(conf, cu);
            cc.compile(new File[]{sourceFile.toFile()});
            for (Object cls : cu.getClasses()) {
                String clazz = ((GroovyClass) cls).getName();
                String path = clazz.replace(".", FileSystems.getDefault().getSeparator());
                log.debug("----- compile class : {}", clazz);
                if (path.endsWith(FilenameUtils.getBaseName(sourceFile.getFileName().toString()))) {
                    mainClass = clazz;
                    classFiles.add(0, Paths.get(path));
                } else {
                    classFiles.add(Paths.get(path));
                }
            }
        }

        /**
         * fileの内容からmd5を生成して返す
         *
         * @return md5のString
         * @throws IOException
         */
        private String getMd5() throws IOException {
            try (InputStream in = new BufferedInputStream(Files.newInputStream(sourceFile))) {
                return DigestUtils.md5Hex(in);
            }
        }

        /**
         * If can use cache data then return false
         *
         * @return true/false
         * @throws IOException
         */
        public boolean canNotUseCache() throws IOException {
            if (md5hex == null) {
                throw new IllegalStateException(String.format("Required fields is empty. ", this));
            }
            return !md5hex.equals(cachedMd5hex);
        }

        /**
         * Fetch cached data
         *
         * @return
         * @throws IOException
         */
        private void readCache() throws IOException {
            if (Files.exists(md5File)) {
                log.debug("Read md5 from {}", md5File);
                try (BufferedReader in = Files.newBufferedReader(md5File)) {
                    cachedMd5hex = in.readLine();
                    mainClass = in.readLine();
                    mainClass = mainClass.replace(FileSystems.getDefault().getSeparator(), ".");
                    classFiles.add(Paths.get(mainClass));
                    String line;
                    while ((line = in.readLine()) != null) {
                        classFiles.add(Paths.get(line));
                    }
                }
            }
        }

        /**
         * Output cache data
         *
         * @throws IOException
         */
        private void writeCache() throws IOException {
            if (md5File == null || StringUtils.isEmpty(md5hex) || StringUtils.isEmpty(mainClass)) {
                throw new IllegalStateException(String.format("Required fields is empty. ", this));
            }
            try (BufferedWriter out = Files.newBufferedWriter(md5File)) {
                out.write(md5hex);
                out.newLine();
                for (Path file : classFiles) {
                    out.write(file.toString());
                    out.newLine();
                }
                log.info("--------- output md5 to {}", md5File);
            }
        }

        /**
         * Remove cached files
         */
        public void removeCache() throws IOException {
            if (md5File != null) {
                Files.deleteIfExists(md5File);
            }
            for (Path file : classFiles) {
                Files.deleteIfExists(CACHE_ROOT.resolve(file.resolveSibling(file.getFileName().toString() + ".class")));
            }
            classFiles.clear();
        }

        /**
         * Load class
         *
         * @return Script object.
         * @throws ClassNotFoundException
         */
        private Script load() throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
            Class<?> clazz = gse.getGroovyClassLoader().loadClass(mainClass);
            log.info("new groovy class = {}", clazz.toString());
            Object newObject = clazz.newInstance();
            if (Script.class.isInstance(newObject)) {
                return ((Script) newObject);
            }
            throw new RuntimeException("Class is not Script");
        }
    }

}
