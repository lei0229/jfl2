package org.jfl2.file;

import javafx.collections.ObservableList;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Pathに属性情報を追加
 * symlink 関連情報ページ http://docs.oracle.com/cd/E26537_01/tutorial/essential/io/links.html#detect
 */
@Slf4j
@EqualsAndHashCode(of = "path")
public class Jfl2Path {
    private static final String VIEW_OWNER = "owner";
    private static final String VIEW_DOS = "dos";
    private static final String VIEW_ACL = "acl";
    private static final String VIEW_BASIC = "basic";
    private static final String VIEW_USER = "user";
    private static final String VIEW_POSIX = "posix";

    private static final String ATTR_IS_DIRECTORY = "isDirectory";
    private static final String ATTR_IS_SYMBOLIC_LINK = "isSymbolicLink";
    private static final String ATTR_LAST_MODIFIED_TIME = "lastModifiedTime";
    private static final String ATTR_LAST_ACCESS_TIME = "lastAccessTime";
    private static final String ATTR_LAST_CREATION_TIME = "lastCreationTime";

    private static final String ATTR_SIZE = "size";
    private static final String ATTR_READONLY = "readonly";
    private static final String ATTR_HIDDEN = "hidden";
    private static final String ATTR_SYSTEM = "system";
    private static final String ATTR_ARCHIVE = "archive";
    private static final String ATTR_PERMISSION = "permission";

    private static final String SEPARATOR = ",";
    private static final String COMMON_ATTRS = String.join(SEPARATOR, ATTR_SIZE, ATTR_IS_DIRECTORY, ATTR_IS_SYMBOLIC_LINK, ATTR_LAST_MODIFIED_TIME);
    private static final String DOS_ATTRS = "dos:" + String.join(SEPARATOR, ATTR_ARCHIVE, ATTR_HIDDEN, ATTR_SYSTEM, ATTR_READONLY, COMMON_ATTRS);
    private static final String MAC_ATTRS = "posix:" + String.join(SEPARATOR, ATTR_PERMISSION, COMMON_ATTRS);
    private static final String POSIX_ATTRS = "posix:" + String.join(SEPARATOR, ATTR_PERMISSION, COMMON_ATTRS);

    // Attribute ALL
    static final public String CSS_ATTR_DIR           = "attr-dir";
    static final public String CSS_ATTR_SYMBOLIC_LINK = "attr-symlink";
    // Attribute Windows
    static final public String CSS_ATTR_HIDDEN        = "attr-hidden";
    static final public String CSS_ATTR_READONLY      = "attr-readonly";
    static final public String CSS_ATTR_ARCHIVE       = "attr-archive";
    // Attribute Linux & Mac
    static final public String CSS_ATTR_EXECUTE       = "attr-execute";
    static final public String CSS_ATTR_WRITE         = "attr-write";
    static final public String CSS_ATTR_READ          = "attr-read";
    // Attributes list
    static final public List<String> CSS_WIN_LIST = Arrays.asList(CSS_ATTR_DIR, CSS_ATTR_SYMBOLIC_LINK, CSS_ATTR_HIDDEN, CSS_ATTR_READONLY, CSS_ATTR_ARCHIVE);
    static final public List<String> CSS_MAC_LIST = Arrays.asList(CSS_ATTR_DIR, CSS_ATTR_SYMBOLIC_LINK, CSS_ATTR_EXECUTE, CSS_ATTR_WRITE, CSS_ATTR_READ);
    static final public List<String> CSS_LINUX_LIST = Arrays.asList(CSS_ATTR_DIR, CSS_ATTR_SYMBOLIC_LINK, CSS_ATTR_EXECUTE, CSS_ATTR_WRITE, CSS_ATTR_READ);

    static {
        Map<String, Class<? extends FileAttributeView>> hash = new HashMap<>();
        hash.put("owner", FileOwnerAttributeView.class);
        hash.put("dos", DosFileAttributeView.class);
        hash.put("acl", AclFileAttributeView.class);
        hash.put("basic", BasicFileAttributeView.class);
        hash.put("user", UserDefinedFileAttributeView.class);
        hash.put("posix", PosixFileAttributeView.class);
//        str2FileAttributeViewClass = Collections.unmodifiableMap(hash);
    }

    /**
     * 保持するPath
     */
    private final Path path;
    /**
     * 属性Map
     */
    private Map<String, Object> attributeMap;
    /**
     * FileAttributeViewのクラスマップ作成
     */
    private Map<String, Class<? extends FileAttributeView>> str2FileAttributeViewClass;

    /**
     * constructor
     *
     * @param path Specific value
     */
    public Jfl2Path(String path) {
        this.path = Paths.get(path);
    }

    /**
     * constructor
     *
     * @param path Specific value
     */
    public Jfl2Path(Path path) {
        this.path = path;
    }

    /**
     * List&lt;Jfl2Path&gt; を List&lt;String&gt; に変換します
     *
     * @param jfl2PathList 変換元リスト
     * @return 変換後リスト
     */
    static public List<String> toStringList(List<Jfl2Path> jfl2PathList) {
        return jfl2PathList.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * 文字列表現
     *
     * @return
     */
    @Override
    public String toString() {
        return path.toString();
    }

    /**
     * まだ読み込んでいなければ、属性情報を読み込む
     *
     * @throws IOException
     */
    public void readAttributesIfNotRead() throws IOException {
        if (attributeMap == null) {
            readAttributes();
        }
    }

    /**
     * 属性情報を再読み込み
     *
     * @throws IOException
     */
    public void readAttributes() throws IOException {
        if (SystemUtils.IS_OS_WINDOWS) {
            attributeMap = Files.readAttributes(path, DOS_ATTRS, LinkOption.NOFOLLOW_LINKS);
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            attributeMap = Files.readAttributes(path, MAC_ATTRS, LinkOption.NOFOLLOW_LINKS);
        } else {
            attributeMap = Files.readAttributes(path, POSIX_ATTRS, LinkOption.NOFOLLOW_LINKS);
        }
        log.debug(attributeMap.toString());
    }

    /**
     * listにcss用クラスをセットする
     * @return this
     */
    public Jfl2Path setCss(List<String> list) throws IOException {
        if (isDirectory()) list.add(Jfl2Path.CSS_ATTR_DIR);
        if (isSymlink()) list.add(Jfl2Path.CSS_ATTR_SYMBOLIC_LINK);

        if (SystemUtils.IS_OS_WINDOWS) {
            if (isArchive())  list.add(Jfl2Path.CSS_ATTR_ARCHIVE);
            if (isReadonly())  list.add(Jfl2Path.CSS_ATTR_READONLY);
            if (isHidden())  list.add(Jfl2Path.CSS_ATTR_HIDDEN);
        }else if(SystemUtils.IS_OS_MAC_OSX){
        }else if(SystemUtils.IS_OS_LINUX){
        }
        return this;
    }

    /**
     * 各OS用の属性を返す
     * @return
     */
    static public List<String> getRemoveCssList(){
        if (SystemUtils.IS_OS_WINDOWS) {
            return CSS_WIN_LIST;
        }else if(SystemUtils.IS_OS_MAC_OSX){
            return CSS_MAC_LIST;
        }else if(SystemUtils.IS_OS_LINUX){
            return CSS_LINUX_LIST;
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * ファイルサイズ取得
     *
     * @return
     */
    public LongHasString getSize() throws IOException {
        readAttributesIfNotRead();
        Long longValue = (Long) attributeMap.get(ATTR_SIZE);
        if((Boolean)attributeMap.get(ATTR_IS_DIRECTORY)){
            return new LongHasString(longValue, "<dir>");
        }
        return new LongHasString(longValue);
    }

    /**
     * パーミッション取得
     * @return
     * @throws IOException
     */
    public FileAttribute<Set<PosixFilePermission>> getPermission() throws IOException{
        readAttributesIfNotRead();
        return (FileAttribute<Set<PosixFilePermission>>) attributeMap.get(ATTR_PERMISSION);
    }

    /**
     * Directoryフラグ取得
     *
     * @return
     * @throws IOException
     */
    public Boolean isDirectory() throws IOException {
        readAttributesIfNotRead();
        return (Boolean) attributeMap.get(ATTR_IS_DIRECTORY);
    }

    /**
     * Archiveフラグ取得
     *
     * @return
     * @throws IOException
     */
    public Boolean isArchive() throws IOException {
        readAttributesIfNotRead();
        return (Boolean) attributeMap.get(ATTR_ARCHIVE);
    }

    /**
     * Systemフラグ取得
     *
     * @return
     * @throws IOException
     */
    public Boolean isSystem() throws IOException {
        readAttributesIfNotRead();
        return (Boolean) attributeMap.get(ATTR_SYSTEM);
    }

    /**
     * Hiddenフラグ取得
     *
     * @return
     * @throws IOException
     */
    public Boolean isHidden() throws IOException {
        readAttributesIfNotRead();
        return (Boolean) attributeMap.get(ATTR_HIDDEN);
    }

    /**
     * Readonlyフラグ取得
     *
     * @return
     * @throws IOException
     */
    public Boolean isReadonly() throws IOException {
        readAttributesIfNotRead();
        return (Boolean) attributeMap.get(ATTR_READONLY);
    }

    /**
     * symlinkフラグ取得
     *
     * @return
     * @throws IOException
     */
    public Boolean isSymlink() throws IOException {
        readAttributesIfNotRead();
        return (Boolean) attributeMap.get(ATTR_IS_SYMBOLIC_LINK);
    }

    /**
     * 選択時文字列
     * @return
     */
    public String getMark(){
        return "*";
    }

    /**
     * ファイル名取得
     *
     * @return
     * @throws IOException
     */
    public String getName() {
        return path.getFileName().toString();
    }

    /**
     * Fetch path
     *
     * @return path
     * @throws IOException
     */
    public Path getPath() {
        return path;
    }

    //
    // owner は不要かも
    //

    /**
     * 所有者取得
     *
     * @return
     * @throws IOException
     */
    public Optional<UserPrincipal> getOwner() throws IOException {
        if (hasFileOwnerAttribute()) {
            return Optional.of(Files.getOwner(path, LinkOption.NOFOLLOW_LINKS));
        }
        return Optional.empty();
    }

    /**
     * FiliSystemが所有者情報を持っていればtrue
     * @return
     */
    public boolean hasFileOwnerAttribute() {
        return this.path.getFileSystem().supportedFileAttributeViews().contains(VIEW_OWNER);
    }

}
