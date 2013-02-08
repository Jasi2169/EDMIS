package com.progdan.parserserver.index.converters;

import java.io.*;

import com.progdan.logengine.*;

public class TXT2Text extends Converter {
    private static Logger logger = Logger.getLogger(TXT2Text.class.getName());
    public TXT2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of TXT2Text.TXT2Text()***");
        logger.debug("<<< End of TXT2Text.TXT2Text()***");
    }

    public void convertFile(String id) {
        logger.debug(">>> Start of TXT2Text.convertFile()***");
        File source = new File(reppath, id + ".txt");
        File target = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
        source.renameTo(target);
        source.delete();
        logger.debug("<<< End of TXT2Text.convertFile()***");
    }
}
