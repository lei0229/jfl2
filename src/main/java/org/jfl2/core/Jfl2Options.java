package org.jfl2.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jfl2.file.sort.SortedDirPosition;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * 設定とか保持したいと思われるクラス
 */
@Slf4j
public class Jfl2Options {
    @Option(name = "-f", aliases = "--force-initialization", required = false, usage = "Setting files are forced initialization.") @Getter @Setter
    private boolean forceInitialization = false;

    @Option(name = "-s", aliases = "--sorted-dir-position", required = false, usage = "Position of sorted directories. top/bottom/natural") @Getter @Setter
    private SortedDirPosition sortedDirectoryPosition = SortedDirPosition.TOP;

    /**
     * parser
     */
    @Getter
    private CmdLineParser parser;

    /**
     * constructor
     */
    public Jfl2Options() {
        parser = new CmdLineParser(this);
    }

    /**
     * parse
     *
     * @param args
     * @return
     * @throws CmdLineException
     */
    public Jfl2Options parse(Collection<String> args) throws CmdLineException {
        parser.parseArgument(args);
        return this;
    }

    /**
     * 使い方をStringで取得
     *
     * @return
     */
    public String getUsage() {
        try (Writer out = new StringWriter()) {
            parser.printUsage(out, ResourceBundleManager.getMessage());
            return out.toString();
        } catch (IOException e) {
            log.error("Usage message creation failed.", e);
        }
        return "";
    }
}
