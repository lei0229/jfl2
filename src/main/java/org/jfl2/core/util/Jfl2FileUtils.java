package org.jfl2.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.StrBuilder;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class Jfl2FileUtils {

    /**
     * 親ディレクトリを作成する
     *
     * @param srcPath file of directory
     * @return success : true / failure : false
     */
    static public boolean createParentDirectories(Path srcPath) {
        log.info("Create parent directory : ", srcPath);
        Path parentDir = srcPath.getParent();
        if (!Files.exists(parentDir)) {
            log.info("Create directory : {}", parentDir);
            try {
                Files.createDirectories(parentDir);
                return true;
            } catch (IOException e) {
                log.error("Could not create directory : " + parentDir, e);
            }
        } else {
            if (Files.isDirectory(parentDir)) {
                log.info("Already created : ", srcPath);
                return true;
            } else {
                log.info("Already created but this is not directory : ", srcPath);
            }
        }
        return false;
    }

    /**
     * 存在しなければディレクトリを作成
     *
     * @param path 作成するディレクトリ
     * @return path
     * @throws IOException
     */
    static public Path createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path)) {
            return path;
        }
        return Files.createDirectories(path);
    }

    /**
     * フォルダを子供ごと削除
     *
     * @param deletePath
     * @return Return true if success.
     */
    static public boolean removeDirectory(Path deletePath) throws IOException {
        if (deletePath != null && !Files.exists(deletePath)) {
            return false;
        }
        Files.walkFileTree(deletePath, new RemoveFileVisitor());
        return true;
    }

}

@Slf4j
class RemoveFileVisitor implements FileVisitor<Path> {
    protected int indentSize;

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        print("preVisitDirectory : " + dir.getFileName());
        this.indentSize++;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        print("visitFile : " + file.getFileName());
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        this.indentSize--;
        print("postVisitDirectory : " + dir.getFileName());
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        String error = String.format(" [exception=%s, message=%s]", exc.getClass(), exc.getMessage());

        print("visitFileFailed : " + file.getFileName() + error);

        return FileVisitResult.CONTINUE;
    }

    protected void print(String message) {
        log.debug(new StrBuilder().appendPadding(this.indentSize, ' ').append(message).toString());
    }
}
