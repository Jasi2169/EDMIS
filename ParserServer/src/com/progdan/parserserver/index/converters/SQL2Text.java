package com.progdan.parserserver.index.converters;

import com.progdan.logengine.Logger;
import java.io.File;

public class SQL2Text extends Converter{
    private static Logger logger = Logger.getLogger(TXT2Text.class.getName());
    public SQL2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of SQL2Text.TXT2Text()***");
        logger.debug("<<< End of SQL2Text.TXT2Text()***");
    }
    public void convertFile(String id) {
        logger.debug(">>> Start of SQL2Text.convertFile()***");
        File source = new File(reppath, id + ".sql");
        File target = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
        source.renameTo(target);
        source.delete();
        logger.debug("<<< End of SQL2Text.convertFile()***");
    }
}
