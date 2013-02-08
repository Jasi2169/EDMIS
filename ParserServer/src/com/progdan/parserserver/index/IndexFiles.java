package com.progdan.parserserver.index;

import java.util.*;
import java.io.*;

import com.progdan.logengine.*;
import com.progdan.parserserver.index.converters.*;
import com.progdan.parserserver.server.*;

public class IndexFiles extends Thread {
    private static Logger logger = Logger.getLogger(IndexFiles.class.getName());
    private static String reppath;
    private static String cmdpath;
    private LinkedList index;
    private IndexController control;
    private Server server;
    public IndexFiles(LinkedList index, IndexController control, Server server) {
        logger.debug(">>> Start of IndexFiles.IndexFiles()***");
        this.index = index;
        this.control = control;
        this.server = server;
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/" + "db.properties"));
            reppath = props.getProperty("reppath", "C:\\EDMIS");
            cmdpath = props.getProperty("cmdpath", "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug("<<< End of IndexFiles.IndexFiles()***");
    }

    public void run() {
        logger.debug(">>> Start of IndexFiles.run()***");
        Hashtable task;
        Converter conv;
        boolean result;
        while (index.size() != 0) {
            task = (Hashtable) index.removeFirst();
            result = false;
            String format = (String) task.get("format");
            String id = (String) task.get("id");
            String parsed = control.fileParsed(id);
            if ((parsed == null) || (parsed.compareTo("false") != 0)) {
                if (format.compareTo("txt") == 0) {
                    conv = new TXT2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("pdf") == 0) {
                    conv = new PDF2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if ((format.compareTo("htm") == 0) ||
                    (format.compareTo("html") == 0)) {
                    conv = new HTML2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("chm") == 0) {
                    conv = new CHM2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("ppt") == 0) {
                    conv = new PPT2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("doc") == 0) {
                    conv = new DOC2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("xls") == 0) {
                    conv = new XLS2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("rtf") == 0) {
                    conv = new RTF2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("ps") == 0) {
                    conv = new PS2Text(reppath, cmdpath);
                    result = conv.index(task);
                }

                if (format.compareTo("c") == 0) {
                    conv = new C2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("cpp") == 0) {
                    conv = new CPP2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("h") == 0) {
                    conv = new H2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("java") == 0) {
                    conv = new JAVA2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
                if (format.compareTo("sql") == 0) {
                    conv = new SQL2Text(reppath, cmdpath);
                    result = conv.index(task);
                }
            }
            if (result) {
                control.completeTask(task);
                control.parseFile(id);
            } else {
                control.noParseFile(id);
            }
            server.updateGUI();
        }
        server.removeIndexer();
        logger.debug("<<< End of IndexFiles.run()***");
    }
}
